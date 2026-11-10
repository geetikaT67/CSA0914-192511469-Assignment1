// Demonstration of static variables, static methods, object lifecycle, and pass-by-value of references
public class StaticLifecycleDemo {

    // Ticket demonstrating static vs instance state
    static class SimpleTicket {
        // STATIC MEMBER: Shared globally across all ticket instances to ensure monotonically increasing IDs
        private static int ticketCounter = 0;
        private static final String FACILITY_NAME;

        // Static initialization block
        static {
            FACILITY_NAME = "SIMATS Smart-Parking Campus Center";
            System.out.println("[STATIC BLOCK] Initialized " + FACILITY_NAME);
        }

        // INSTANCE MEMBERS: Unique to each individual ticket object
        private final int ticketId;
        private final String vehicleNo;

        public SimpleTicket(String vehicleNo) {
            this.ticketId = ++ticketCounter;
            this.vehicleNo = vehicleNo;
        }

        public static int getTotalTicketsIssued() {
            return ticketCounter;
        }

        public static String getFacilityName() {
            return FACILITY_NAME;
        }

        public int getTicketId() { return ticketId; }
        public String getVehicleNo() { return vehicleNo; }

        @Override
        public String toString() {
            return "Ticket #" + ticketId + " (" + vehicleNo + ")";
        }
    }

    // Demonstrating Java pass-by-value of reference
    public static void modifyReference(SimpleTicket t) {
        // Re-assigning reference locally does NOT change caller's reference
        t = new SimpleTicket("MODIFIED_VEHICLE");
    }

    public static void main(String[] args) {
        System.out.println("=== Task 05: Static Members and Object Life Cycle Demo ===\n");

        System.out.println("Facility Name from Static Method: " + SimpleTicket.getFacilityName());

        // Creating tickets
        SimpleTicket t1 = new SimpleTicket("TN01A1111");
        SimpleTicket t2 = new SimpleTicket("TN02B2222");
        SimpleTicket t3 = new SimpleTicket("TN03C3333");

        System.out.println("Ticket 1: " + t1);
        System.out.println("Ticket 2: " + t2);
        System.out.println("Ticket 3: " + t3);
        System.out.println("Total Tickets Issued (Static Counter): " + SimpleTicket.getTotalTicketsIssued());

        // Object Passing Demonstration
        System.out.println("\n--- Object Passing (Pass-by-value of Reference) ---");
        System.out.println("Before modifyReference, t1 = " + t1);
        modifyReference(t1);
        System.out.println("After modifyReference, t1 = " + t1 + " (Caller reference unchanged)");

        // Object Life Cycle and Garbage Collection Eligibility
        System.out.println("\n--- Object Lifecycle & Garbage Collection Eligibility ---");
        System.out.println("Dereferencing t3 (t3 = null)...");
        t3 = null; // The object previously referenced by t3 is now unreachable and eligible for GC.
        System.gc(); // Suggest garbage collection to JVM
        System.out.println("Dereferenced object is now eligible for GC reclaim on heap.");
    }
}
