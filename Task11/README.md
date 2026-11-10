# Task 11: Implement, Test and Defend the Design

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K5 – Evaluate

---

## 1. System Implementation Summary

The complete **Campus Smart-Parking System** is implemented in `CampusParkingSystem.java` and verified with a 100% passing test suite in `ParkingSystemTestSuite.java`.

### Key System Highlights:
1. **Multi-level Slot Allocation:** Floor-major search allocating lowest available `[floor, slot]`.
2. **Vehicle Specialization:** `TwoWheeler` (Rs 10/hr), `Car` (Rs 30/hr), and `ElectricCar` (Rs 30/hr + Rs 12/kWh).
3. **Pass Exemption:** Students and staff holding monthly passes park free of charge.
4. **Integer Ceiling Billing:** Uses `(minutes + 59) / 60` for accurate full-hour billing.
5. **Defensive Validation:** Duplicate registration detection, negative time protection, and invalid ticket handling.

---

## 2. Test Execution Results Table

| Test ID | Test Category | Scenario / Input | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Normal Operation | Park TwoWheeler visitor `TN09AA1001` at min 600 | Allocated to Floor 0, Slot 0 | Floor 0, Slot 0 | **PASS** |
| **TC-02** | Normal Operation | Park Car visitor `TN09BB2002` at min 615 | Allocated to Floor 0, Slot 1 | Floor 0, Slot 1 | **PASS** |
| **TC-03** | Normal Operation | Park ElectricCar visitor `TN09CC3003` at min 630 | Allocated to Floor 1, Slot 0 | Floor 1, Slot 0 | **PASS** |
| **TC-04** | Normal Operation | Park Car Student Pass `TN09DD4004` at min 540 | Allocated to Floor 1, Slot 1 | Floor 1, Slot 1 | **PASS** |
| **TC-05** | Boundary Case | Facility full (all 4 slots taken); attempt 5th vehicle | Return `null` (`[FULL]`) | Returned `null` | **PASS** |
| **TC-06** | Boundary Case | Stay exactly at 120-min slab boundary (Car @ Rs 30/hr) | Charged exactly Rs 60.00 | Paid Rs 60.00 | **PASS** |
| **TC-07** | Boundary Case | Zero-minute parking stay (0 min duration) | Charged Rs 0.00 | Paid Rs 0.00 | **PASS** |
| **TC-08** | Boundary Case | 105 mins stay for TwoWheeler (2 full hours @ Rs 10/hr) | Charged Rs 20.00 | Paid Rs 20.00 | **PASS** |
| **TC-09** | Pass Exemption | Student Pass exit after 240 mins stay | Charged Rs 0.00 | Paid Rs 0.00 | **PASS** |
| **TC-10** | EV Charging | EV stay 30 mins (Rs 30) + 15 kWh charging @ Rs 12 (Rs 180) | Charged Rs 210.00 | Paid Rs 210.00 | **PASS** |
| **TC-11** | Invalid Case | Attempt duplicate vehicle entry with same plate | Rejected with error | Rejected | **PASS** |
| **TC-12** | Invalid Case | Attempt exit with non-existent ticket ID (`9999`) | Returns `-1.0` | Returned `-1.0` | **PASS** |
| **TC-13** | Invalid Case | Exit time earlier than entry time (`700 < 800`) | Rejected with error | Returned `-1.0` | **PASS** |
| **TC-14** | Reconciliation | Hand-calculated daily revenue comparison | Rs 320.00 | Rs 320.00 | **PASS** |

---

## 3. Hand-Calculated Revenue Verification

