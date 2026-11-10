# Task 05: Apply static Members and the Object Life Cycle

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Static Variables vs. Instance Variables

### Why `ticketCounter` Must Be `static`
- **Definition:** A `static` variable is loaded once in the Metaspace/Class area and shared among all instances of the class.
- **Why Instance Variable is WRONG:** If `ticketCounter` were an instance variable, every newly instantiated `ParkingTicket` would receive its own independent copy initialized to `0` (or the default starting value). Every ticket issued would have the duplicate ID `#1`, violating the critical business rule that ticket numbers must be unique across the campus.

---

## 2. Static Methods and Static Blocks

- **Static Methods:** Utility functions that do not depend on the state of a specific instance (e.g., `getTotalTicketsIssued()`, `DayPass.getPrice()`). They can be called directly on the class without allocating heap memory for an object (`SimpleTicket.getTotalTicketsIssued()`).
- **Static Initialization Block (`static { ... }`):** Executes exactly once when the class is first loaded by the Java ClassLoader. Used to configure campus-wide configuration parameters (e.g., reading base tariffs, initializing campus facility metadata).

---

## 3. Object Lifecycle and Garbage Collection (GC) Eligibility

1. **Creation:** An object is born when `new ParkingTicket(...)` is executed; memory is allocated on the heap, and its reference is placed on the stack or in the facility's array.
2. **Active Life:** While the vehicle is parked, the ticket reference is retained in `CampusParkingSystem.activeTickets[]`.
3. **Exit & Dereferencing:**
   - When the vehicle departs, the slot is vacated, and the ticket reference is cleared from `activeTickets[i] = null` (or shifted).
4. **GC Eligibility:** As soon as there are no strong references (roots) pointing to the `ParkingTicket` object from stack frames or active arrays, it becomes **unreachable**. The JVM Garbage Collector (e.g., G1 GC) marks it as eligible for memory reclamation during subsequent GC sweeps.

---

## 4. Parameter Passing Mechanism in Java

- Java is **strictly pass-by-value**.
- When an object (e.g., `ParkingTicket t`) is passed to a method:
  - The *value of the reference pointer* (the memory address) is copied into the method parameter.
  - The method can mutate the contents of the object via methods (`t.closeTicket(...)`).
  - However, reassigning the parameter (`t = new Ticket(...)`) merely updates the local parameter copy on the method's stack frame; the caller's reference remains unchanged.

---

## 5. Alternative Concept & Complexity Analysis

### Case Study: Global Static Counter vs. Facility Instance Counter for Two Facilities (North & South Gates)

Suppose the university operates two separate facilities: **North Gate Facility** and **South Gate Facility**.

```
Case A: Global static int ticketCounter
┌────────────────────────────────────────────────────────┐
│ Global static ticketCounter in Metaspace (shared)      │
└───────────────────────────┬────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
   North Gate Facility             South Gate Facility
   Issues Ticket #1001             Issues Ticket #1002
   Issues Ticket #1003             Issues Ticket #1004
```

```
Case B: Instance variable int facilityTicketCounter
┌──────────────────────────────┐    ┌──────────────────────────────┐
│ North Gate Facility          │    │ South Gate Facility          │
│ instance counter: 1001, 1002 │    │ instance counter: 1001, 1002 │
└──────────────────────────────┘    └──────────────────────────────┘
  (Ticket #1001 duplicated across North and South facilities!)
```

| Metric / Scenario | Case A: Global Static Counter | Case B: Per-Facility Instance Counter | Case C: Composite Facility Prefix (`"NG-1001"`, `"SG-1001"`) |
| :--- | :--- | :--- | :--- |
| **Uniqueness across Campus** | **Guaranteed:** No two tickets share the same numeric ID anywhere on campus. | **Collision Risk:** North and South facilities both issue Ticket #1001, causing ambiguity at central exit gates. | **Guaranteed & Informative:** Combines per-facility counter with gate identifier. |
| **Space Complexity ($k$ facilities, $n$ tickets)** | $\mathcal{O}(1)$ space: exactly one 32-bit `int` stored in class metadata. | $\mathcal{O}(k)$ space: one `int` field per facility object. | $\mathcal{O}(k)$ facility counters + string prefix. |
| **Concurrency / Contention** | Can become a thread contention hotspot under high concurrency across gates (mitigated via `AtomicInteger`). | No cross-facility contention; each gate increments independently. | Independent counters with zero lock contention. |

**Recommended Solution:** In an enterprise multi-gate setup, each facility maintains an instance counter prefixed with a facility identifier (e.g., `NORTH-1001`, `SOUTH-1001`), combining zero lock contention with guaranteed global uniqueness.
