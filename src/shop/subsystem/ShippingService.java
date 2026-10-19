package shop.subsystem;

import shop.exception.ShippingException;

import java.util.HashSet;
import java.util.Set;

/** Creates and cancels shipments. */
public class ShippingService {

    private final Set<String> activeShipments = new HashSet<>();

    public String createShipment(String orderId, String address) throws ShippingException {
        if (address == null || address.isBlank()) {
            throw new ShippingException("Cannot ship: delivery address is missing");
        }
        String trackingNumber = "TRK-" + orderId;
        activeShipments.add(trackingNumber);
        System.out.println("  [Shipping] shipment " + trackingNumber + " created for " + address);
        return trackingNumber;
    }

    public void cancelShipment(String trackingNumber) {
        if (activeShipments.remove(trackingNumber)) {
            System.out.println("  [Shipping] shipment " + trackingNumber + " cancelled");
        }
    }
}
