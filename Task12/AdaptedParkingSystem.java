import java.util.Arrays;

// User Type Enum with Day Pass Support
enum UserCategory {
    STUDENT_MONTHLY_PASS,
    STAFF_MONTHLY_PASS,
    VISITOR_HOURLY,
    VISITOR_DAY_PASS
}

// Capability interface for EVs
interface Chargeable {
    double chargingFee(int kWh);
}

// Capability interface for Pass holders
interface PassHolder {
    boolean isValidPass();
    String getPassType();
}

// Abstract Vehicle Base Class
abstract class Vehicle {
    private final String regNo;
    private final UserCategory userCategory;

    public Vehicle(String regNo, UserCategory userCategory) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        this.regNo = regNo.trim().toUpperCase();
        this.userCategory = userCategory != null ? userCategory : UserCategory.VISITOR_HOURLY;
    }

    public String getRegNo() { return regNo; }
    public UserCategory getUserCategory() { return userCategory; }

    public boolean hasFreeParkingPass() {
        return userCategory == UserCategory.STUDENT_MONTHLY_PASS ||
               userCategory == UserCategory.STAFF_MONTHLY_PASS ||
               userCategory == UserCategory.VISITOR_DAY_PASS;
    }

    protected abstract double standardRatePerHour();

    // Standard hourly fee computation using integer ceiling
    public double calculateFee(int entryMinute, int exitMinute) {
        if (exitMinute < entryMinute) {
            throw new IllegalArgumentException("Exit time cannot precede entry time.");
        }
        if (hasFreeParkingPass()) {
            return 0.0; // Covered by monthly or day pass
        }
        int minutes = exitMinute - entryMinute;
        if (minutes == 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * standardRatePerHour();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle v)) return false;
        return regNo.equalsIgnoreCase(v.regNo);
    }

    @Override
    public int hashCode() {
        return regNo.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + regNo + ", Category: " + userCategory + "]";
    }
}

// 1. Two-Wheeler
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo, UserCategory userCategory) {
        super(regNo, userCategory);
    }

    @Override
    protected double standardRatePerHour() {
        return 10.0;
    }
}

// 2. Regular Car
class Car extends Vehicle {
    public Car(String regNo, UserCategory userCategory) {
        super(regNo, userCategory);
    }

    @Override
    protected double standardRatePerHour() {
        return 30.0;
    }
}

// 3. Electric Car
class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo, UserCategory userCategory) {
        super(regNo, userCategory);
    }

    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative.");
        return kWh * 12.0;
    }
}

// 4. NEW CATEGORY 1: E-Bike (Task 12 requirement: charged a flat fee)
class EBike extends Vehicle {
    private static final double FLAT_FEE = 15.0; // Rs 15 flat fee

    public EBike(String regNo, UserCategory userCategory) {
        super(regNo, userCategory);
    }

    @Override
    protected double standardRatePerHour() {
        return 0.0; // Not billed hourly
    }

    @Override
    public double calculateFee(int entryMinute, int exitMinute) {
        if (hasFreeParkingPass()) {
            return 0.0;
        }
        int minutes = exitMinute - entryMinute;
        if (minutes == 0) return 0.0;
        return FLAT_FEE; // Flat fee regardless of duration
    }
}

// 5. NEW CATEGORY 2: Delivery Van (Task 12 requirement: double car rate during 9 AM - 5 PM)
class DeliveryVan extends Vehicle {
    // 9 AM = 9 * 60 = 540 min, 5 PM = 17 * 60 = 1020 min
    private static final int PEAK_START_MIN = 540;
    private static final int PEAK_END_MIN = 1020;
    private static final double REGULAR_RATE = 30.0;       // Regular car rate: Rs 30/hr
    private static final double PEAK_RATE = 2 * REGULAR_RATE; // Double rate: Rs 60/hr

    public DeliveryVan(String regNo, UserCategory userCategory) {
        super(regNo, userCategory);
    }

    @Override
    protected double standardRatePerHour() {
        return REGULAR_RATE;
    }

    @Override
    public double calculateFee(int entryMinute, int exitMinute) {
        if (hasFreeParkingPass()) {
            return 0.0;
        }
        int minutes = exitMinute - entryMinute;
        if (minutes == 0) return 0.0;

        // Check if stay overlaps with 9 AM - 5 PM
        boolean overlapsPeak = (entryMinute < PEAK_END_MIN && exitMinute > PEAK_START_MIN);
        int hours = (minutes + 59) / 60;

        if (overlapsPeak) {
            return hours * PEAK_RATE; // Rs 60 per hour
        } else {
            return hours * REGULAR_RATE; // Rs 30 per hour
        }
    }
}

