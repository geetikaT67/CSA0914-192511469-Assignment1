package parking.app;

import parking.model.Car;
import parking.model.ParkingSlot;
import parking.model.TwoWheeler;
import parking.model.Vehicle;
import parking.service.ParkingService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Task 10: Packages, Access Modifiers, and Autoboxing Demo ===\n");

        // 1. Using packaged service
        ParkingService service = new ParkingService(new int[]{2, 2});
        Vehicle bike = new TwoWheeler("TN09BIKE1");
        Vehicle car = new Car("TN09CAR2");

        ParkingSlot s1 = service.parkVehicle(bike);
        ParkingSlot s2 = service.parkVehicle(car);

        System.out.println("Bike parked at: " + s1);
        System.out.println("Car parked at: " + s2);
        System.out.println("Available slots remaining: " + service.getAvailableSlotCount());

        // 2. Demonstration of Autoboxing and Wrapper Comparison Risk
        System.out.println("\n--- Autoboxing and Wrapper Comparison Pitfall Demo ---");
        // Within Integer cache range (-128 to 127):
        Integer id1 = 100; // Autoboxed
        Integer id2 = 100; // Autoboxed (cached)
        System.out.println("id1 = 100, id2 = 100 (Cached range):");
        System.out.println("  id1 == id2: " + (id1 == id2) + " [True due to JVM -128..127 Integer cache]");

        // Outside Integer cache range:
        Integer id3 = 1000; // Autoboxed (new heap object)
        Integer id4 = 1000; // Autoboxed (distinct new heap object)
        System.out.println("id3 = 1000, id4 = 1000 (Outside cached range):");
        System.out.println("  id3 == id4: " + (id3 == id4) + " [CRITICAL BUG: False because == compares heap reference address!]");
        System.out.println("  id3.equals(id4): " + id3.equals(id4) + " [CORRECT: .equals() compares value]");

        // 3. Demonstrating Unboxing NullPointerException Risk
        System.out.println("\n--- Wrapper Unboxing NullPointerException Risk ---");
        Integer nullableSlot = null;
        try {
            int slotPrimitive = nullableSlot; // Implicit auto-unboxing invokes nullableSlot.intValue() -> NPE!
            System.out.println("Slot: " + slotPrimitive);
        } catch (NullPointerException e) {
            System.out.println("CAUGHT EXPECTED NPE: Auto-unboxing a null wrapper throws NullPointerException!");
        }
    }
}
