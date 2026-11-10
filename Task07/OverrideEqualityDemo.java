import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Demonstration of Method Overriding, Object contract (equals/hashCode/toString), and the final keyword
public class OverrideEqualityDemo {

    static abstract class Vehicle {
        // 'final' variable: once initialized, license plate can never be mutated
        private final String regNo;

        public Vehicle(String regNo) {
            this.regNo = Objects.requireNonNull(regNo, "regNo cannot be null").trim().toUpperCase();
        }

        // 'final' method: prevents subclasses from tampering with license plate identity access
        public final String getRegNo() {
            return regNo;
        }

        // Overridable abstract method for fee rate
        protected abstract double ratePerHour();

        // Overriding equals() so two vehicles with identical regNo are considered equal
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof Vehicle)) return false;
            Vehicle other = (Vehicle) o;
            return this.regNo.equals(other.regNo);
        }

        // Overriding hashCode() according to the Java Object contract:
        // If v1.equals(v2), then v1.hashCode() must equal v2.hashCode()
        @Override
        public int hashCode() {
            return this.regNo.hashCode();
        }

        // Overriding toString() for clear string representation
        @Override
        public String toString() {
            return getClass().getSimpleName() + " [Plate: " + regNo + "]";
        }
    }

    static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() { return 10.0; }
    }

    static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() { return 30.0; }
    }

    // A final class cannot be extended further (e.g. security-sensitive or terminal type)
    static final class HeavyTransport extends Vehicle {
        public HeavyTransport(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() { return 80.0; }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 07: Method Overriding and Equality Demo ===\n");

        Vehicle v1 = new Car("TN09XY1234");
        Vehicle v2 = new Car("TN09XY1234"); // Different object instance, same plate
        Vehicle v3 = new TwoWheeler("TN09XY1234"); // Different subclass, same plate

        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        System.out.println("v3: " + v3);

        System.out.println("\n--- Testing Equality (equals) and Hash Code ---");
        System.out.println("v1 == v2 (Reference identity): " + (v1 == v2) + " (Distinct heap objects)");
        System.out.println("v1.equals(v2) (Domain equality): " + v1.equals(v2) + " [PASS: Equal by registration]");
        System.out.println("v1.equals(v3) (Cross-type with same plate): " + v1.equals(v3) + " [Equal by registration]");
        System.out.println("v1.hashCode() == v2.hashCode(): " + (v1.hashCode() == v2.hashCode()) + " [PASS: Hash contract holds]");

        // Demonstrating HashSet deduplication
        Set<Vehicle> vehicleSet = new HashSet<>();
        vehicleSet.add(v1);
        vehicleSet.add(v2); // Duplicate registration should not be added twice
        System.out.println("\nHashSet size after adding v1 and v2: " + vehicleSet.size() + " [PASS: Deduplicated by equals/hashCode]");
    }
}
