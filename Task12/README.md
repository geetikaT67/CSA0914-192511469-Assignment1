# Task 12: Day-4 Design Challenge – Re-evaluate and Adapt the Design

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K5 – Evaluate

---

## A. Impact Analysis (2 Marks)

### 1. What Has Changed from the Original Problem?
1. **Two New Vehicle Categories:**
   - **E-Bikes:** Charged a flat fee (Rs 15.00) regardless of the parking duration.
   - **Delivery Vans:** Time-differentiated pricing: charged at **double the standard car rate** (Rs 60.00/hr) during peak commercial delivery hours (**9:00 AM to 5:00 PM / 540 to 1020 minutes**), and at standard car rate (Rs 30.00/hr) during off-peak hours.
2. **Visitor One-Day Pass:** Visitors can prepay a fixed one-day pass (Rs 80.00) at vehicle entry to enjoy unlimited parking duration with zero hourly fee upon exit.
3. **Facility Geometry Expansion:** Facility expanded from 3 uniform floors to **5 non-uniform floors** with variable slot counts per floor.

### 2. Affected Classes, Methods, and Data Structures
- **`Vehicle`:** Fee method signature updated from `calculateFee(int minutes)` to `calculateFee(int entryMinute, int exitMinute)` to provide temporal context for peak-time calculations.
- **`UserCategory` Enum / Pass Logic:** Expanded to include `VISITOR_DAY_PASS`.
- **`CampusParkingSystem`:** Slot array capacity expanded to accommodate jagged allocation across 5 floors (`int[] slotsPerFloor = {3, 5, 2, 4, 3}`).

### 3. Impact of Non-Uniform Slots on Array Design
- Rectangular 2D arrays (`new Slot[5][maxSlots]`) are invalid because floors have different physical capacities; using a rectangular array would result in phantom non-existent slots and wasted heap pointers.
- A **Jagged 2D Array (`new Slot[5][]`)** where each floor array is sized individually (`slots[f] = new Slot[slotsPerFloor[f]]`) accommodates non-uniform floors naturally with zero memory waste.

### 4. Classes Modified vs. Classes Extended
- **Modified:** `Vehicle` (updated parameter list for temporal fee calculation) and `UserCategory` (added day pass).
- **Extended:** Added `EBike extends Vehicle` and `DeliveryVan extends Vehicle` without touching `TwoWheeler` or `Car`.

---

## B. Evaluation of the Original Design (2 Marks)

| Evaluation Question | Technical Assessment |
| :--- | :--- |
| **Can new vehicle types be added only by writing new subclasses?** | **Yes.** Thanks to the abstract base class `Vehicle`, adding `EBike` and `DeliveryVan` was achieved purely by creating new subclasses overriding `calculateFee(...)`. |
| **Are pricing rules spread across many classes or kept in one place?** | Each vehicle subclass encapsulates its own category pricing rule (`ratePerHour()` and `calculateFee()`). Peak delivery logic belongs to `DeliveryVan`, while flat rate logic belongs to `EBike`. |
| **Can existing tested classes remain unchanged?** | Existing tested classes (`TwoWheeler`, `Car`, `ElectricCar`) required zero logic alterations. |

---

## C. Adaptation or Redesign (2 Marks)

### Architecture Decision: **MODIFIED & EXTENDED**
Rather than discarding the tested architecture, the system was cleanly adapted:
1. **`EBike` Subclass:**
   ```java
   class EBike extends Vehicle {
       private static final double FLAT_FEE = 15.0;
       @Override
       public double calculateFee(int entry, int exit) {
           return hasFreeParkingPass() ? 0.0 : FLAT_FEE;
       }
   }
   ```
2. **`DeliveryVan` Subclass:**
   ```java
   class DeliveryVan extends Vehicle {
       // Peak window: 9 AM (540 min) to 5 PM (1020 min)
       private static final double PEAK_RATE = 60.0;    // Double car rate
       private static final double REGULAR_RATE = 30.0; // Standard car rate
       @Override
       public double calculateFee(int entry, int exit) {
           if (hasFreeParkingPass()) return 0.0;
           boolean overlapsPeak = (entry < 1020 && exit > 540);
           int hours = ((exit - entry) + 59) / 60;
           return hours * (overlapsPeak ? PEAK_RATE : REGULAR_RATE);
       }
   }
   ```
3. **One-Day Pass Mechanism:** Integrated via `DayPass` static pricing (Rs 80.00) and `UserCategory.VISITOR_DAY_PASS`.

---

## D. Evidence-Based Defence (2 Marks)

### 1. Minimal Blast Radius
- Only 1 core class (`Vehicle`) had its parameter list enriched; 0 existing vehicle subclasses had their internal billing logic altered.

### 2. Extensibility for Future Vehicle Types
- Adding a future category (e.g., `HeavyBus` with axle surcharge) requires authoring exactly 1 new subclass adhering to the `Vehicle` contract, with zero modifications to the allocation engine.

### 3. Verification Test Evidence (`Task12TestSuite.java`)
```
==================================================================
     TASK 12: DAY-4 DESIGN CHALLENGE - ADAPTED SYSTEM TEST SUITE   
==================================================================
--- 1. Validating 5-Floor Facility with Jagged Slot Allocation ---
[PASS] 5 Floors successfully initialized with custom slot counts per floor.

--- 2. Validating E-Bike Flat Fee Requirement ---
[PASS] E-Bike short stay (30 mins) charged exact flat fee: Rs 15.00
[PASS] E-Bike long stay (300 mins / 5 hrs) charged exact flat fee: Rs 15.00

--- 3. Validating Delivery Van Peak Rate (9 AM - 5 PM) Requirement ---
[PASS] Delivery Van during peak hours (2 hrs @ Rs 60/hr) correctly charged: Rs 120.00
[PASS] Delivery Van outside peak hours (2 hrs @ standard Rs 30/hr) correctly charged: Rs 60.00

--- 4. Validating Visitor One-Day Pass Requirement ---
[PASS] One-Day Pass holder parked for 8 hours with Rs 0 exit fee (Pass prepaid Rs 80.00).

==================================================================
TASK 12 TEST RESULTS: 6 PASSED, 0 FAILED
Total Revenue Collected in Task 12: Rs 290.0
==================================================================
```

### 4. Architectural Trade-offs
- Passing `(entryMinute, exitMinute)` instead of `durationMinutes` increases parameter count by one integer, but unlocks time-of-day peak pricing, surge tariffs, and overnight penalty calculations without architectural refactoring.
