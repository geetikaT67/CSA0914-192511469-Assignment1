import java.util.Arrays;

// Capability interface for electric vehicles
interface Chargeable {
    double chargingFee(int kWh);
}

// User Category enum
enum UserType {
    STUDENT_PASS,
    STAFF_PASS,
    VISITOR
}

// Abstract base class representing a Vehicle
abstract class Vehicle {
    private final String regNo;
    private final UserType userType;

    public Vehicle(String regNo, UserType userType) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        this.regNo = regNo.trim().toUpperCase();
        this.userType = userType != null ? userType : UserType.VISITOR;
    }

    public String getRegNo() {
        return regNo;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean hasPass() {
        return userType == UserType.STUDENT_PASS || userType == UserType.STAFF_PASS;
    }

    protected abstract double ratePerHour();

    // Every started hour is charged in full using integer arithmetic
    public double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative.");
        }
        // Staff and students holding a valid monthly pass park free of charge
        if (hasPass()) {
            return 0.0;
        }
        if (minutes == 0) {
            return 0.0; // Zero minutes stay
        }
        int hours = (minutes + 59) / 60; // Integer arithmetic ceiling
        return hours * ratePerHour();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return regNo.equalsIgnoreCase(vehicle.regNo);
    }

    @Override
    public int hashCode() {
        return regNo.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + regNo + ", Type: " + userType + "]";
    }
}

// Two-Wheeler subclass
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo, UserType userType) {
        super(regNo, userType);
    }

    @Override
    protected double ratePerHour() {
        return 10.0; // Rs 10 per hour
    }
}

// Four-Wheeler Car subclass
class Car extends Vehicle {
    public Car(String regNo, UserType userType) {
        super(regNo, userType);
    }

    @Override
    protected double ratePerHour() {
        return 30.0; // Rs 30 per hour
    }
}

// Electric Car subclass implementing Chargeable
class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo, UserType userType) {
        super(regNo, userType);
    }

    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0) {
            throw new IllegalArgumentException("Energy consumed cannot be negative.");
        }
        return kWh * 12.0; // Rs 12 per kWh
    }
}

// Parking Slot class
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

    public void occupy(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
    }

    public void vacate() {
        this.parkedVehicle = null;
    }

    @Override
    public String toString() {
        return "Floor " + floor + "-Slot " + slotNumber + (isFree() ? " (Free)" : " (Occupied by " + parkedVehicle.getRegNo() + ")");
    }
}

// Parking Ticket class
class ParkingTicket {
    private static int ticketCounter = 1000;

    private final int ticketId;
    private final Vehicle vehicle;
    private final int floor;
    private final int slotNumber;
    private final int entryTimeMinutes;
    private int exitTimeMinutes;
    private double totalFee;
    private boolean active;

    public ParkingTicket(Vehicle vehicle, int floor, int slotNumber, int entryTimeMinutes) {
        this.ticketId = ++ticketCounter;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.entryTimeMinutes = entryTimeMinutes;
        this.active = true;
    }

    // Copy constructor (e.g., for re-issuing a duplicate receipt/ticket)
    public ParkingTicket(ParkingTicket other) {
        this.ticketId = other.ticketId;
        this.vehicle = other.vehicle;
        this.floor = other.floor;
        this.slotNumber = other.slotNumber;
        this.entryTimeMinutes = other.entryTimeMinutes;
        this.exitTimeMinutes = other.exitTimeMinutes;
        this.totalFee = other.totalFee;
        this.active = other.active;
    }

    public int getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public int getEntryTimeMinutes() { return entryTimeMinutes; }
    public int getExitTimeMinutes() { return exitTimeMinutes; }
    public double getTotalFee() { return totalFee; }
    public boolean isActive() { return active; }

    public void closeTicket(int exitTimeMinutes, double fee) {
        this.exitTimeMinutes = exitTimeMinutes;
        this.totalFee = fee;
        this.active = false;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " [" + vehicle + " at F" + floor + "-S" + slotNumber +
               ", Entry: " + entryTimeMinutes + " min, Status: " + (active ? "ACTIVE" : "CLOSED (Rs " + totalFee + ")") + "]";
    }
}

// Facility Class representing the Multi-Level Smart Parking System
public class CampusParkingSystem {
    private final ParkingSlot[][] slots;
    private final ParkingTicket[] activeTickets;
    private int activeTicketCount;
    private double totalDailyRevenue;

