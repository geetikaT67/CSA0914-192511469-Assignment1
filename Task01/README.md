# Task 01: Understand the Problem and Identify the Objects

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. System Inputs and Outputs

### System Inputs
- **Vehicle Information:** Registration number (`String`, e.g., `"TN01AB1234"`), vehicle category (`TwoWheeler`, `Car`, `ElectricCar`, `EBike`, `DeliveryVan`).
- **User Classification:** User role / pass holder status (`Student Pass`, `Staff Pass`, `Visitor Hourly`, `Visitor Day Pass`).
- **Time Inputs:** Entry timestamp in minutes and Exit timestamp in minutes.
- **Service Usage:** EV charging energy consumed in kilowatt-hours (`kWh`).
- **Facility Configuration:** Total floors, number of slots per floor (flexible / jagged configuration).

### System Outputs
- **Parking Ticket:** Unique auto-incremented ticket ID, vehicle registration number, assigned floor number, slot number, entry timestamp, and active status.
- **Slot Status & Allocation Confirmation:** Confirmation of allocated slot `[floor, slot]` or `null` / error message when the facility is completely full.
- **Exit Receipt / Billing Summary:** Duration of stay (in minutes and billed integer ceiling hours), base parking fee, EV charging surcharge, pass discount waiver, net amount payable, and timestamp of exit.
- **Operational Metrics:** Daily total revenue collected, current occupancy, and count of available free slots.

---

## 2. Candidate Classes (Nouns) and Methods (Verbs)

| Identified Noun (Candidate Class) | Real-World Role | Identified Verbs (Methods) |
| :--- | :--- | :--- |
| **`Vehicle`** (Abstract) | Represents any vehicle entering campus | `getRegNo()`, `calculateFee()`, `ratePerHour()`, `equals()`, `hashCode()` |
| **`TwoWheeler`** | Subclass for motorcycles/scooters | `ratePerHour()` |
| **`Car`** | Subclass for four-wheelers | `ratePerHour()` |
| **`ElectricCar`** | Subclass for electric cars requiring charging | `chargingFee()`, `ratePerHour()` |
| **`ParkingSlot`** | Individual designated parking spot | `occupy()`, `vacate()`, `isFree()`, `getParkedVehicle()` |
| **`ParkingTicket`** | Physical/digital token issued on entry | `getTicketId()`, `closeTicket()`, `getTotalFee()`, `isActive()` |
| **`CampusParkingSystem`** | Central orchestrator of the parking facility | `parkVehicle()`, `exitVehicle()`, `isVehicleParked()`, `getFreeSlotCount()` |
| **`Chargeable`** (Interface) | Capability interface for vehicles needing power | `chargingFee(int kWh)` |

---

## 3. Class Responsibilities and Justification

1. **`Vehicle` (Abstract Base Class):**
   - *Responsibility:* Encapsulates common vehicle attributes (registration number and user category) and defines the contract for fee calculation.
   - *Justification:* Prevents redundant code across vehicle types while ensuring polymorphic handling.
2. **`ParkingSlot`:**
   - *Responsibility:* Maintains state of an individual physical slot (floor index, slot index, and vehicle reference).
   - *Justification:* Separates physical layout modeling from business pricing rules.
3. **`ParkingTicket`:**
   - *Responsibility:* Tracks the transaction lifecycle (unique ticket ID, allocated slot, entry and exit times, total fee).
   - *Justification:* Immutable proof of entry required for audit trails, fee computation, and lost ticket prevention.
4. **`CampusParkingSystem`:**
   - *Responsibility:* Manages overall multi-level slot allocation, ticket issuance, vehicle release, and daily revenue accounting.
   - *Justification:* Serves as the central coordinator (Facade) preventing direct, uncontrolled manipulation of internal slot arrays.

---

## 4. Business Rules and Facility Assumptions

### Business Rules
1. **Single Occupancy:** A parking slot can never hold more than one vehicle simultaneously.
2. **Unique Ticket ID:** Ticket numbers are generated sequentially via a shared atomic/static counter and are never reused.
3. **Pass Exemption:** Students and staff holding valid monthly passes park free of charge (parking fee = Rs 0.00). EV charging is billed separately if consumed.
4. **Hour Ceiling Rule:** Every started hour is charged in full (e.g., 61 minutes is billed as 2 full hours) using integer arithmetic `(minutes + 59) / 60`.
5. **Temporal Validity:** A vehicle cannot exit before its entry timestamp (`exitTime >= entryTime`).
6. **No Duplicate Parking:** A vehicle with an active ticket inside the facility cannot enter again until it exits.

### Facility Assumptions
- Floors can have different capacities (jagged array structure).
- Entry and exit timestamps are recorded in integer minutes since midnight (0–1439).
- Parking allocation follows a lowest-floor, lowest-slot index first search strategy.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Object-Oriented Design vs. Parallel Arrays

Instead of modeling vehicles as objects (`Vehicle v = new Car(...)`), suppose vehicles were stored using primitive parallel arrays:
```java
String[] regNo = new String[MAX];
String[] type = new String[MAX];
int[] entryTime = new int[MAX];
```

| Criterion | Object-Oriented Design (`Vehicle` objects) | Parallel Arrays (`regNo[]`, `type[]`, `entryTime[]`) |
| :--- | :--- | :--- |
| **Data Cohesion** | High: attributes and behaviors are encapsulated in a single cohesive unit. | Very Low: vehicle attributes are fragmented across disconnected arrays; indices must stay manually synchronized. |
| **Adding New Attribute (e.g. `isElectric`)** | Add one private field to `Vehicle` or create a subclass/interface. Zero changes to caller search logic. | Must declare a new array `boolean[] isElectric = new boolean[MAX];` and update all insert, delete, and shift routines. |
| **Search by RegNo Time Complexity** | $\mathcal{O}(n)$ linear scan over object array (or $\mathcal{O}(1)$ with `HashMap`). | $\mathcal{O}(n)$ linear scan over `regNo[]`. |
| **Space Complexity per Vehicle** | $\sim 24\text{–}32\text{ bytes}$ object overhead + references on heap. Memory is grouped together. | 3 separate array references + array elements. Higher risk of memory fragmentation and off-by-one corruption during deletions. |
| **Extensibility & Polymorphism** | Polymorphic dispatch $\mathcal{O}(1)$ without conditional branches. | Requires brittle `if-else` or `switch` chains matching string types. |

**Conclusion:** The object-oriented approach guarantees data integrity, eliminates index-synchronization bugs, and makes extending vehicle categories seamless.