### Independent Manual Calculation:
1. **Vehicle 1 (TwoWheeler Visitor):** 105 mins $\rightarrow \lceil 105/60 \rceil = 2\text{ hrs} \times \text{Rs } 10 = \mathbf{\text{Rs } 20.00}$.
2. **Vehicle 2 (Car Visitor):** 120 mins $\rightarrow \lceil 120/60 \rceil = 2\text{ hrs} \times \text{Rs } 30 = \mathbf{\text{Rs } 60.00}$.
3. **Vehicle 3 (ElectricCar Visitor):** 30 mins $\rightarrow 1\text{ hr} \times \text{Rs } 30 = \text{Rs } 30.00$; EV Charging: $15\text{ kWh} \times \text{Rs } 12 = \text{Rs } 180.00 \rightarrow \text{Total } = \mathbf{\text{Rs } 210.00}$.
4. **Vehicle 4 (Student Pass Holder):** 240 mins $\rightarrow$ Free monthly pass $\rightarrow \mathbf{\text{Rs } 0.00}$.
5. **Vehicle 5 (Zero-Minute Stay):** 0 mins $\rightarrow \mathbf{\text{Rs } 0.00}$.
6. **Vehicle 6 (Car Visitor Cleanup):** 60 mins $\rightarrow 1\text{ hr} \times \text{Rs } 30 = \mathbf{\text{Rs } 30.00}$.

$$\text{Total Expected Daily Revenue} = 20.00 + 60.00 + 210.00 + 0.00 + 0.00 + 30.00 = \mathbf{\text{Rs } 320.00}$$

**Program Execution Output:**
```
Expected Hand-Calculated Revenue: Rs 320.0
Actual System Daily Revenue:    Rs 320.0
[PASS] Hand-calculated daily revenue strictly matches system output (Rs 320.00)!
```

---

## 4. Evaluation of Object-Oriented Principles and Design Limitation

### Principles Followed:
1. **Encapsulation:** All internal slot coordinates, timestamps, and fees are `private`. Invariants are guarded by validated methods.
2. **Inheritance:** Clean hierarchical and multilevel tree (`TwoWheeler`, `Car`, `ElectricCar`).
3. **Polymorphism:** Clean dynamic dispatch on `calculateFee()` and interface polymorphism on `Chargeable`.
4. **Abstraction:** High-level caller code deals only with `Vehicle` abstractions and `CampusParkingSystem` APIs.

### One Inherent Limitation of Current Design:
- **Linear Array Search on Exit:** Active tickets are maintained in a 1D array requiring $\mathcal{O}(n)$ linear scanning when locating a ticket by registration number or ticket ID. While completely acceptable for college-scale deployments ($n \le 100$), it becomes a CPU bottleneck under high-volume commercial scaling.

---

## 5. Alternative Concept & Complexity Analysis

### Comparison: Active Tickets in Array vs. `ArrayList<ParkingTicket>` vs. `HashMap<String, ParkingTicket>`

| Performance / Metric | Primitive Array (`ParkingTicket[]`) | `ArrayList<ParkingTicket>` | `HashMap<String, ParkingTicket>` (Keyed by RegNo) |
| :--- | :--- | :--- | :--- |
| **Exit Lookup Time by RegNo** | $\mathcal{O}(n)$ linear scan through array. | $\mathcal{O}(n)$ linear scan via loop or stream. | **$\mathcal{O}(1)$ average constant time** via hash bucket lookup. |
| **Exit Ticket Removal Time** | $\mathcal{O}(n)$ shifting elements to fill the gap. | $\mathcal{O}(n)$ underlying arraycopy element shift. | **$\mathcal{O}(1)$ constant time** bucket node unlinking. |
| **Memory / Space per Parked Vehicle** | 4 or 8 bytes (raw reference pointer on 64-bit JVM with compressed oops). | Reference pointer + `ArrayList` internal capacity buffer growth overhead. | $\sim 32\text{ bytes}$ per `Node<K, V>` entry (hash, key ref, value ref, next pointer). |
| **Fixed vs Dynamic Capacity** | Fixed at instantiation; requires pre-sizing. | Dynamically grows by $1.5\times$ factor automatically. | Dynamically grows with load factor rehash at 0.75 threshold. |

**Conclusion:** For massive commercial parking garages ($> 5,000$ active vehicles), migrating the exit lookup to a `HashMap<String, ParkingTicket>` slashes exit processing time from $\mathcal{O}(n)$ to $\mathcal{O}(1)$ with minimal memory tradeoff.
