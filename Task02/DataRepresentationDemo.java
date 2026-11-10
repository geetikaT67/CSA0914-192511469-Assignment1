public class DataRepresentationDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 02: Data Representation and Precision Demonstration ===\n");

        // 1. Data Types Demonstration
        String regNo = "TN09AB1234";    // Vehicle identifier (String for alphanumeric plate)
        int slotNumber = 5;             // Non-negative integer index
        int entryTimeMinutes = 600;     // 10:00 AM in minutes since midnight
        int exitTimeMinutes = 675;      // 11:15 AM in minutes
        int durationMinutes = exitTimeMinutes - entryTimeMinutes; // 75 minutes
        double ratePerHour = 30.0;      // Currency/rate requires double for precision

        System.out.println("Vehicle Number: " + regNo);
        System.out.println("Duration: " + durationMinutes + " minutes");

        // 2. Demonstrating Integer Division Bug vs Correct Integer Ceiling
        System.out.println("\n--- Demonstrating Integer Division Pitfall ---");
        // BUGGY CASE: Simple integer division truncates fractional hours!
        int wrongBilledHours = durationMinutes / 60; // 75 / 60 = 1 hour (Lost 15 minutes!)
        double wrongFee = wrongBilledHours * ratePerHour; // 1 * 30.0 = Rs 30.00 (INCORRECT!)

        // CORRECT CASE: Integer ceiling arithmetic
        int correctBilledHours = (durationMinutes + 59) / 60; // (75 + 59) / 60 = 134 / 60 = 2 hours
        double correctFee = correctBilledHours * ratePerHour; // 2 * 30.0 = Rs 60.00 (CORRECT!)

        System.out.println("Naive Integer Division hours (duration / 60): " + wrongBilledHours + " hr -> Fee: Rs " + wrongFee + " [BUG: Underbilled]");
        System.out.println("Correct Integer Ceiling ((duration + 59) / 60): " + correctBilledHours + " hrs -> Fee: Rs " + correctFee + " [CORRECT]");

        // 3. Jagged Array Demonstration for Variable Floor Capacities
        System.out.println("\n--- Multi-Level Jagged Array Demonstration ---");
        int[] slotsPerFloor = {4, 6, 3}; // Floor 0: 4 slots, Floor 1: 6 slots, Floor 2: 3 slots
        String[][] facilitySlots = new String[slotsPerFloor.length][];

        for (int f = 0; f < slotsPerFloor.length; f++) {
            facilitySlots[f] = new String[slotsPerFloor[f]];
            System.out.println("Floor " + f + " initialized with " + facilitySlots[f].length + " slots.");
        }

        // Direct slot access: Floor 1, Slot 4
        facilitySlots[1][4] = regNo;
        System.out.println("Assigned " + regNo + " to facilitySlots[1][4] = " + facilitySlots[1][4]);
    }
}
