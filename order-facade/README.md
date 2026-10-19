# Online Order Processing Facade (Java 17)

ShP-2216 Software Design Patterns, Assignment #4: Facade pattern (Option B).

`OrderFacade` hides four collaborating services behind two simple operations:
`placeOrder(cart, customer)` and `cancelOrder(orderId)`. If any step fails, the steps
that already succeeded are rolled back (e.g. reserved stock is released when payment is declined).

## Structure
```
src/shop
├── Main.java                  Client: uses only OrderFacade
├── facade/OrderFacade.java    The Facade
├── subsystem/                 InventoryService, PaymentProcessor, ShippingService, NotificationService
├── model/                     Product, CartItem, Cart, Customer, Order
└── exception/                 SubsystemException (+3 subtypes), OrderFailedException
```

## Run
```bash
javac -d out $(find src -name '*.java')
java -cp out shop.Main
```
Or open the folder in IntelliJ IDEA, mark `src` as Sources Root and run `shop.Main`.

## Demo scenarios (Main)
1. Successful order  2. Out of stock  3. Payment declined (stock released)
4. Shipping fails (payment refunded + stock released)  5. Cancel an order

## Pattern notes
- Subsystem classes have no reference to the Facade and can be used directly (see the second `OrderFacade` constructor).
- Clients see a single exception type, `OrderFailedException`, never subsystem exceptions.