    public CampusParkingSystem(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) {
            throw new IllegalArgumentException("Facility must have at least one floor.");
        }
        slots = new ParkingSlot[slotsPerFloor.length][];
        int totalCapacity = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                slots[f][s] = new ParkingSlot(f, s);
            }
            totalCapacity += slotsPerFloor[f];
        }
        activeTickets = new ParkingTicket[totalCapacity];
        activeTicketCount = 0;
        totalDailyRevenue = 0.0;
    }

    // Check if vehicle with regNo is already parked inside
    public boolean isVehicleParked(String regNo) {
        for (int i = 0; i < activeTicketCount; i++) {
            if (activeTickets[i].isActive() && activeTickets[i].getVehicle().getRegNo().equalsIgnoreCase(regNo)) {
                return true;
            }
        }
        return false;
    }

    // Vehicle entry operation
    public ParkingTicket parkVehicle(Vehicle vehicle, int entryTimeMinutes) {
        if (vehicle == null) {
            System.out.println("[REJECTED] Vehicle cannot be null.");
            return null;
        }
        if (isVehicleParked(vehicle.getRegNo())) {
            System.out.println("[REJECTED] Duplicate entry: Vehicle " + vehicle.getRegNo() + " is already inside the facility.");
            return null;
        }

        // Search floor by floor, slot by slot for first free slot
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s].isFree()) {
                    slots[f][s].occupy(vehicle);
                    ParkingTicket ticket = new ParkingTicket(vehicle, f, s, entryTimeMinutes);
                    activeTickets[activeTicketCount++] = ticket;
                    System.out.println("[PARKED] " + vehicle + " allocated to Floor " + f + ", Slot " + s + ". " + ticket);
                    return ticket;
                }
            }
        }

        System.out.println("[FULL] No free slot available for " + vehicle.getRegNo() + ".");
        return null;
    }

    // Vehicle exit operation
    public double exitVehicle(int ticketId, int exitTimeMinutes, int chargingKWh) {
        ParkingTicket ticket = null;
        int ticketIndex = -1;

        for (int i = 0; i < activeTicketCount; i++) {
            if (activeTickets[i].getTicketId() == ticketId && activeTickets[i].isActive()) {
                ticket = activeTickets[i];
                ticketIndex = i;
                break;
            }
        }

        if (ticket == null) {
            System.out.println("[ERROR] Invalid or inactive ticket ID: " + ticketId);
            return -1.0;
        }

        if (exitTimeMinutes < ticket.getEntryTimeMinutes()) {
            System.out.println("[ERROR] Exit time (" + exitTimeMinutes + ") cannot be earlier than entry time (" + ticket.getEntryTimeMinutes() + ").");
            return -1.0;
        }

        int durationMinutes = exitTimeMinutes - ticket.getEntryTimeMinutes();
        Vehicle v = ticket.getVehicle();
        double parkingFee = v.calculateFee(durationMinutes);
        double evChargingFee = 0.0;

        if (v instanceof Chargeable chargeable && chargingKWh > 0) {
            evChargingFee = chargeable.chargingFee(chargingKWh);
        }

        double totalFee = parkingFee + evChargingFee;
        ticket.closeTicket(exitTimeMinutes, totalFee);

        // Free the slot
        slots[ticket.getFloor()][ticket.getSlotNumber()].vacate();

        // Remove from active array by shifting
        for (int i = ticketIndex; i < activeTicketCount - 1; i++) {
            activeTickets[i] = activeTickets[i + 1];
        }
        activeTickets[--activeTicketCount] = null;

        totalDailyRevenue += totalFee;

        System.out.printf("[EXIT] %s exited from Floor %d, Slot %d. Duration: %d mins. Parking Fee: Rs %.2f, Charging Fee: Rs %.2f | Total Paid: Rs %.2f%n",
                v.getRegNo(), ticket.getFloor(), ticket.getSlotNumber(), durationMinutes, parkingFee, evChargingFee, totalFee);

        return totalFee;
    }

    public double getTotalDailyRevenue() {
        return totalDailyRevenue;
    }

    public int getFreeSlotCount() {
        int count = 0;
        for (ParkingSlot[] floor : slots) {
            for (ParkingSlot slot : floor) {
                if (slot.isFree()) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println("=== Campus Smart-Parking System Initialized ===");
        CampusParkingSystem facility = new CampusParkingSystem(new int[]{2, 2}); // 2 floors, 2 slots each
        System.out.println("Total free slots: " + facility.getFreeSlotCount());
    }
}
