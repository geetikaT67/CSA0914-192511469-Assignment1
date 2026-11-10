# Task 04: Design Classes, Objects and Constructors

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Class Design: Fields and Methods

### `ParkingTicket`
- **Fields:**
  - `ticketCounter` (`static int`): Global sequential generator.
  - `ticketId` (`final int`): Immutable unique ID.
  - `vehicleNo` (`final String`): Vehicle registration.
  - `floor` (`final int`), `slot` (`final int`): Position indices.
  - `entryTimeMinutes` (`final int`): Entry timestamp.
  - `exitTimeMinutes` (`int`), `totalFee` (`double`), `active` (`boolean`): Mutable exit fields.
- **Methods:** Getters for all fields, `closeTicket(exitTime, fee)`, `toString()`.

### `ParkingSlot`
- **Fields:** `floor` (`final int`), `slotNumber` (`final int`), `parkedVehicle` (`Vehicle`).
- **Methods:** `occupy(Vehicle)`, `vacate()`, `isFree()`, `getParkedVehicle()`.

---

## 2. Constructor Types and When Each is Used

1. **Default / No-Arg Constructor:** Not provided for `Ticket` or `Vehicle` because an uninitialized vehicle or ticket without a registration number is invalid in this domain.
2. **Parameterized Constructor:** The primary constructor initializing all essential invariant state (`regNo`, `userType`, `floor`, `slot`).
3. **Overloaded Constructors:** Provided to furnish sensible defaults (e.g., defaulting user role to `VISITOR` or defaulting entry time to `0`).
4. **Constructor Chaining using `this(...)`:**
   - Calls the primary constructor from overloaded variants.
   - **Crucial Benefit:** Avoids duplicated validation logic and ensures all objects funnel through a single point of validation.

---

## 3. Copy Constructor Analysis

- **Definition:** A constructor that initializes a new object by copying the field values from an existing object of the same class (`Ticket(Ticket original)`).
- **Use Case in Parking System:**
  - **Re-issuing Lost Tickets:** When a student or visitor loses their paper receipt, the system can clone the active ticket data to issue a verified duplicate without mutating the original or allocating a new slot.
  - **Audit Snapshot:** Freezes ticket state at exit before persistence or archiving.

---

## 4. Memory Storage Model: Stack vs. Heap

```
STACK MEMORY                             HEAP MEMORY
[main() frame]
┌─────────────────────────┐              ┌──────────────────────────────────────┐
│ Ticket t1 (ref: @0x10A) │ ───────────> │ Ticket Object (@0x10A)               │
│                         │              │  ticketId: 1001                      │
│ Ticket t2 (ref: @0x20B) │ ───┐          │  vehicleNo: @0x30C ("TN01A1000")     │
│                         │    │          │  floor: 0, slot: 1                   │
│ Ticket t1Dup (@0x40D)   │ ─┐ │          └──────────────────────────────────────┘
└─────────────────────────┘  │ │         ┌──────────────────────────────────────┐
                             │ └───────> │ Ticket Object (@0x20B)               │
                             │           │  ticketId: 1002, floor: 1, slot: 2   │
                             │           └──────────────────────────────────────┘
                             │           ┌──────────────────────────────────────┐
                             └─────────> │ Ticket Duplicate Object (@0x40D)     │
                                         │  ticketId: 1001 (cloned attributes)  │
                                         └──────────────────────────────────────┘
```
- **Stack Memory:** Allocates local primitive variables and object reference pointers (`t1`, `t2`, `t1Dup`). Cleared automatically when the calling method exits.
- **Heap Memory:** Allocates the actual `Ticket` objects and `String` literal pools dynamically via the `new` operator. Managed by Java Garbage Collection.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Constructor Chaining vs. Default Constructor + Setters vs. Static Factory Methods

| Criterion | Overloaded Constructors with Chaining (`this(...)`) | Default Constructor + Setters | Static Factory Methods (`Ticket.createVisitorTicket(...)`) |
| :--- | :--- | :--- | :--- |
| **Object Invariant Guarantee** | **Guaranteed:** Object cannot exist in an uninitialized or invalid state; fields can be marked `final`. | **Weak / Unsafe:** Object can be in a half-initialized state if caller forgets a setter call. Cannot use `final` fields. | **Guaranteed:** Invariants validated before `new Ticket(...)` is invoked; descriptive method names. |
| **Code Verbosity** | Minimal and clean. | High (requires repetitive `.setFloor()`, `.setSlot()`, etc.). | Medium (requires private constructor + public static factory methods). |
| **Time to Create $n$ Objects** | $\mathcal{O}(n)$ direct single-call object instantiation. | $\mathcal{O}(n \times k)$ where $k$ is the number of setters called per object. | $\mathcal{O}(n)$ direct instantiation (JIT inlines static factories). |
| **Memory / Space per Object** | $\mathcal{O}(1)$ object header + primitive/reference fields. | $\mathcal{O}(1)$ object fields (fields cannot be `final`, potentially inhibiting compiler optimization). | $\mathcal{O}(1)$ identical heap footprint to constructor. |

**Conclusion:** Parameterized constructors with chaining enforce immutability (`final`), eliminate incomplete objects, and minimize method call overhead.
