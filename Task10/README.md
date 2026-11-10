# Task 10: Enforce Encapsulation through Packages and Access Modifiers

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Package Architecture

The system is organized into modular packages separating data models, business logic, and presentation:
```
parking/
├── model/       # Domain entities: Vehicle, TwoWheeler, Car, ParkingSlot, ParkingTicket
├── service/     # Business logic: ParkingService, tariff calculators
└── app/         # Execution entry point: Main
```
- **`parking.model`:** Holds domain state. Access to internal fields is strictly mediated via access modifiers and validated getters.
- **`parking.service`:** Coordinates slot operations, tariff computation, and capacity tracking. Does not directly leak internal array representations.
- **`parking.app`:** Presentation layer containing the `main()` driver method.

---

## 2. Access Modifiers Justification

| Access Modifier | System Usage | Technical Justification |
| :--- | :--- | :--- |
| **`private`** | `slots[][]`, `regNo`, `ticketId`, `parkedVehicle` | Ensures state cannot be mutated directly by external code; prevents corrupting slot arrays or bypassing business logic. |
| **`default` (package-private)** | Internal helper methods within the service layer | Allows collaborating classes inside `parking.model` to access helpers while keeping them completely invisible to outside packages. |
| **`protected`** | `ratePerHour()`, `Vehicle(...)` constructor | Allows concrete subclasses (`Car`, `TwoWheeler`) in child packages to inherit and override without exposing them publicly. |
| **`public`** | Public APIs: `parkVehicle()`, `exitVehicle()`, `getRegNo()` | Clean, documented entry points for clients of the system. |

---

## 3. Data Validation in Getters and Setters

- **Constructor / Setter Validation:**
  - `regNo`: Throws `IllegalArgumentException` if `null` or whitespace.
  - `minutes`: Rejects negative durations.
  - `occupy(Vehicle v)`: Throws `IllegalStateException` if slot is already occupied, preventing double-parking.

---

## 4. Wrapper Classes and Autoboxing Risks

Java provides wrapper classes (`Integer`, `Double`, `Boolean`) for primitives (`int`, `double`, `boolean`). While convenient for collections (`ArrayList<Integer>`), they introduce two dangerous traps:

### Pitfall 1: Reference Equality (`==`) vs Value Equality (`.equals()`)
- Java caches `Integer` instances in the range **`-128 to 127`**.
- `Integer a = 100; Integer b = 100; a == b` evaluates to `true` (same cached reference).
- `Integer x = 1000; Integer y = 1000; x == y` evaluates to **`false`**! They are distinct objects on the heap with different addresses.
- **Resolution:** Always use `.equals()` or compare primitives using `int`.

### Pitfall 2: Silent `NullPointerException` on Auto-Unboxing
- If a wrapper object is `null`, attempting to unbox it into a primitive (e.g., `int slot = nullableInteger;`) invokes `nullableInteger.intValue()`, immediately triggering an unhandled `NullPointerException`.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Private Fields with Validating Methods vs. Public Fields with Calling-Code Validation

Suppose all fields (`floor`, `slot`, `regNo`, `isOccupied`) were made `public`:

| Architectural Criterion | Private Fields with Validating Accessors | Public Fields with External Caller Validation |
| :--- | :--- | :--- |
| **Data Integrity Guarantee** | **Guaranteed:** Single source of truth. Invalid state cannot be injected under any circumstance. | **Zero Guarantee:** Any developer, external module, or buggy routine can directly execute `slot.parkedVehicle = null;` without clearing tickets. |
| **Code Duplication & Maintenance** | Single check inside the class. Update validation rule once in one place. | Every single calling class across the entire codebase must repeat duplicate `if (slot < 0)` checks before every mutation. |
| **Runtime Performance Cost** | Negligible ($\sim 1\text{ nanosecond}$ JVM JIT inlines simple getter/setter methods into direct memory reads/writes). | Identical raw instruction cost, but prone to missed checks leading to data corruption. |

**Conclusion:** Encapsulation via `private` fields and validated accessors is fundamental to defensive programming and system stability.
