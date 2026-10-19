package shop.exception;

public class OutOfStockException extends SubsystemException {
    public OutOfStockException(String message) {
        super(message);
    }
}
