# Task 07: Override and Specialise Behaviour

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Overriding Behaviour in Subclasses

- **Methods Overridden:**
  - `ratePerHour()`: Abstract in `Vehicle`, overridden in `TwoWheeler` (Rs 10.0) and `Car` (Rs 30.0) to provide category tariffs.
  - `calculateFee()`: Overridden in `EBike` (Task 12) to return a flat fee (Rs 15.0), and `DeliveryVan` to inject peak-time multipliers.
  - `equals(Object o)`, `hashCode()`, and `toString()`: Overridden from `java.lang.Object`.

---

## 2. The `@Override` Annotation and Overriding Rules

### Compiler Benefits of `@Override`
1. **Safety:** Directs the Java compiler (`javac`) to verify that a superclass method with the exact signature exists. Catches subtle typos (e.g., misspelling `calculateFee(int mins)` as `calculateFee(long mins)`) at compile-time instead of silently defining an overloaded method.
2. **Readability:** Signals intent clearly to viva examiners and code reviewers.

### Mandatory Java Overriding Rules
1. **Method Signature:** Method name and parameter list must be identical.
2. **Return Type:** Must be identical or a covariant subtype (e.g., returning a subclass).
3. **Access Privilege:** Subclass cannot assign weaker access privilege (e.g., cannot reduce `public` to `protected` or `private`).
4. **Exceptions:** Subclass cannot declare broader checked exceptions than the superclass method.

---

## 3. Justified Use of the `final` Keyword

- **`final` Field (`private final String regNo`):** A physical car's license plate number cannot change after manufacture/registration. Making it `final` enforces thread safety and prevents accidental mutation.
- **`final` Method (`public final String getRegNo()`):** Prevents malicious or erroneous subclasses from returning spoofed registration strings.
- **`final` Class (`final class HeavyTransport`):** Closes the class against further extension, preventing unauthorized subclassing.

---

## 4. Object Contract: `equals()`, `hashCode()`, and `toString()`

### Why Registration Number Defines Identity
In the real world, a vehicle is uniquely identified by its license plate (`regNo`), regardless of whether two Java reference variables point to separate heap memory locations.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof Vehicle other)) return false;
    return this.regNo.equalsIgnoreCase(other.regNo);
}

@Override
public int hashCode() {
    return this.regNo.toUpperCase().hashCode();
}
```
### Crucial Java Contract Rule
If `v1.equals(v2)` is `true`, then `v1.hashCode()` **must** be equal to `v2.hashCode()`. Violating this causes hashing collections (`HashSet`, `HashMap`) to store duplicates or fail lookups.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Subclass Overriding vs. Monolithic Switch Statement on Type Enum

Suppose instead of overriding `ratePerHour()`, a single `calculateFee()` method used a `switch` statement on an integer/enum type:

```java
// Alternative Anti-Pattern
double calculateFee(int type, int minutes) {
    int hours = (minutes + 59) / 60;
    switch (type) {
        case TWO_WHEELER: return hours * 10;
        case CAR:         return hours * 30;
        case EV:          return hours * 30 + 120;
        default: throw new IllegalArgumentException();
    }
}
```

| Criterion | Method Overriding (OOP Polymorphism) | Monolithic `switch` on Type Field |
| :--- | :--- | :--- |
| **Adding New Category (e.g., `EBike`)** | **Zero Modification to existing classes:** Simply author `class EBike extends Vehicle`. Satisfies Open/Closed Principle (OCP). | **Modifies existing code:** Must locate and edit every single `switch` statement across the entire codebase (`calculateFee`, `displayInfo`, etc.). High regression risk. |
| **Execution Time for $k$ Types** | $\mathcal{O}(1)$ dynamic method dispatch via virtual method table (vtable). Constant time regardless of how many types exist. | $\mathcal{O}(k)$ sequential branch comparisons if compiled as `lookupswitch`, or $\mathcal{O}(1)$ if dense `tableswitch`. |
| **Testability & Cohesion** | High: unit tests for `TwoWheeler` run isolated from `Car`. | Poor: changes to one type risk breaking all types in the shared switch block. |

**Conclusion:** Method overriding adheres to the SOLID Open/Closed Principle and yields constant-time execution unaffected by the number of vehicle categories.
