package shop.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    public enum Status { PLACED, CANCELLED }

    private final String orderId;
    private final Customer customer;
    private final List<CartItem> items;
    private final BigDecimal total;
    private final String transactionId;
    private final String trackingNumber;
    private Status status = Status.PLACED;

    public Order(String orderId, Customer customer, List<CartItem> items, BigDecimal total,
                 String transactionId, String trackingNumber) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.total = total;
        this.transactionId = transactionId;
        this.trackingNumber = trackingNumber;
    }

    public String orderId() { return orderId; }
    public Customer customer() { return customer; }
    public List<CartItem> items() { return items; }
    public BigDecimal total() { return total; }
    public String transactionId() { return transactionId; }
    public String trackingNumber() { return trackingNumber; }
    public Status status() { return status; }

    public void markCancelled() { this.status = Status.CANCELLED; }

    @Override
    public String toString() {
        return "Order{" + orderId + ", " + status + ", total=" + total
                + ", tracking=" + trackingNumber + "}";
    }
}
