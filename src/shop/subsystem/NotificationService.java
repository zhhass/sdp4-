package shop.subsystem;

/** Sends messages to customers (simulated by console output). */
public class NotificationService {

    public void send(String email, String subject) {
        System.out.println("  [Notification] to " + email + ": " + subject);
    }
}
