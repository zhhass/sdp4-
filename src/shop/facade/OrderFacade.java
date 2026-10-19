package shop.facade;

import shop.exception.OrderFailedException;
import shop.exception.SubsystemException;
import shop.model.Cart;
import shop.model.Customer;
import shop.model.Order;
import shop.subsystem.InventoryService;
import shop.subsystem.NotificationService;
import shop.subsystem.PaymentProcessor;
import shop.subsystem.ShippingService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Facade: gives clients two simple operations (placeOrder / cancelOrder) and hides the
 * order in which inventory, payment, shipping and notification must be coordinated,
 * including compensation (rollback) when a step fails.
 */
public class OrderFacade {

    private final InventoryService inventory;
    private final PaymentProcessor payments;
    private final ShippingService shipping;
    private final NotificationService notifications;

    private final Map<String, Order> orders = new HashMap<>();
    private final AtomicInteger orderSequence = new AtomicInteger(1000);

    /** Default wiring so that simple clients never touch subsystem classes. */
    public OrderFacade() {
        this(InventoryService.withDemoStock(), PaymentProcessor.withDemoAccounts(),
                new ShippingService(), new NotificationService());
    }

    /** Wiring for advanced clients (and tests) that supply their own subsystem instances. */
    public OrderFacade(InventoryService inventory, PaymentProcessor payments,
                       ShippingService shipping, NotificationService notifications) {
        this.inventory = inventory;
        this.payments = payments;
        this.shipping = shipping;
        this.notifications = notifications;
    }

    public Order placeOrder(Cart cart, Customer customer) {
        requireValid(cart, customer);
        String orderId = "ORD-" + orderSequence.incrementAndGet();
        System.out.println("Placing " + orderId + " for " + customer.name());

        Deque<Runnable> compensations = new ArrayDeque<>();
        try {
            inventory.reserve(orderId, cart.items());
            compensations.push(() -> inventory.release(orderId));

            String transactionId = payments.charge(customer.id(), cart.total());
            compensations.push(() -> payments.refund(transactionId));

            String trackingNumber = shipping.createShipment(orderId, customer.address());
            compensations.push(() -> shipping.cancelShipment(trackingNumber));

            inventory.commit(orderId);
            Order order = new Order(orderId, customer, cart.items(), cart.total(),
                    transactionId, trackingNumber);
            orders.put(orderId, order);
            notifications.send(customer.email(), "Order " + orderId + " confirmed");
            return order;
        } catch (SubsystemException e) {
            System.out.println("  !! " + e.getMessage() + " -> rolling back");
            rollback(compensations);
            throw new OrderFailedException(orderId, e.getMessage());
        }
    }

    public void cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderFailedException(orderId, "order not found");
        }
        if (order.status() == Order.Status.CANCELLED) {
            throw new OrderFailedException(orderId, "order already cancelled");
        }
        System.out.println("Cancelling " + orderId);
        shipping.cancelShipment(order.trackingNumber());
        payments.refund(order.transactionId());
        inventory.restock(order.items());
        order.markCancelled();
        notifications.send(order.customer().email(), "Order " + orderId + " cancelled");
    }

    /** Simple read-only query so clients can see the effect of an order without subsystem access. */
    public int stockOf(String sku) {
        return inventory.available(sku);
    }

    private void rollback(Deque<Runnable> compensations) {
        while (!compensations.isEmpty()) {
            compensations.pop().run();
        }
    }

    private void requireValid(Cart cart, Customer customer) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart must contain at least one item");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
    }
}
