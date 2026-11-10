// Demonstration of Constructors, Constructor Chaining, this keyword, and Copy Constructors
public class ConstructorDemo {

    // Ticket class demonstrating default, parameterized, overloaded, and copy constructors
    static class Ticket {
        private static int counter = 1000;

        private final int ticketId;
        private final String vehicleNo;
        private final int floor;
        private final int slot;
        private final int entryTimeMinutes;
        private final String passType;

        // 1. Fully-parameterized constructor (primary constructor)
        public Ticket(String vehicleNo, int floor, int slot, int entryTimeMinutes, String passType) {
            if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle number cannot be empty.");
            }
            this.ticketId = ++counter;
            this.vehicleNo = vehicleNo.toUpperCase();
            this.floor = floor;
            this.slot = slot;
            this.entryTimeMinutes = entryTimeMinutes;
            this.passType = passType;
        }

        // 2. Overloaded constructor using constructor chaining (this(...))
        // Defaulting passType to "VISITOR"
        public Ticket(String vehicleNo, int floor, int slot, int entryTimeMinutes) {
            this(vehicleNo, floor, slot, entryTimeMinutes, "VISITOR");
        }

        // 3. Overloaded constructor defaulting entryTime to current simulation start (0 min)
        public Ticket(String vehicleNo, int floor, int slot) {
            this(vehicleNo, floor, slot, 0, "VISITOR");
        }

        // 4. Copy Constructor (used when re-issuing a duplicate ticket or archival snapshot)
        public Ticket(Ticket original) {
            if (original == null) {
                throw new IllegalArgumentException("Cannot copy null ticket.");
            }
            this.ticketId = original.ticketId; // Preserves same ID for duplicate token
            this.vehicleNo = original.vehicleNo;
            this.floor = original.floor;
            this.slot = original.slot;
            this.entryTimeMinutes = original.entryTimeMinutes;
            this.passType = original.passType;
        }

        public int getTicketId() { return ticketId; }
        public String getVehicleNo() { return vehicleNo; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public int getEntryTimeMinutes() { return entryTimeMinutes; }
        public String getPassType() { return passType; }

        @Override
        public String toString() {
            return "Ticket #" + ticketId + " [" + vehicleNo + ", Floor: " + floor +
                   ", Slot: " + slot + ", Entry: " + entryTimeMinutes + " min, Pass: " + passType + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 04: Constructors and Object Creation Demonstration ===\n");

        // 1. Primary parameterized constructor
        Ticket t1 = new Ticket("TN01A1000", 0, 1, 540, "STUDENT_PASS");
        System.out.println("1. Primary Parameterized Constructor: " + t1);

        // 2. Overloaded constructor with constructor chaining
        Ticket t2 = new Ticket("TN02B2000", 1, 2, 600);
        System.out.println("2. Chained Overloaded Constructor (Visitor): " + t2);

        // 3. Chained constructor defaulting time and pass type
        Ticket t3 = new Ticket("TN03C3000", 2, 0);
        System.out.println("3. Chained Constructor (Defaulted Time): " + t3);

        // 4. Copy constructor (Re-issuing duplicate ticket)
        Ticket t1Duplicate = new Ticket(t1);
        System.out.println("4. Re-issued Duplicate Ticket (Copy Constructor): " + t1Duplicate);
        System.out.println("Is distinct object in memory (t1 != t1Duplicate)? " + (t1 != t1Duplicate));
        System.out.println("Do they hold equal ticket ID? " + (t1.getTicketId() == t1Duplicate.getTicketId()));
    }
}
