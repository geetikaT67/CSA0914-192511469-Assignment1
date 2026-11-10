// Demonstration of Abstraction: Abstract Classes, Interfaces, Default Methods, and Static Methods
public class AbstractionDemo {

    // Capability Interface 1: Charging
    interface Chargeable {
        double DEFAULT_EV_RATE = 12.0;

        // Abstract method: must be implemented by concrete class
        double chargingFee(int kWh);

        // Default method: provides default logic without breaking existing implementers
        default double calculateFastChargingFee(int kWh) {
            return chargingFee(kWh) * 1.25; // 25% surcharge for rapid DC charging
        }

        // Static utility method in interface
        static boolean isValidKWh(int kWh) {
            return kWh >= 0 && kWh <= 200;
        }
    }

    // Capability Interface 2: Payment Processing
    interface Payable {
        double processPayment(double amount);

        default void printReceipt(String item, double amount) {
            System.out.println("RECEIPT: " + item + " | Total Paid: Rs " + amount);
        }
    }

    // Abstract Class: Holds common state and constructor, leaving ratePerHour abstract
    static abstract class Vehicle {
        private final String regNo;

        public Vehicle(String regNo) {
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }

        public abstract double ratePerHour();

        public double calculateBaseFee(int hours) {
            return hours * ratePerHour();
        }
    }

    // Concrete class implementing multiple interfaces (Multiple Inheritance of Type)
    static class ElectricCar extends Vehicle implements Chargeable, Payable {
        public ElectricCar(String regNo) {
            super(regNo);
        }

        @Override
        public double ratePerHour() {
            return 30.0;
        }

        @Override
        public double chargingFee(int kWh) {
            return kWh * DEFAULT_EV_RATE;
        }

        @Override
        public double processPayment(double amount) {
            System.out.println("Processing digital campus wallet payment of Rs " + amount);
            return amount;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 09: Abstraction and Interface Features Demo ===\n");

        ElectricCar ev = new ElectricCar("TN09EV1234");
        System.out.println("EV Registration: " + ev.getRegNo());
        System.out.println("Standard EV Charging (15 kWh): Rs " + ev.chargingFee(15));
        System.out.println("Fast EV Charging Default Method (15 kWh): Rs " + ev.calculateFastChargingFee(15));
        System.out.println("Is 50 kWh valid? (Static interface method): " + Chargeable.isValidKWh(50));

        ev.processPayment(390.0);
        ev.printReceipt("Parking + Fast Charging", 390.0);
    }
}
