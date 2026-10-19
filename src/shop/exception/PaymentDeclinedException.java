package shop.exception;

public class PaymentDeclinedException extends SubsystemException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
