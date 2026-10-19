package shop.exception;

/** Base class for failures raised by the subsystem services (hidden from clients by the Facade). */
public class SubsystemException extends Exception {
    public SubsystemException(String message) {
        super(message);
    }
}
