package shop;

import shop.exception.OrderFailedException;
import shop.facade.OrderFacade;
import shop.model.Cart;
import shop.model.Customer;
import shop.model.Order;
import shop.model.Product;

import java.math.BigDecimal;

/** Client: talks to the OrderFacade only. */
public class Main {

    public static void main(String[] args) {
        OrderFacade shop = new OrderFacade();

        Product laptop = new Product("LAPTOP", "Laptop", new BigDecimal("1200"));
        Product mouse = new Product("MOUSE", "Mouse", new BigDecimal("15"));

        Customer alice = new Customer("c-alice", "Alice", "alice@mail.com", "Astana, Kabanbay Batyr 1");
        Customer bob = new Customer("c-bob", "Bob", "bob@mail.com", "Astana, Mangilik El 5");
        Customer carol = new Customer("c-alice", "Carol", "carol@mail.com", "");

        section("1. Successful order");
        Order order = shop.placeOrder(new Cart().add(laptop, 1).add(mouse, 2), alice);
        System.out.println("Result: " + order + " | laptops left: " + shop.stockOf("LAPTOP"));

        section("2. Out of stock (nothing is charged)");
        tryPlace(shop, new Cart().add(laptop, 10), alice);

        section("3. Payment declined -> reserved stock is released");
        tryPlace(shop, new Cart().add(laptop, 1), bob);
        System.out.println("Laptops left: " + shop.stockOf("LAPTOP") + " (unchanged)");

        section("4. Shipping fails -> payment refunded AND stock released");
        tryPlace(shop, new Cart().add(mouse, 1), carol);
        System.out.println("Mice left: " + shop.stockOf("MOUSE") + " (unchanged)");

        section("5. Cancel the first order");
        shop.cancelOrder(order.orderId());
        System.out.println("Result: " + order + " | laptops left: " + shop.stockOf("LAPTOP"));
    }

    private static void tryPlace(OrderFacade shop, Cart cart, Customer customer) {
        try {
            shop.placeOrder(cart, customer);
        } catch (OrderFailedException e) {
            System.out.println("Client sees: " + e.getMessage());
        }
    }

    private static void section(String title) {
        System.out.println("\n=== " + title + " ===");
    }
}
