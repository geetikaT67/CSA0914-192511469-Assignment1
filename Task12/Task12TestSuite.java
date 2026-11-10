public class Task12TestSuite {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("     TASK 12: DAY-4 DESIGN CHALLENGE - ADAPTED SYSTEM TEST SUITE   ");
        System.out.println("==================================================================\n");

        int passed = 0;
        int failed = 0;

        // Requirement 3: 5 Floors with different numbers of slots on each floor
        // Floor 0: 3 slots, Floor 1: 5 slots, Floor 2: 2 slots, Floor 3: 4 slots, Floor 4: 3 slots
        int[] floorConfig = {3, 5, 2, 4, 3};
        AdaptedParkingSystem system = new AdaptedParkingSystem(floorConfig);

        System.out.println("--- 1. Validating 5-Floor Facility with Jagged Slot Allocation ---");
        if (system.getFloorCount() == 5 &&
            system.getSlotsOnFloor(0) == 3 &&
            system.getSlotsOnFloor(1) == 5 &&
            system.getSlotsOnFloor(2) == 2 &&
            system.getSlotsOnFloor(3) == 4 &&
            system.getSlotsOnFloor(4) == 3) {
            System.out.println("[PASS] 5 Floors successfully initialized with custom slot counts per floor.");
            passed++;
        } else {
            System.out.println("[FAIL] 5-Floor initialization mismatch.");
            failed++;
        }

        // Requirement 1A: E-Bike Category (Flat Fee of Rs 15 regardless of duration)
        System.out.println("\n--- 2. Validating E-Bike Flat Fee Requirement ---");
        // E-Bike 1 parked for 30 minutes
        EBike ebike1 = new EBike("TN09EB001", UserCategory.VISITOR_HOURLY);
        ParkingTicket tEB1 = system.parkVehicle(ebike1, 600, false); // 10:00 AM
        double feeEB1 = system.exitVehicle(tEB1.getTicketId(), 630, 0, false); // 30 mins
        if (feeEB1 == 15.0) {
            System.out.println("[PASS] E-Bike short stay (30 mins) charged exact flat fee: Rs 15.00");
            passed++;
        } else {
            System.out.println("[FAIL] E-Bike flat fee failed. Expected 15.0, got " + feeEB1);
            failed++;
        }

        // E-Bike 2 parked for 300 minutes (5 hours) -> Still Rs 15 flat fee
        EBike ebike2 = new EBike("TN09EB002", UserCategory.VISITOR_HOURLY);
        ParkingTicket tEB2 = system.parkVehicle(ebike2, 600, false);
        double feeEB2 = system.exitVehicle(tEB2.getTicketId(), 900, 0, false); // 300 mins
        if (feeEB2 == 15.0) {
            System.out.println("[PASS] E-Bike long stay (300 mins / 5 hrs) charged exact flat fee: Rs 15.00");
            passed++;
        } else {
            System.out.println("[FAIL] E-Bike flat fee for long stay failed. Expected 15.0, got " + feeEB2);
            failed++;
        }

        // Requirement 1B: Delivery Van (Double Car Rate = Rs 60/hr during 9 AM - 5 PM)
        System.out.println("\n--- 3. Validating Delivery Van Peak Rate (9 AM - 5 PM) Requirement ---");
        // Case A: Delivery Van during peak hours (e.g. 10:00 AM to 12:00 PM -> 120 mins = 2 hrs @ Rs 60/hr = Rs 120.00)
        DeliveryVan vanPeak = new DeliveryVan("TN09VAN01", UserCategory.VISITOR_HOURLY);
        ParkingTicket tVan1 = system.parkVehicle(vanPeak, 600, false); // 10:00 AM (peak)
        double feeVan1 = system.exitVehicle(tVan1.getTicketId(), 720, 0, false); // 12:00 PM (120 mins)
        if (feeVan1 == 120.0) {
            System.out.println("[PASS] Delivery Van during peak hours (2 hrs @ Rs 60/hr) correctly charged: Rs 120.00");
            passed++;
        } else {
            System.out.println("[FAIL] Delivery Van peak rate failed. Expected 120.0, got " + feeVan1);
            failed++;
        }

        // Case B: Delivery Van outside peak hours (e.g. 7:00 PM to 9:00 PM -> 19:00 (1140) to 21:00 (1260) -> 2 hrs @ Rs 30/hr = Rs 60.00)
        DeliveryVan vanOffPeak = new DeliveryVan("TN09VAN02", UserCategory.VISITOR_HOURLY);
        ParkingTicket tVan2 = system.parkVehicle(vanOffPeak, 1140, false); // 7:00 PM
        double feeVan2 = system.exitVehicle(tVan2.getTicketId(), 1260, 0, false); // 9:00 PM (120 mins)
        if (feeVan2 == 60.0) {
            System.out.println("[PASS] Delivery Van outside peak hours (2 hrs @ standard Rs 30/hr) correctly charged: Rs 60.00");
            passed++;
        } else {
            System.out.println("[FAIL] Delivery Van off-peak rate failed. Expected 60.0, got " + feeVan2);
            failed++;
        }

        // Requirement 2: Visitor One-Day Pass
        System.out.println("\n--- 4. Validating Visitor One-Day Pass Requirement ---");
        Car visitorDayPassCar = new Car("TN09PASS99", UserCategory.VISITOR_HOURLY);
        // Visitor buys day pass at entry for Rs 80
        ParkingTicket tPass = system.parkVehicle(visitorDayPassCar, 500, true);
        // Stays for 8 hours (480 mins)
        double exitFee = system.exitVehicle(tPass.getTicketId(), 980, 0, true);
        if (exitFee == 0.0 && system.getDayPassesSold() == 1) {
            System.out.println("[PASS] One-Day Pass holder parked for 8 hours with Rs 0 exit fee (Pass prepaid Rs 80.00).");
            passed++;
        } else {
            System.out.println("[FAIL] One-Day Pass holder exit fee failed. Got " + exitFee);
            failed++;
        }

        // Summary
        System.out.println("\n==================================================================");
        System.out.println("TASK 12 TEST RESULTS: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("Total Revenue Collected in Task 12: Rs " + system.getTotalRevenue());
        System.out.println("==================================================================");
    }
}
