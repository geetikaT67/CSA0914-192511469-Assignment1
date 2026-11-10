# Task 06: Design the Inheritance Hierarchy

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Inheritance Hierarchy Design

The system employs both **Hierarchical Inheritance** and **Multilevel Inheritance**:
1. **Hierarchical Inheritance:** `Vehicle` serves as the abstract root superclass, from which both `TwoWheeler` and `Car` inherit directly.
2. **Multilevel Inheritance:** `ElectricCar` extends `Car`, which in turn extends `Vehicle` (`ElectricCar` $\rightarrow$ `Car` $\rightarrow$ `Vehicle`).
3. **Interface Realization:** `ElectricCar` realizes the `Chargeable` interface.

```
                    ┌─────────────────────────┐
                    │      <<abstract>>       │
                    │         Vehicle         │
                    └────────────┬────────────┘
                                 │
                 ┌───────────────┴───────────────┐
                 ▼                               ▼
       ┌───────────────────┐           ┌───────────────────┐
       │    TwoWheeler     │           │        Car        │
       └───────────────────┘           └─────────┬─────────┘
                                                 │
                                                 ▼
      ┌────────────────────┐           ┌───────────────────┐
      │   <<interface>>    │◀ ─ ─ ─ ─ ─│    ElectricCar    │
      │     Chargeable     │           └───────────────────┘
      └────────────────────┘
```

---

## 2. Inherited vs. Added Features

| Class | Inherited Members | Newly Added Members |
| :--- | :--- | :--- |
| **`Vehicle`** | `java.lang.Object` methods (`toString()`, `equals()`, etc.) | `regNo`, `userType`, `getRegNo()`, `calculateFee()`, abstract `ratePerHour()` |
| **`TwoWheeler`** | `regNo`, `userType`, `getRegNo()`, `calculateFee()` | Implements `ratePerHour()` returning Rs 10.0 |
| **`Car`** | `regNo`, `userType`, `getRegNo()`, `calculateFee()` | `passengerCapacity`, implements `ratePerHour()` returning Rs 30.0 |
| **`ElectricCar`** | Everything from `Car` and `Vehicle` | `batteryCapacityKWh`, implements `Chargeable.chargingFee(kWh)` at Rs 12/kWh |

---

## 3. Usage of `super` Keyword

- `super(regNo)` in Constructors: Delegates initialization of private superclass fields (`regNo`) upwards to `Vehicle`. Must always be the very first statement in the constructor.
- `super.calculateFee(minutes)` in Methods: Allows a subclass to reuse the base fee algorithm while adding category-specific surcharges.

---

## 4. Constructor Execution Trace: `new ElectricCar("TN09EV9999", 60)`

When an `ElectricCar` object is instantiated, constructor execution proceeds strictly from superclass down to subclass (Top-to-Bottom order):

```
Step 1: main() calls new ElectricCar("TN09EV9999", 60)
         │
Step 2:  └─► ElectricCar constructor calls super("TN09EV9999", 5)
              │
Step 3:       └─► Car constructor calls super("TN09EV9999")
                   │
Step 4:            └─► Vehicle constructor executes first:
                       - Object header allocated on heap
                       - Initializes private regNo = "TN09EV9999"
                       - Outputs: "[1] Vehicle constructor executing."
                   │
Step 5:            Car constructor body resumes:
                   - Initializes passengerCapacity = 5
                   - Outputs: "[2] Car constructor executing."
              │
Step 6:  ElectricCar constructor body resumes:
         - Initializes batteryCapacityKWh = 60
         - Outputs: "[3] ElectricCar constructor executing."
```

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Inheritance (`Car is-a Vehicle`) vs. Composition (`Vehicle has-a VehicleType`)

In a Composition model, a single `Vehicle` class has a reference to a `VehicleType` strategy object:
```java
class Vehicle {
    private String regNo;
    private VehicleType type; // Strategy holding rate and charging behavior
}
```

| Criterion | Inheritance (`Car is-a Vehicle`) | Composition (`Vehicle has-a VehicleType`) |
| :--- | :--- | :--- |
| **Adding a New Vehicle Type** | Write a new subclass (`class EBike extends Vehicle`). Existing classes are untouched (Open/Closed Principle). | Instantiate a new `VehicleType` data record (`new VehicleType("EBike", 15.0, false)`). No new classes needed if rules are purely parametric. |
| **Fee Calculation Time** | $\mathcal{O}(1)$ dynamic method dispatch (vtable lookup in hardware). | $\mathcal{O}(1)$ delegation call to `type.getRate()`. |
| **Space Overhead per Vehicle Object** | 1 Object header + fields on heap. Direct flattened memory layout. | 2 Object headers: `Vehicle` object + `VehicleType` object reference pointer. |
| **Complex Category Behavior** | Superior when categories have radically different algorithmic behavior (e.g., peak-hour delivery vans vs. EV charging equations). | Superior when types only differ by numeric configuration data (rates). |

**Conclusion:** Inheritance cleanly models structural and behavioral specialization in Java, especially when subclasses introduce new capability interfaces (`Chargeable`).
