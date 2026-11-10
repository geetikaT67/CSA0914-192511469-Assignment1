package parking.model;

public abstract class Vehicle {
    // Encapsulated private field: accessible only within this class
    private final String regNo;

    // Protected constructor: accessible to subclasses in any package
    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Vehicle registration cannot be blank.");
        }
        this.regNo = regNo.trim().toUpperCase();
    }

    // Public getter: controlled read-only access for external packages
    public final String getRegNo() {
        return regNo;
    }

    // Protected abstract method: intended to be overridden by subclasses
    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Validation Error: Duration cannot be negative.");
        }
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + regNo + ")";
    }
}
