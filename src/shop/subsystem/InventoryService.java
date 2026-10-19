package shop.subsystem;

import shop.exception.OutOfStockException;
import shop.model.CartItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Tracks stock levels and temporary reservations. Knows nothing about the Facade. */
public class InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();
    private final Map<String, List<CartItem>> reservations = new HashMap<>();

    public static InventoryService withDemoStock() {
        InventoryService inventory = new InventoryService();
        inventory.addStock("LAPTOP", 3);
        inventory.addStock("MOUSE", 10);
        inventory.addStock("KEYBOARD", 5);
        return inventory;
    }

    public void addStock(String sku, int quantity) {
        stock.merge(sku, quantity, Integer::sum);
    }

    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    /** All-or-nothing: either every item is reserved or none is. */
    public void reserve(String orderId, List<CartItem> items) throws OutOfStockException {
        for (CartItem item : items) {
            String sku = item.product().sku();
            if (available(sku) < item.quantity()) {
                throw new OutOfStockException("Not enough stock for " + item.product().name()
                        + " (requested " + item.quantity() + ", available " + available(sku) + ")");
            }
        }
        items.forEach(item -> stock.merge(item.product().sku(), -item.quantity(), Integer::sum));
        reservations.put(orderId, List.copyOf(items));
        System.out.println("  [Inventory] reserved stock for " + orderId);
    }

    public void commit(String orderId) {
        reservations.remove(orderId);
        System.out.println("  [Inventory] reservation confirmed for " + orderId);
    }

    public void release(String orderId) {
        List<CartItem> reserved = reservations.remove(orderId);
        if (reserved != null) {
            restock(reserved);
            System.out.println("  [Inventory] released reservation for " + orderId);
        }
    }

    public void restock(List<CartItem> items) {
        items.forEach(item -> stock.merge(item.product().sku(), item.quantity(), Integer::sum));
    }
}
