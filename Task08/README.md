# Task 08: Apply Polymorphism

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Compile-Time vs. Runtime Polymorphism

### 1.1 Compile-Time Polymorphism (Method Overloading)
- **Concept:** Multiple methods in the same class share the same name but differ in their parameter signatures (number, types, or order of arguments).
- **Selection:** Resolved at compile-time (*static binding*) by the compiler based strictly on argument types:
  ```java
  // Overloaded method variants:
  public double calculateFee(int durationMinutes);
  public double calculateFee(java.time.LocalTime in, java.time.LocalTime out);
  ```
  When the caller passes `calculateFee(130)`, the compiler binds directly to the `int` signature during compilation.

### 1.2 Runtime Polymorphism (Dynamic Method Dispatch)
- **Upcasting:** A superclass reference array (`Vehicle[] parked = { new TwoWheeler(...), new Car(...), new ElectricCar(...) };`) stores references to various subclass objects.
- **Dynamic Dispatch:** When iterating with `for (Vehicle v : parked) { v.calculateFee(130); }`, the JVM looks up the actual runtime class in the object header's virtual method table (`vtable`) and invokes the subclass's implementation.

---

## 2. Dynamic Method Dispatch Call Trace

Let us trace the method invocation for `parked[2]` (`ElectricCar`):

```
1. Declared (Static) Type:      Vehicle
2. Actual (Runtime) Object:     ElectricCar (heap object created by `new ElectricCar("TN03C3")`)
3. Method Call:                 v.calculateFee(130)
4. Dispatch Mechanism:          JVM inspects v's vtable -> jumps to Vehicle.calculateFee(int)
5. Hook Execution:              Vehicle.calculateFee() calls this.ratePerHour()
6. Dynamic Resolution:          'this' resolves to ElectricCar -> Car.ratePerHour() returning 30.0
7. EV Interface Check:          v instanceof Chargeable c -> true -> c.chargingFee(10) returns 120.0
8. Final Computed Fee:          90.0 + 120.0 = Rs 210.0
```

---

## 3. Explanation of `equals()` Result on Page 15

In `FeeDemo.java`, the following statement is executed:
```java
System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2"))); // PRINTS: true
```

### Why it Prints `true`:
The `equals()` method in `Vehicle` is defined as:
```java
@Override
public boolean equals(Object o) {
    return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
}
```
1. `new ElectricCar("TN02B2")` is an instance of `Vehicle` (because `ElectricCar` extends `Car`, which extends `Vehicle`). Hence `o instanceof Vehicle` evaluates to `true`.
2. Both objects have identical registration strings (`"TN02B2"`). Hence `regNo.equals(...)` evaluates to `true`.

### Is this the Correct Behavior for our System?
**Yes, absolutely.** In a physical parking facility, the license plate number is the unique identity of the physical transport unit. A vehicle cannot be simultaneously parked as both a gas car and an electric car under the same government-issued registration number. Equating them prevents fraudulent or duplicate slot allocations.

---

## 4. Alternative Concept & Complexity Analysis

### Comparison: Dynamic Method Dispatch vs. `instanceof` Checks and Type Casting

Suppose dynamic dispatch were eliminated and fee calculation used `instanceof` checks:
```java
// Alternative Anti-pattern
double computeFee(Vehicle v, int minutes) {
    if (v instanceof TwoWheeler tw) {
        return tw.calculateTwoWheelerFee(minutes);
    } else if (v instanceof ElectricCar ec) {
        return ec.calculateElectricFee(minutes);
    } else if (v instanceof Car c) {
        return c.calculateCarFee(minutes);
    }
    // ... continues for every vehicle type
}
```

| Criterion | Dynamic Method Dispatch (vtable lookup) | Explicit `instanceof` Cascades & Casting |
| :--- | :--- | :--- |
| **Time Complexity per Call ($k$ types)** | $\mathcal{O}(1)$ constant time (single pointer dereference into method table). | Worst-Case: $\mathcal{O}(k)$ linear checks down the `if-else` branch chain. |
| **Code Maintenance & Extensibility** | **Zero Modification:** Adding `DeliveryVan` requires zero changes to the calling loop. | **High Maintenance:** Every loop and caller must be manually updated with a new `else if (v instanceof DeliveryVan)` block. |
| **Bytecode Size & Cleanliness** | Single `invokevirtual` bytecode instruction. | Massive bytecode footprint with branch instructions and cast checks. |

**Conclusion:** Dynamic method dispatch yields constant $\mathcal{O}(1)$ execution and complete decoupling between caller loops and concrete subclasses.
