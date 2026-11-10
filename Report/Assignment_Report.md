# ASSIGNMENT 1 (CO1) REPORT
## Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System

---

### Student & Course Metadata
- **Course:** Programming in Java
- **Course Outcome:** CO1 (Java fundamentals, control statements, OOP, inheritance, polymorphism, abstraction, packages, encapsulation)
- **Student Register Number:** 192511469
- **Repository Name:** CSA0914-192511469-Assignment1
- **GitHub Repository Link:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment1](https://github.com/geetikaT67/CSA0914-192511469-Assignment1)
- **Java Development Kit:** Java SE 25 LTS (build 25.0.4.1)
- **Operating System:** Windows 11

---

## Executive Summary
This report presents the analysis, object-oriented architectural design, complete implementation, and rigorous experimental validation of a modular **Campus Smart-Parking System**. 

The system models a multi-level campus parking facility supporting varied vehicle types (two-wheelers, gasoline cars, electric vehicles, e-bikes, and commercial delivery vans) and user classifications (student and staff monthly pass holders, hourly visitors, and day pass purchasers). Every task (Task 01 through Task 12), including all mandatory "Complete the Code" challenges, has been fully executed, validated, and documented according to the SIMATS Engineering standards.

---

## Task 01: Understand the Problem and Identify the Objects

### 1.1 Problem Analysis and Candidate Identification
- **Inputs:** Vehicle registration plate (`String`), category (`TwoWheeler`, `Car`, `ElectricCar`, `EBike`, `DeliveryVan`), user role (`Student Pass`, `Staff Pass`, `Visitor`), entry and exit timestamps (minutes since midnight), and EV energy consumption (`kWh`).
- **Outputs:** Unique numerical parking ticket token, physical slot coordinate `[floor, slot]`, itemized billing receipt, and facility metrics (revenue, occupancy).
- **Candidate Classes (Nouns):** `Vehicle` (abstract), `TwoWheeler`, `Car`, `ElectricCar`, `EBike`, `DeliveryVan`, `ParkingSlot`, `ParkingTicket`, `CampusParkingSystem`.
- **Candidate Methods (Verbs):** `parkVehicle()`, `exitVehicle()`, `calculateFee()`, `ratePerHour()`, `chargingFee()`, `occupy()`, `vacate()`.

### 1.2 Business Rules and Assumptions
1. **Single Occupancy Invariant:** No slot can hold multiple vehicles concurrently.
2. **Sequential Unique Tokens:** Ticket IDs are generated monotonically and never recycled.
3. **Pass Exemption:** Verified student/staff monthly pass holders incur Rs 0.00 base parking fees.
4. **Integer Hour Ceiling:** Any started hour is billed in full (`(minutes + 59) / 60`).
5. **Temporal Validity:** Exit time must be greater than or equal to entry time.

### 1.3 Alternative Concept & Complexity Analysis: Objects vs. Parallel Arrays
Storing vehicles in parallel arrays (`String[] regNo`, `String[] type`, `int[] entryTime`) fragments state across decoupled linear arrays. Adding an attribute requires creating a new array across the codebase. Searching by registration requires an $\mathcal{O}(n)$ linear scan in both, but parallel arrays lack data encapsulation, risk desynchronization during deletions, and prevent polymorphic method dispatch.

---

## Task 02: Choose the Data Representation

### 2.1 Field Types and Multi-Level Floor Layout
- `slotNumber`: `int` (compact non-negative index).
- `vehicleNumber`: `String` (immutable alphanumeric representation).
- `entryTime` / `duration`: `int` (elapsed minutes; avoids floating-point inaccuracies).
- `fee`: `double` (currency requiring fractional representation).
- **Array Layout:** **Jagged Array (`String[][] slots = new String[floors][]`)** was selected because parking structures often feature variable capacities per floor (e.g., ground floor holding 50 slots, top terrace holding 20).

### 2.2 Casting and Precision Traps
Naive integer division `duration / 60` for 75 minutes truncates to `1` hour, underbilling the client by Rs 30.00. The correct integer arithmetic ceiling `(duration + 59) / 60` evaluates $(75 + 59) / 60 = 134 / 60 = 2$ hours without floating-point cast bugs.

### 2.3 Alternative Analysis: Flat 1D Array vs. Jagged Array vs. `ArrayList<String[]>`
A flat 1D array using `index = floor * S + slot` requires uniform floor capacities and wastes memory when layouts vary. `ArrayList<String[]>` provides dynamic resizing but introduces object wrapping overhead. The jagged array provides direct $\mathcal{O}(1)$ slot access with minimal memory footprint.

---

## Task 03: Design the Control Logic & Complete the Code

### 3.1 Pseudocode (SIMATS Engineering Standard)
```text
BEGIN ALGORITHM AllocateSlot
    INPUT: vehicleNo (STRING), slots (2D ARRAY of STRING)
    OUTPUT: [floor, slot] OR NULL
    FOR f FROM 0 TO LENGTH(slots) - 1 DO
        FOR s FROM 0 TO LENGTH(slots[f]) - 1 DO
            IF slots[f][s] IS NULL THEN
                SET slots[f][s] = vehicleNo
                RETURN ARRAY [f, s]
            END IF
        END FOR
    END FOR
    RETURN NULL
END ALGORITHM
```

### 3.2 Completed Code Execution (`ParkingFacility.java`)
```java
// Implemented allocate() and release() methods
public int[] allocate(String vehicleNo) {
    for (int f = 0; f < slots.length; f++) {
        for (int s = 0; s < slots[f].length; s++) {
            if (slots[f][s] == null) {
                slots[f][s] = vehicleNo;
                return new int[]{f, s};
            }
        }
    }
    return null;
}

public boolean release(int floor, int slot) {
    if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) return false;
    if (slots[floor][slot] == null) return false;
    slots[floor][slot] = null;
    return true;
}
```
**Verified Output:**
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```

### 3.3 Complexity Analysis: Linear Scan vs. Free-Slot Stack
The linear scan requires $\mathcal{O}(F \times S)$ in the worst-case and $\mathcal{O}(1)$ auxiliary space. Maintaining a stack of free slot indices per floor achieves $\mathcal{O}(1)$ allocation and release time, but consumes $\mathcal{O}(F \times S)$ auxiliary memory and introduces synchronization overhead.

---

## Task 04: Design Classes, Objects and Constructors

### 4.1 Constructors and Chaining
- Parameterized constructors ensure object state invariants are established at instantiation.
- Constructor chaining (`this(...)`) funnels secondary constructors into a single primary constructor, eliminating code duplication.
- **Copy Constructor (`Ticket(Ticket original)`):** Essential for issuing verified duplicate tickets for lost tokens without mutating the master ticket or reserving duplicate slots.

### 4.2 Stack vs. Heap Memory Model
Local reference variables (`Ticket t1`) reside on the thread execution stack, while instantiated objects (`Ticket` instance headers and payload data) reside in the JVM Heap.

---

## Task 05: Apply static Members and the Object Life Cycle

### 5.1 Static Members
- `ticketCounter` must be `static` so that it is shared globally across all ticket instances. An instance variable would reset to its initial value on every `new Ticket()` call, causing duplicate ticket IDs.
- Static blocks initialize campus-wide configuration parameters upon class loading.

### 5.2 Object Lifecycle & Parameter Passing
Objects are allocated on the heap and remain reachable while referenced in `CampusParkingSystem.activeTickets[]`. Upon vehicle departure, clearing the array slot leaves the object unreachable, making it eligible for garbage collection. Java operates strictly by **pass-by-value**; passing an object copies the reference address, leaving the caller's reference variable immutable even if the method parameter is reassigned.

### 5.3 Multi-Facility Analysis (North vs. South Gates)
For multiple independent parking facilities, a single global static counter introduces lock contention across gates. The optimal enterprise architecture assigns an instance counter to each facility prefixed with a gate code (`NG-1001`, `SG-1001`), ensuring both localized concurrency and global uniqueness.

---

## Task 06: Design the Inheritance Hierarchy

### 6.1 Hierarchy Diagram
```
                     ┌──────────────────────────┐
                     │       <<abstract>>       │
                     │          Vehicle         │
                     └─────────────┬────────────┘
                                   │
                 ┌─────────────────┴─────────────────┐
                 ▼                                   ▼
        ┌──────────────────┐               ┌──────────────────┐
        │    TwoWheeler    │               │       Car        │
        └──────────────────┘               └─────────┬────────┘
                                                     │
                                                     ▼
       ┌───────────────────┐               ┌──────────────────┐
       │   <<interface>>   │◀ ─ ─ ─ ─ ─ ─ ─│   ElectricCar    │
       │    Chargeable     │               └──────────────────┘
       └───────────────────┘
```

### 6.2 Constructor Execution Trace: `new ElectricCar("TN09EV9999", 60)`
1. `ElectricCar` constructor receives arguments and delegates to `super(regNo, 5)`.
2. `Car` constructor receives arguments and delegates to `super(regNo)`.
3. `Vehicle` constructor executes first on the heap, setting `regNo = "TN09EV9999"`.
4. `Car` constructor body executes next, setting `passengerCapacity = 5`.
5. `ElectricCar` constructor body completes, setting `batteryCapacityKWh = 60`.

---

## Task 07: Override and Specialise Behaviour

### 7.1 Method Overriding & Rules
- `ratePerHour()` is overridden in subclasses to provide specific tariffs (TwoWheeler: Rs 10, Car: Rs 30).
- Rules strictly observed: identical signature, covariant return type, non-restrictive access modifiers, and verified via `@Override`.
- `final` is applied to `regNo` and `getRegNo()` to prevent unauthorized tampering.

### 7.2 Object Contract: `equals()`, `hashCode()`, and `toString()`
Two `Vehicle` objects are defined as equal if and only if their registration plate strings match (`regNo.equalsIgnoreCase(...)`). Consistent with Java's contract, `hashCode()` is derived directly from `regNo.toUpperCase().hashCode()`, ensuring correct deduplication inside hashing collections (`HashSet`).

---

## Task 08: Apply Polymorphism & Complete the Code

### 8.1 Polymorphic Execution
- Upcasting allows a heterogeneous `Vehicle[]` array to store `TwoWheeler`, `Car`, and `ElectricCar` references.
- Dynamic method dispatch executes the appropriate subclass implementation via the JVM virtual method table (`vtable`) in $\mathcal{O}(1)$ time.

### 8.2 FeeDemo Execution Verification
```java
// Completed calculateFee with integer arithmetic ceiling
public double calculateFee(int minutes) {
    int hours = (minutes + 59) / 60;
    return hours * ratePerHour();
}
```
**Verified Output:**
```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```
- `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` evaluates to `true` because `ElectricCar` is-a `Vehicle` and both share identical registration plates. This correctly reflects real-world vehicle identity.

---

## Task 09: Introduce Abstraction

### 9.1 Abstract Class vs. Interface Rationale
- `Vehicle` is an **abstract class** because all vehicles share core state (`regNo`, `userType`) and require a common constructor.
- `Chargeable` and `Payable` are **interfaces** because they represent modular capabilities that apply only to specific vehicles.

### 9.2 Inversion Analysis
If `Vehicle` were an interface and `Chargeable` an abstract class, `Vehicle` could not encapsulate state or constructors, forcing massive code duplication across all subclasses. Furthermore, Java's single class inheritance restriction would prevent an electric car from extending both `Car` and `Chargeable`.

---

## Task 10: Enforce Encapsulation through Packages & Modifiers

### 10.1 Modular Architecture
- `parking.model`: Domain entities (`Vehicle`, `TwoWheeler`, `Car`, `ParkingSlot`).
- `parking.service`: Business service coordinating allocations (`ParkingService`).
- `parking.app`: Presentation and runtime execution (`Main`).

### 10.2 Autoboxing Hazards
In Java, wrapper objects in the range `-128 to 127` are cached. Comparing `Integer` objects outside this range (e.g., `id1 == id2` for value 1000) evaluates to `false` because `==` performs reference identity checking. In addition, auto-unboxing a `null` wrapper object triggers a `NullPointerException`. Value equality must always use `.equals()`.

---

## Task 11: Implement, Test and Defend the Design

### 11.1 Test Suite Matrix (100% Passed)
| Test ID | Category | Description | Result |
| :---: | :--- | :--- | :---: |
| TC-01 to TC-04 | Normal Flow | Entry and slot allocation for TwoWheeler, Car, EV, Student Pass | **PASS** |
| TC-05 | Boundary | Facility full rejection (allocation returns null) | **PASS** |
| TC-06 | Boundary | Exact 120-minute slab boundary (2 hours @ Rs 30 = Rs 60.00) | **PASS** |
| TC-07 | Boundary | Zero-minute stay billed as Rs 0.00 | **PASS** |
| TC-08 | Boundary | 105-minute stay billed as 2 full hours (Rs 20.00) | **PASS** |
| TC-09 | Exemption | Monthly pass holder parked free (Rs 0.00) | **PASS** |
| TC-10 | Capability | EV charging computation (30 min parking + 15 kWh = Rs 210.00) | **PASS** |
| TC-11 | Error Handling| Duplicate vehicle registration entry rejected | **PASS** |
| TC-12 | Error Handling| Non-existent ticket ID exit safely rejected (-1.0) | **PASS** |
| TC-13 | Error Handling| Exit time preceding entry time rejected (-1.0) | **PASS** |
| TC-14 | Audit | Hand-calculated revenue reconciliation matches exactly (Rs 320.00) | **PASS** |

### 11.2 Hand-Calculated Revenue Proof
$$\text{Expected Total} = \text{Rs } 20.00 (\text{Bike}) + \text{Rs } 60.00 (\text{Car}) + \text{Rs } 210.00 (\text{EV}) + \text{Rs } 0.00 (\text{Pass}) + \text{Rs } 0.00 (\text{Zero}) + \text{Rs } 30.00 (\text{Cleanup}) = \mathbf{\text{Rs } 320.00}$$
System output matched Rs 320.00 exactly.

### 11.3 Alternative Ticket Storage Complexity
Active tickets stored in primitive arrays require $\mathcal{O}(n)$ search and removal time. Refactoring to a `HashMap<String, ParkingTicket>` keyed by registration plate slashes exit lookup to $\mathcal{O}(1)$ average time, ideal for large-scale enterprise deployments.

---

## Task 12: Day-4 Design Challenge – Re-evaluate and Adapt

### 12.1 Impact Analysis
- Added `EBike` (charged a flat fee of Rs 15.00).
- Added `DeliveryVan` (charged at double the car rate, Rs 60.00/hr, during peak commercial hours 9:00 AM – 5:00 PM).
- Added `DayPass` purchase option for visitors (prepaid flat fee of Rs 80.00 with Rs 0.00 exit charge).
- Expanded facility to 5 non-uniform floors (`int[] slotsPerFloor = {3, 5, 2, 4, 3}`).

### 12.2 Design Defense
- **Zero Regression:** Existing tested classes (`TwoWheeler`, `Car`, `ElectricCar`) were preserved without modification.
- **Open/Closed Principle:** New vehicles were integrated purely by adding subclasses (`EBike`, `DeliveryVan`) overriding `calculateFee(entry, exit)`.
- **Validation:** All 6 tests in `Task12TestSuite` passed with 100% accuracy.

---

## Compilation and Execution Quick Reference

```powershell
# Task 03: Complete the Code (ParkingFacility)
javac CompleteTheCode\ParkingFacility.java
java -cp CompleteTheCode ParkingFacility

# Task 08: Complete the Code (FeeDemo)
javac CompleteTheCode\FeeDemo.java
java -cp CompleteTheCode FeeDemo

# Task 10: Packages and Encapsulation
javac -d Task10\bin (Get-ChildItem -Path Task10\src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
java -cp Task10\bin parking.app.Main

# Task 11: Complete Campus Parking System and Test Suite
javac -d Task11 Task11\CampusParkingSystem.java Task11\ParkingSystemTestSuite.java
java -cp Task11 ParkingSystemTestSuite

# Task 12: Adapted Parking System (Day-4 Challenge)
javac -d Task12 Task12\AdaptedParkingSystem.java Task12\Task12TestSuite.java
java -cp Task12 Task12TestSuite
```

---

## Viva Voce Verification Questions & Answers

1. **Q: Why is integer ceiling `(minutes + 59) / 60` preferred over `Math.ceil()`?**  
   *A:* `Math.ceil()` requires floating-point division `(minutes / 60.0)` which introduces IEEE 754 precision inaccuracies. Integer arithmetic is deterministic, faster, and immune to rounding drift.
2. **Q: Why does `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` return `true`?**  
   *A:* Because `equals()` uses `o instanceof Vehicle` and compares registration strings. In real life, two objects with the same license plate represent the same physical vehicle.
3. **Q: Why is `ticketCounter` static?**  
   *A:* To provide a single shared sequential counter across all instances. If it were an instance variable, each ticket would reset to 1.
4. **Q: Why use a jagged array for parking floors?**  
   *A:* Different floors have different physical dimensions and slot counts. A jagged array allows each floor sub-array to be sized individually without wasting memory.
