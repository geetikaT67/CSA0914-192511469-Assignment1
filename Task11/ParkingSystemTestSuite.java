public class ParkingSystemTestSuite {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("        CAMPUS SMART-PARKING SYSTEM - TASK 11 TEST SUITE        ");
        System.out.println("===============================================================\n");

        int passed = 0;
        int failed = 0;

        // Facility with 2 floors: Floor 0 has 2 slots, Floor 1 has 2 slots (Total 4 slots)
        CampusParkingSystem system = new CampusParkingSystem(new int[]{2, 2});

        // ==========================================================
        // TEST CATEGORY 1: NORMAL OPERATION
        // ==========================================================
        System.out.println("--- 1. Testing Normal Operation (Different Types & Pass Categories) ---");
        ParkingTicket t1 = system.parkVehicle(new TwoWheeler("TN09AA1001", UserType.VISITOR), 600); // 10:00 AM
        ParkingTicket t2 = system.parkVehicle(new Car("TN09BB2002", UserType.VISITOR), 615);        // 10:15 AM
        ParkingTicket t3 = system.parkVehicle(new ElectricCar("TN09CC3003", UserType.VISITOR), 630); // 10:30 AM
        ParkingTicket t4 = system.parkVehicle(new Car("TN09DD4004", UserType.STUDENT_PASS), 540);  // 09:00 AM

        if (t1 != null && t2 != null && t3 != null && t4 != null) {
            System.out.println("[PASS] All 4 normal vehicles parked successfully in free slots.");
            passed++;
        } else {
            System.out.println("[FAIL] Failed to park normal vehicles.");
            failed++;
        }

        // ==========================================================
        // TEST CATEGORY 2: BOUNDARY CASES
        // ==========================================================
        System.out.println("\n--- 2. Testing Boundary Cases ---");

        // Boundary 2.1: Last Free Slot & Facility Full
        // All 4 slots are occupied now. Attempting 5th vehicle:
        ParkingTicket tFull = system.parkVehicle(new TwoWheeler("TN09ZZ9999", UserType.VISITOR), 640);
        if (tFull == null && system.getFreeSlotCount() == 0) {
            System.out.println("[PASS] Boundary Case: Facility Full handled correctly (allocation returned null).");
            passed++;
        } else {
            System.out.println("[FAIL] Boundary Case: Over-allocation occurred.");
            failed++;
        }

        // Boundary 2.2: Exact Fee-Slab Boundary (120 minutes)
        // Vehicle 2 (Car Visitor) exits after exactly 120 minutes:
        double feeT2 = system.exitVehicle(t2.getTicketId(), 735, 0); // 735 - 615 = 120 mins = 2 hrs -> 2 * 30 = Rs 60
        if (feeT2 == 60.0) {
            System.out.println("[PASS] Boundary Case: Exact slab boundary (120 mins = 2 hrs @ Rs 30/hr -> Rs 60.00).");
            passed++;
        } else {
            System.out.println("[FAIL] Boundary Case: Expected Rs 60.00 but got Rs " + feeT2);
            failed++;
        }

        // Boundary 2.3: Zero-Minute Parking
        // Park a vehicle and immediately exit with 0 duration
        ParkingTicket tZero = system.parkVehicle(new TwoWheeler("TN09EE5005", UserType.VISITOR), 660);
        double feeZero = system.exitVehicle(tZero.getTicketId(), 660, 0); // 0 minutes
        if (feeZero == 0.0) {
            System.out.println("[PASS] Boundary Case: Zero-minute parking correctly charged Rs 0.00.");
            passed++;
        } else {
            System.out.println("[FAIL] Boundary Case: Expected Rs 0.00 for 0 mins but got Rs " + feeZero);
            failed++;
        }

        // Boundary 2.4: 1 Minute into Next Hour (Ceiling rule)
        // 105 mins for TwoWheeler Visitor (Vehicle 1) -> 2 hours @ 10/hr = Rs 20.00
        double feeT1 = system.exitVehicle(t1.getTicketId(), 705, 0);
        if (feeT1 == 20.0) {
            System.out.println("[PASS] Boundary Case: 105 minutes charged as 2 full hours (Rs 20.00).");
            passed++;
        } else {
            System.out.println("[FAIL] Boundary Case: Expected Rs 20.00 but got Rs " + feeT1);
            failed++;
        }

        // Student Pass Monthly Holder (Vehicle 4): 240 mins -> Rs 0.00
        double feeT4 = system.exitVehicle(t4.getTicketId(), 780, 0);
        if (feeT4 == 0.0) {
            System.out.println("[PASS] Pass Holder: Student pass holder parked free (Rs 0.00).");
            passed++;
        } else {
            System.out.println("[FAIL] Expected Rs 0.00 for Pass Holder but got Rs " + feeT4);
            failed++;
        }

        // Electric Car (Vehicle 3): 30 mins (1 hr = Rs 30) + 15 kWh @ Rs 12 = Rs 180 -> Rs 210.00
        double feeT3 = system.exitVehicle(t3.getTicketId(), 660, 15);
        if (feeT3 == 210.0) {
            System.out.println("[PASS] EV Operation: 30 mins stay + 15 kWh charging charged correctly (Rs 210.00).");
            passed++;
        } else {
            System.out.println("[FAIL] Expected Rs 210.00 for EV but got Rs " + feeT3);
            failed++;
        }

        // ==========================================================
        // TEST CATEGORY 3: INVALID CASES
        // ==========================================================
        System.out.println("\n--- 3. Testing Invalid & Error Cases ---");

        // Invalid 3.1: Duplicate Registration Number
        ParkingTicket dup1 = system.parkVehicle(new Car("TN09DUP111", UserType.VISITOR), 800);
        ParkingTicket dup2 = system.parkVehicle(new Car("TN09DUP111", UserType.STAFF_PASS), 805);
        if (dup1 != null && dup2 == null) {
            System.out.println("[PASS] Invalid Case: Duplicate vehicle registration rejected successfully.");
            passed++;
        } else {
            System.out.println("[FAIL] Invalid Case: Duplicate vehicle registration accepted.");
            failed++;
        }

        // Invalid 3.2: Invalid / Non-existent Ticket Exit
        double invalidTicketFee = system.exitVehicle(9999, 900, 0);
        if (invalidTicketFee == -1.0) {
            System.out.println("[PASS] Invalid Case: Non-existent ticket ID handled safely (returned -1.0).");
            passed++;
        } else {
            System.out.println("[FAIL] Invalid Case: Non-existent ticket ID returned invalid result.");
            failed++;
        }

        // Invalid 3.3: Exit time before entry time
        double negativeDurationFee = system.exitVehicle(dup1.getTicketId(), 700, 0); // entry was 800
        if (negativeDurationFee == -1.0) {
            System.out.println("[PASS] Invalid Case: Exit time earlier than entry time rejected.");
            passed++;
        } else {
            System.out.println("[FAIL] Invalid Case: Negative duration permitted.");
            failed++;
        }

        // Clean up dup1
        system.exitVehicle(dup1.getTicketId(), 860, 0); // 60 mins Car -> Rs 30

        // ==========================================================
        // TEST CATEGORY 4: HAND-CALCULATED REVENUE VERIFICATION
        // ==========================================================
        System.out.println("\n--- 4. Hand-Calculated Revenue Verification ---");
        // Hand-calculated items for the 5 initial vehicles:
        // T1: TwoWheeler (105 mins) = Rs 20.00
        // T2: Car (120 mins)        = Rs 60.00
        // T3: EV (30 min + 15 kWh)  = Rs 30 + Rs 180 = Rs 210.00
        // T4: Student Pass (240 min)= Rs 0.00
        // TZero: TwoWheeler (0 min) = Rs 0.00
        // Expected initial sum = Rs 290.00
        // plus dup1 cleanup (60 min Car = Rs 30.00) => Total = Rs 320.00
        double actualRevenue = system.getTotalDailyRevenue();
        double expectedRevenue = 20.0 + 60.0 + 210.0 + 0.0 + 0.0 + 30.0;

        System.out.println("Expected Hand-Calculated Revenue: Rs " + expectedRevenue);
        System.out.println("Actual System Daily Revenue:    Rs " + actualRevenue);

        if (Double.compare(actualRevenue, expectedRevenue) == 0) {
            System.out.println("[PASS] Hand-calculated daily revenue strictly matches system output (Rs 320.00)!");
            passed++;
        } else {
            System.out.println("[FAIL] Revenue mismatch! Expected: " + expectedRevenue + ", Got: " + actualRevenue);
            failed++;
        }

        // ==========================================================
        // SUMMARY
        // ==========================================================
        System.out.println("\n===============================================================");
        System.out.println("TEST RESULTS SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("===============================================================");
    }
}
