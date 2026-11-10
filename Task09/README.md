# Task 09: Introduce Abstraction

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Abstract Classes vs. Interfaces: Design Rationale

### Why `Vehicle` is an Abstract Class
- **Shared State & Identity:** Every physical vehicle inherently possesses invariant state: a license plate number (`regNo`) and user type (`userType`).
- **Constructors:** Abstract classes can have parameterized constructors that enforce mandatory field initialization upon subclass construction (`super(regNo)`).
- **Abstract Method:** `ratePerHour()` is declared `abstract` because a generic vehicle has no concept of a tariff; concrete subclasses (`TwoWheeler`, `Car`) must define their own rate.

### Why `Chargeable` and `Payable` are Interfaces
- **Peripheral Capabilities (Can-Do Relationship):** Not all vehicles consume electricity (gasoline cars and cycles do not). Charging is a capability rather than a core identity.
- **Cross-Hierarchy Applicability:** In the future, a parking slot itself or an electric lawnmower could implement `Chargeable`. Interfaces allow disparate class hierarchies to share common method signatures.

---

## 2. Multiple Inheritance of Type via Interfaces

Java forbids multiple class inheritance (`extends A, B`) to avoid the deadly Diamond Problem (state and method ambiguity). However, Java permits a single class to implement multiple interfaces (`implements Chargeable, Payable`):
- `ElectricCar` extends `Vehicle` (Single Implementation Inheritance: acquires state and constructors).
- `ElectricCar` implements `Chargeable, Payable` (Multiple Interface Realization: promises capability compliance).

---

## 3. Interface Modern Features: `default` and `static` Methods

- **Default Methods (`default double calculateFastChargingFee(int kWh)`):**
  - Allows library designers to add new capabilities to an existing interface with a fallback implementation without breaking existing implementing classes.
- **Static Methods (`static boolean isValidKWh(int kWh)`):**
  - Serves as cohesive helper/validation logic directly associated with the interface without requiring an instantiated object.

---

## 4. Alternative Concept & Complexity Analysis

### Comparison: `Vehicle` as Interface + `Chargeable` as Abstract Class (Inversion Analysis)

Suppose we inverted the design:
- `interface Vehicle` with default methods
- `abstract class Chargeable`

```
Consequences of Inverting the Design:
1. Loss of State & Constructors:
   Interfaces cannot declare instance variables (fields in interfaces are implicitly public static final constants).
   Therefore, an interface Vehicle cannot hold private final String regNo or have a constructor!
   Every single subclass (TwoWheeler, Car, DeliveryVan) would be forced to duplicate the declaration
   of regNo, its constructor, and its getter method.

2. Fragile Inheritance Hierarchy:
   If Chargeable becomes an abstract class, then only subclasses of Chargeable can be chargeable.
   Because Java restricts classes to single inheritance, a class could NOT extend both Vehicle and Chargeable!
   An ElectricCar could not inherit from both Car and Chargeable.
```

| Architectural Criterion | Current Design (`Vehicle` Abstract Class, `Chargeable` Interface) | Inverted Design (`Vehicle` Interface, `Chargeable` Abstract Class) |
| :--- | :--- | :--- |
| **Encapsulation of State** | **Clean:** `regNo` is private and centralized in `Vehicle`. | **Broken / Duplicated:** `regNo` duplicated across every implementing class. |
| **Multiple Capabilities** | **Flexible:** Vehicles implement `Chargeable`, `Payable`, etc. as needed. | **Rigid / Blocked:** Java single inheritance blocks extending multiple classes. |
| **Method Dispatch Time** | `invokevirtual`: $\mathcal{O}(1)$ via fast indexed vtable. | `invokeinterface`: slight sub-nanosecond table lookup overhead (mitigated by modern JVM inline caching). |
| **Object Space Overhead** | Compact heap layout: 1 object header + fields. | Identical heap footprint per object, but redundant code memory in Metaspace. |

**Conclusion:** Abstract classes are strictly necessary when common state and constructors must be inherited, while interfaces are ideal for modular, orthogonal capabilities.
