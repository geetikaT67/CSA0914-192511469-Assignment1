// Demonstration of Inheritance Hierarchy and Constructor Execution Trace
public class InheritanceHierarchyDemo {

    // Superclass
    static abstract class Vehicle {
        private final String regNo;

        public Vehicle(String regNo) {
            System.out.println("  [1] Vehicle constructor executing. regNo initialized to: " + regNo);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        public abstract double ratePerHour();
    }

    // Subclass inheriting Vehicle (Hierarchical inheritance)
    static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) {
            super(regNo);
            System.out.println("  [2] TwoWheeler constructor executing.");
        }

        @Override
        public double ratePerHour() { return 10.0; }
    }

    // Subclass inheriting Vehicle
    static class Car extends Vehicle {
        private final int passengerCapacity;

        public Car(String regNo, int passengerCapacity) {
            super(regNo); // Explicit call to superclass constructor
            System.out.println("  [2] Car constructor executing. Capacity set to: " + passengerCapacity);
            this.passengerCapacity = passengerCapacity;
        }

        public Car(String regNo) {
            this(regNo, 4);
        }

        public int getPassengerCapacity() { return passengerCapacity; }

        @Override
        public double ratePerHour() { return 30.0; }
    }

    // Interface capability
    interface Chargeable {
        double chargingFee(int kWh);
    }

    // Multilevel Subclass: ElectricCar extends Car extends Vehicle
    static class ElectricCar extends Car implements Chargeable {
        private final int batteryCapacityKWh;

        public ElectricCar(String regNo, int batteryCapacityKWh) {
            super(regNo, 5); // Chained call to immediate superclass Car constructor
            System.out.println("  [3] ElectricCar constructor executing. Battery: " + batteryCapacityKWh + " kWh");
            this.batteryCapacityKWh = batteryCapacityKWh;
        }

        @Override
        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }

        public int getBatteryCapacityKWh() { return batteryCapacityKWh; }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 06: Inheritance and Constructor Trace Demo ===\n");

        System.out.println("Creating an ElectricCar instance: new ElectricCar(\"TN09EV9999\", 60)");
        ElectricCar ev = new ElectricCar("TN09EV9999", 60);

        System.out.println("\nConstructor Execution Trace Complete.");
        System.out.println("Vehicle Details: " + ev.getRegNo() + ", Hourly Rate: Rs " + ev.ratePerHour() +
                           ", Charging Fee for 10 kWh: Rs " + ev.chargingFee(10));
    }
}
