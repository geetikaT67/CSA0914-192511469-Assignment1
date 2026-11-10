# Task 02: Choose the Data Representation

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Field Data Types and Justifications

| Field Name | Selected Data Type | Technical Justification |
| :--- | :--- | :--- |
| **`slotNumber`** | `int` | Slots are non-negative sequential numbers. Standard 32-bit signed integer (`int`) provides more than enough range with minimal memory footprint. |
| **`vehicleNumber`** | `String` | Vehicle license plates contain alphanumeric characters, spaces, and formatting hyphens (e.g., `"TN-09-AB-1234"`). `String` provides built-in immutability, uppercase conversion, and equality matching. |
| **`entryTime` / `exitTime`**| `int` | Stored as integer elapsed minutes from midnight (0 to 1439 for a 24-hour cycle) or epoch minutes. Simplifies arithmetic without floating-point rounding errors. |
| **`duration`** | `int` | Computed as `exitTime - entryTime` in whole minutes. |
| **`fee`** | `double` | Currency values require decimal fractional precision (e.g., Rs 30.00, Rs 210.50). |

---

## 2. Floor and Slot Representation: 1D vs. 2D vs. Jagged Array

- **1D Array (`String[]`):** Flattens all slots into a single line. Requires fixed floor sizes using the formula $\text{index} = \text{floor} \times \text{slotsPerFloor} + \text{slot}$. **Limitation:** Fails when floors have variable capacities (ragged geometry).
- **2D Rectangular Array (`String[][]`):** Requires every floor to have the exact same number of slots (`new String[F][S]`). **Limitation:** Wastes memory with `null` padding if floor sizes differ.
- **Jagged Array (`String[][] slots = new String[floors][];`):** **Selected Choice.** Each floor is allocated as an independent 1D array `slots[f] = new String[slotsPerFloor[f]];`.
  - **Advantages:**
    1. Accurately reflects physical buildings where lower floors may accommodate 50 cars, while upper terrace floors accommodate only 20 cars.
    2. Zero memory waste.
    3. Natural syntax: `slots[floor][slot]` maps directly to spatial coordinates.

---

## 3. Type Conversion, Casting Risks, and Precision Loss

### The Integer Truncation Bug
A common mistake in Java fee computation is using naive integer division:
```java
// BUGGY:
int hours = durationMinutes / 60; // For 75 minutes: 75 / 60 = 1 hour (Lost 15 minutes!)
double fee = hours * ratePerHour; // Underbills customer by a full hour!
```

### The Incorrect Double Cast Bug
```java
// WRONG:
int hours = (int) Math.round(durationMinutes / 60.0); // 75 / 60.0 = 1.25 -> rounds down to 1 hour!
```

### The Correct Integer Arithmetic Resolution
According to the business rule, *every started hour is charged in full* (ceiling rule). Using pure integer arithmetic without floating-point imprecision:
```java
int hours = (durationMinutes + 59) / 60;
// Example:
// 1 minute  -> (1 + 59) / 60 = 60 / 60 = 1 hour
// 60 minutes -> (60 + 59) / 60 = 119 / 60 = 1 hour
// 61 minutes -> (61 + 59) / 60 = 120 / 60 = 2 hours
```

---

## 4. Input Mechanism: Scanner vs. Command-Line Arguments

- **`Scanner` (Interactive Console):** Chosen for flexible runtime interaction, testing multiple simulation steps, and receiving prompt-based inputs from users.
- **Command-line Arguments (`String[] args`):** Useful for batch configuration (e.g., passing initial floor capacities `java Main 2 4 3`), but clumsy for continuous vehicle entry/exit operations.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Jagged Array vs. Flat 1D Array vs. `ArrayList<String[]>`

| Representation | Locate Slot by `[f, s]` Time | Space Complexity | Flexibility for Variable Floor Capacities |
| :--- | :--- | :--- | :--- |
| **Jagged Array (`String[][]`)** | $\mathcal{O}(1)$ direct reference access | Minimal: $F$ floor array pointers + $\sum S_f$ slot references | Native support: each sub-array sized dynamically |
| **1D Array (`String[]`)** | $\mathcal{O}(1)$ if fixed; $\mathcal{O}(F)$ if variable | Contiguous array; requires padding if fixed, or prefix-sum offset array | Poor: requires maintaining floor offset array $\text{offsets}[f]$ |
| **`ArrayList<String[]>`** | $\mathcal{O}(1)$ via `.get(f)[s]` | Overhead of `ArrayList` object wrapper and dynamic resizing structures | High: dynamically add/remove floors at runtime |

**Summary:** The jagged array offers the optimal balance of raw $\mathcal{O}(1)$ access speed, zero pointer boxing overhead, and exact memory allocation for non-uniform floor layouts.