// One-Day Pass Record / Helper
class DayPass {
    private static final double DAY_PASS_PRICE = 80.0; // Rs 80 flat for full day parking
    public static double getPrice() { return DAY_PASS_PRICE; }
}

// Parking Slot
class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isFree() { return parkedVehicle == null; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void occupy(Vehicle v) { this.parkedVehicle = v; }
    public void vacate() { this.parkedVehicle = null; }
}

// Parking Ticket
class ParkingTicket {
    private static int counter = 5000;
    private final int ticketId;
    private final Vehicle vehicle;
    private final int floor;
    private final int slotNumber;
    private final int entryMinute;
    private int exitMinute;
    private double feeCharged;
    private boolean active;

    public ParkingTicket(Vehicle vehicle, int floor, int slotNumber, int entryMinute) {
        this.ticketId = ++counter;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.entryMinute = entryMinute;
        this.active = true;
    }

    public int getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public int getEntryMinute() { return entryMinute; }
    public boolean isActive() { return active; }

    public void close(int exitMinute, double fee) {
        this.exitMinute = exitMinute;
        this.feeCharged = fee;
        this.active = false;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " [" + vehicle + " at Floor " + floor + ", Slot " + slotNumber + "]";
    }
}

// Adapted Multi-Floor Parking Facility supporting 5 floors with variable slots
public class AdaptedParkingSystem {
    private final ParkingSlot[][] slots;
    private final ParkingTicket[] activeTickets;
    private int activeCount;
    private double totalRevenue;
    private int dayPassesSold;

    public AdaptedParkingSystem(int[] slotsPerFloor) {
        slots = new ParkingSlot[slotsPerFloor.length][];
        int totalCap = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                slots[f][s] = new ParkingSlot(f, s);
            }
            totalCap += slotsPerFloor[f];
        }
        activeTickets = new ParkingTicket[totalCap];
        activeCount = 0;
        totalRevenue = 0.0;
        dayPassesSold = 0;
    }

    public ParkingTicket parkVehicle(Vehicle vehicle, int entryMinute, boolean purchaseDayPass) {
        if (vehicle == null) return null;

        // If visitor purchases a Day Pass
        if (purchaseDayPass && vehicle.getUserCategory() == UserCategory.VISITOR_HOURLY) {
            dayPassesSold++;
            totalRevenue += DayPass.getPrice();
            System.out.println("[DAY PASS PURCHASED] Vehicle " + vehicle.getRegNo() + " bought a One-Day Pass for Rs " + DayPass.getPrice());
        }

        // Allocate slot
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s].isFree()) {
                    slots[f][s].occupy(vehicle);
                    ParkingTicket ticket = new ParkingTicket(vehicle, f, s, entryMinute);
                    activeTickets[activeCount++] = ticket;
                    System.out.println("[PARKED] " + vehicle + " -> Floor " + f + ", Slot " + s + " (" + ticket + ")");
                    return ticket;
                }
            }
        }
        System.out.println("[FULL] No available slot for " + vehicle.getRegNo());
        return null;
    }

    public double exitVehicle(int ticketId, int exitMinute, int chargingKWh, boolean hasDayPass) {
        int index = -1;
        ParkingTicket ticket = null;
        for (int i = 0; i < activeCount; i++) {
            if (activeTickets[i].getTicketId() == ticketId && activeTickets[i].isActive()) {
                ticket = activeTickets[i];
                index = i;
                break;
            }
        }

        if (ticket == null) return -1.0;

        Vehicle v = ticket.getVehicle();
        double parkingFee = 0.0;
        if (!hasDayPass && !v.hasFreeParkingPass()) {
            parkingFee = v.calculateFee(ticket.getEntryMinute(), exitMinute);
        }

        double evFee = 0.0;
        if (v instanceof Chargeable c && chargingKWh > 0) {
            evFee = c.chargingFee(chargingKWh);
        }

        double total = parkingFee + evFee;
        ticket.close(exitMinute, total);
        slots[ticket.getFloor()][ticket.getSlotNumber()].vacate();

        for (int i = index; i < activeCount - 1; i++) {
            activeTickets[i] = activeTickets[i + 1];
        }
        activeTickets[--activeCount] = null;
        totalRevenue += total;

        System.out.printf("[EXIT] %s freed F%d-S%d. Parking Fee: Rs %.2f, Charging Fee: Rs %.2f | Net Exit Payment: Rs %.2f%n",
                v.getRegNo(), ticket.getFloor(), ticket.getSlotNumber(), parkingFee, evFee, total);

        return total;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public int getDayPassesSold() { return dayPassesSold; }
    public int getFloorCount() { return slots.length; }
    public int getSlotsOnFloor(int floor) { return slots[floor].length; }
}
