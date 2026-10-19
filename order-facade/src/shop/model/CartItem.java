package shop.model;

import java.math.BigDecimal;

public record CartItem(Product product, int quantity) {

    public CartItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
    }

    public BigDecimal subtotal() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}
