package shop.exception;

/** The only exception clients of the OrderFacade need to know about. */
public class OrderFailedException extends RuntimeException {

    private final String orderId;

    public OrderFailedException(String orderId, String reason) {
        super("Order " + orderId + " failed: " + reason);
        this.orderId = orderId;
    }

    public String orderId() {
        return orderId;
    }
}
