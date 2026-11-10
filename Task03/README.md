# Task 03: Design the Control Logic

- **Course:** Programming in Java (CO1)
- **Student Register Number:** 192511469
- **Repository:** CSA0914-192511469-Assignment1
- **Bloom's Level:** K4 – Analyse

---

## 1. Control Logic Design: Slot Allocation and Fee Calculation

### 1.1 Searching Floors and Slots for Allocation
- **Strategy:** Sequential nested iteration (Floor-Major Order). The outer loop iterates through floors $f = 0, 1, \dots, F-1$, and the inner loop iterates through slots $s = 0, 1, \dots, S_f-1$.
- **Early Termination:** The instant an unallocated slot is discovered (`slots[f][s] == null`), the vehicle registration number is assigned, and the method immediately executes `return new int[]{f, s};`.
- **Facility Full Condition:** If both loops terminate without discovering an empty slot, the method returns `null`.

### 1.2 Fee Slab Calculation Logic
- Fee slabs are processed using structured `if-else` guards:
  1. **Pass Exemption Guard:** If the user holds a valid monthly pass, return `0.0`.
  2. **Zero-Minute Guard:** If duration is `0`, return `0.0`.
  3. **Hourly Calculation:** Duration is converted to integer ceiling hours via `(minutes + 59) / 60`.
  4. **Vehicle Rate Multiplication:** Multiplied by subclass-specific rate (`ratePerHour()`).
  5. **EV Surcharge Guard:** If vehicle implements `Chargeable` and consumed energy > 0, append `chargingFee(kWh)`.

### 1.3 Usage of `break`, `continue`, and `return`
- `return`: Used for immediate exit upon finding a slot or completing fee calculation, bypassing extraneous loop cycles.
- Boundary validation guards use immediate `return false;` in `release()` to reject invalid indices or unallocated slots.

---

## 2. Algorithmic Pseudocode (SIMATS Engineering Standard)

### Algorithm 1: Slot Allocation
```text
BEGIN ALGORITHM AllocateSlot
    INPUT: vehicleNo (STRING), slots (2D ARRAY of STRING)
    OUTPUT: allocatedPosition (ARRAY OF INTEGER [floor, slot]) OR NULL

    FOR f FROM 0 TO LENGTH(slots) - 1 DO
        FOR s FROM 0 TO LENGTH(slots[f]) - 1 DO
            IF slots[f][s] IS NULL THEN
                SET slots[f][s] = vehicleNo
                RETURN ARRAY [f, s]
            END IF
        END FOR
    END FOR

    RETURN NULL  // Facility is completely full
END ALGORITHM
```

### Algorithm 2: Slot Release
```text
BEGIN ALGORITHM ReleaseSlot
    INPUT: floor (INTEGER), slot (INTEGER), slots (2D ARRAY of STRING)
    OUTPUT: success (BOOLEAN)

    IF floor < 0 OR floor >= LENGTH(slots) THEN
        RETURN FALSE
    END IF

    IF slot < 0 OR slot >= LENGTH(slots[floor]) THEN
        RETURN FALSE
    END IF

    IF slots[floor][slot] IS NULL THEN
        RETURN FALSE  // Slot is already vacant
    END IF

    SET slots[floor][slot] = NULL
    RETURN TRUE
END ALGORITHM
```

### Algorithm 3: Parking Fee Computation
```text
BEGIN ALGORITHM ComputeParkingFee
    INPUT: durationMinutes (INTEGER), ratePerHour (REAL), hasPass (BOOLEAN), isEV (BOOLEAN), kWh (INTEGER)
    OUTPUT: totalFee (REAL)

    IF hasPass IS TRUE THEN
        SET parkingFee = 0.0
    ELSE IF durationMinutes <= 0 THEN
        SET parkingFee = 0.0
    ELSE
        SET billableHours = (durationMinutes + 59) DIV 60
        SET parkingFee = billableHours * ratePerHour
    END IF

    SET chargingFee = 0.0
    IF isEV IS TRUE AND kWh > 0 THEN
        SET chargingFee = kWh * 12.0
    END IF

    SET totalFee = parkingFee + chargingFee
    RETURN totalFee
END ALGORITHM
```

---

## 3. Execution Verification of Complete the Code (`ParkingFacility.java`)

```
Output from Execution:
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```
*(Exact match with assignment specification)*

---

## 4. Alternative Concept & Complexity Analysis

### Comparison: Linear Scan vs. Free-Slot Stack per Floor

| Operation / Property | Linear Nested Scan (Current Implementation) | Free-Slot Stack per Floor (`Stack<Integer>[]`) |
| :--- | :--- | :--- |
| **`allocate()` Time Complexity** | Worst-Case: $\mathcal{O}(F \times S)$ when facility is nearly full or full. Best-case: $\mathcal{O}(1)$. | Best & Worst-Case: $\mathcal{O}(F)$ to find non-empty floor stack, then $\mathcal{O}(1)$ `pop()`. If global stack used: $\mathcal{O}(1)$. |
| **`release()` Time Complexity** | $\mathcal{O}(1)$ direct coordinate validation and clearance. | $\mathcal{O}(1)$ slot clearance + `push()` slot index onto stack. |
| **Auxiliary Space Complexity** | $\mathcal{O}(1)$ auxiliary space beyond the 2D slot array. | $\mathcal{O}(F \times S)$ auxiliary pointers to store integer slot indices across stacks. |
| **Synchronization / Consistency Risk**| Zero: the 2D array is the single source of truth. | Medium: stack state and slot state must stay synchronized; duplicate pushes on bug corrupt state. |

**Conclusion:** For typical campus facilities ($F \le 5, S \le 100$), linear scan executes in sub-microsecond time with zero memory overhead. For mega-facilities ($> 10,000$ slots), a free-slot queue/stack becomes advantageous.
