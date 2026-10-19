package shop.subsystem;

import shop.exception.PaymentDeclinedException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Charges and refunds customer accounts. */
public class PaymentProcessor {

    private record Transaction(String customerId, BigDecimal amount) { }

    private final Map<String, BigDecimal> balances = new HashMap<>();
    private final Map<String, Transaction> transactions = new HashMap<>();

    public static PaymentProcessor withDemoAccounts() {
        PaymentProcessor processor = new PaymentProcessor();
        processor.openAccount("c-alice", new BigDecimal("5000"));
        processor.openAccount("c-bob", new BigDecimal("20"));
        return processor;
    }

    public void openAccount(String customerId, BigDecimal balance) {
        balances.put(customerId, balance);
    }

    public String charge(String customerId, BigDecimal amount) throws PaymentDeclinedException {
        BigDecimal balance = balances.getOrDefault(customerId, BigDecimal.ZERO);
        if (balance.compareTo(amount) < 0) {
            throw new PaymentDeclinedException("Payment declined: insufficient funds (needed "
                    + amount + ", balance " + balance + ")");
        }
        balances.put(customerId, balance.subtract(amount));
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        transactions.put(transactionId, new Transaction(customerId, amount));
        System.out.println("  [Payment] charged " + amount + " (" + transactionId + ")");
        return transactionId;
    }

    public void refund(String transactionId) {
        Transaction transaction = transactions.remove(transactionId);
        if (transaction != null) {
            balances.merge(transaction.customerId(), transaction.amount(), BigDecimal::add);
            System.out.println("  [Payment] refunded " + transaction.amount() + " (" + transactionId + ")");
        }
    }
}
