# Campus Smart-Parking System

## Student & Repository Details
- **Project Title:** Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System
- **Student Register Number:** 192511469
- **Repository Name:** CSA0914-192511469-Assignment1
- **GitHub Repository Link:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment1](https://github.com/geetikaT67/CSA0914-192511469-Assignment1)
- **Programming Language:** Java
- **Java Version:** Java SE 25 LTS (build 25.0.4.1+1-LTS-5)
- **Course & Module:** Programming in Java — CO1 (Fundamentals & OOP)

---

## Assignment Overview
The **Campus Smart-Parking System** is an object-oriented Java console system designed to model, manage, and automate multi-level vehicle parking on a university campus. The system incorporates vehicle classification (`TwoWheeler`, `Car`, `ElectricCar`, `EBike`, `DeliveryVan`), user roles (`Student Pass`, `Staff Pass`, `Visitor Hourly`, `Visitor Day Pass`), EV charging capability (`Chargeable`), slot allocation using a space-efficient multi-level jagged array, integer ceiling duration billing, duplicate entry defense, and daily revenue accounting.

The project strictly follows the assignment briefing across 12 modular tasks, includes all "Complete the Code" exercises, provides complete Big-O complexity analyses and SIMATS Algorithmic Pseudocode, and incorporates the Day-4 Design Challenge adaptations.

---

## Repository Directory Structure

```text
CSA0914-192511469-Assignment1/
├── CompleteTheCode/
│   ├── ParkingFacility.java        # Completed Task 3 slot allocation & release
│   └── FeeDemo.java                # Completed Task 8 vehicle hierarchy & fee calculation
├── Screenshots/
│   ├── README.md                   # Execution output index
│   ├── Task02_DataRepresentation_Output.txt
│   ├── Task03_CompleteTheCode_ParkingFacility_Output.txt
│   ├── Task04_Constructors_Output.txt
│   ├── Task05_StaticLifecycle_Output.txt
│   ├── Task06_InheritanceHierarchy_Output.txt
│   ├── Task07_OverrideEquality_Output.txt
│   ├── Task08_CompleteTheCode_FeeDemo_Output.txt
│   ├── Task09_Abstraction_Output.txt
│   ├── Task10_PackagesEncapsulation_Output.txt
│   ├── Task11_CampusParkingSystem_FullTestSuite_Output.txt
│   └── Task12_Day4Challenge_Output.txt
├── Report/
│   ├── Assignment_Instructions.pdf # Original student briefing PDF
│   └── Assignment_Report.md        # 50-mark formal assignment report
├── Task01/
│   └── README.md                   # Problem analysis, candidate classes, parallel arrays vs OOP
├── Task02/
│   ├── DataRepresentationDemo.java # Primitive types, jagged array, casting/precision demo
│   └── README.md                   # Data types, array design, casting risks
├── Task03/
│   ├── ParkingFacility.java        # Slot allocation and release code
│   └── README.md                   # Search/release logic, SIMATS pseudocode, stack alternative
├── Task04/
│   ├── ConstructorDemo.java        # Parameterized, chained, and copy constructors
│   └── README.md                   # Constructor analysis, stack/heap layout, factory comparison
├── Task05/
│   ├── StaticLifecycleDemo.java    # Static members, object lifecycle, pass-by-value demo
│   └── README.md                   # Static counter, GC eligibility, multi-facility gate analysis
├── Task06/
│   ├── InheritanceHierarchyDemo.java # Hierarchy and constructor trace demo
│   └── README.md                   # Superclass/subclasses, super keyword, constructor trace, UML
├── Task07/
│   ├── OverrideEqualityDemo.java   # @Override, final, equals/hashCode demo
│   └── README.md                   # Method overriding rules, object equality, switch comparison
├── Task08/
│   ├── FeeDemo.java                # Polymorphism and dynamic method dispatch
│   └── README.md                   # Overloading vs dispatch, call trace, instanceof comparison
├── Task09/
│   ├── AbstractionDemo.java        # Abstract class, Chargeable/Payable interfaces, default methods
│   └── README.md                   # Abstract class vs interface, multiple inheritance, inversion
├── Task10/
│   ├── src/
│   │   └── parking/
│   │       ├── model/              # Domain entities: Vehicle, TwoWheeler, Car, ParkingSlot
│   │       ├── service/            # ParkingService
│   │       └── app/                # Main driver and autoboxing pitfall demonstration
│   └── README.md                   # Package structure, access modifiers, autoboxing risks
├── Task11/
│   ├── CampusParkingSystem.java    # Complete production-ready smart parking system
│   ├── ParkingSystemTestSuite.java # 11 comprehensive automated tests
│   └── README.md                   # Test matrix, hand calculation proof, design defense & limits
├── Task12/
│   ├── AdaptedParkingSystem.java   # Adapted system with EBike, DeliveryVan, DayPass, 5 floors
│   ├── Task12TestSuite.java        # 6 automated tests for Day-4 Challenge requirements
│   └── README.md                   # Impact analysis, evaluation, adaptation, defense
└── README.md                       # Master project README
```

---

## Compilation and Execution Commands

Run these commands from the root repository folder:

### 1. Task 02: Data Representation
```powershell
javac Task02\DataRepresentationDemo.java
java -cp Task02 DataRepresentationDemo
```

### 2. Task 03: Complete the Code (ParkingFacility)
```powershell
javac CompleteTheCode\ParkingFacility.java
java -cp CompleteTheCode ParkingFacility
```

### 3. Task 04: Classes, Objects and Constructors
```powershell
javac Task04\ConstructorDemo.java
java -cp Task04 ConstructorDemo
```

### 4. Task 05: Static Members and Object Lifecycle
```powershell
javac Task05\StaticLifecycleDemo.java
java -cp Task05 StaticLifecycleDemo
```

### 5. Task 06: Inheritance Hierarchy
```powershell
javac Task06\InheritanceHierarchyDemo.java
java -cp Task06 InheritanceHierarchyDemo
```

### 6. Task 07: Override and Specialise Behaviour
```powershell
javac Task07\OverrideEqualityDemo.java
java -cp Task07 OverrideEqualityDemo
```

### 7. Task 08: Complete the Code (FeeDemo)
```powershell
javac CompleteTheCode\FeeDemo.java
java -cp CompleteTheCode FeeDemo
```

### 8. Task 09: Abstraction and Interfaces
```powershell
javac Task09\AbstractionDemo.java
java -cp Task09 AbstractionDemo
```

### 9. Task 10: Packages and Encapsulation
```powershell
javac -d Task10\bin (Get-ChildItem -Path Task10\src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
java -cp Task10\bin parking.app.Main
```

### 10. Task 11: Complete Campus Parking System & Full Test Suite
```powershell
javac -d Task11 Task11\CampusParkingSystem.java Task11\ParkingSystemTestSuite.java
java -cp Task11 ParkingSystemTestSuite
```

### 11. Task 12: Day-4 Design Challenge (Adapted Parking System)
```powershell
javac -d Task12 Task12\AdaptedParkingSystem.java Task12\Task12TestSuite.java
java -cp Task12 Task12TestSuite
```

---

## Task Summaries and Execution Results

| Task | Title | Key Concepts & Deliverables | Execution Result |
| :---: | :--- | :--- | :---: |
| **Task 01** | Understand the Problem & Identify Objects | Candidate classes, responsibilities, business rules, parallel arrays vs OOP complexity analysis | Verified (Written Analysis) |
| **Task 02** | Choose the Data Representation | Primitive data types, jagged array sizing, integer division bug vs integer ceiling arithmetic | **PASS** (Console Output) |
| **Task 03** | Design Control Logic & Complete the Code | Slot allocation & release loops, SIMATS pseudocode, completed `ParkingFacility.java`, stack alternative analysis | **PASS** (Exact output matched) |
| **Task 04** | Classes, Objects and Constructors | Parameterized constructors, chaining with `this()`, copy constructor, stack vs heap memory layout | **PASS** (Console Output) |
| **Task 05** | Static Members and Object Life Cycle | Global static ticket counter, static block, pass-by-value of reference, GC eligibility, multi-facility gate analysis | **PASS** (Console Output) |
| **Task 06** | Design the Inheritance Hierarchy | Hierarchical & multilevel inheritance, `super` keyword, constructor trace for `ElectricCar`, UML diagram | **PASS** (Trace verified) |
| **Task 07** | Override and Specialise Behaviour | `@Override` rules, `final` keyword, overriding `equals()` and `hashCode()` by plate, `HashSet` deduplication | **PASS** (Console Output) |
| **Task 08** | Apply Polymorphism & Complete the Code | Compile-time overloading vs runtime dynamic dispatch, completed `FeeDemo.java`, call trace, `equals` explanation | **PASS** (Exact output matched) |
| **Task 09** | Introduce Abstraction | Abstract class `Vehicle` vs capability interfaces `Chargeable`/`Payable`, default/static methods, inversion analysis | **PASS** (Console Output) |
| **Task 10** | Packages and Access Modifiers | Modular packages (`model`, `service`, `app`), access modifiers, validating accessors, autoboxing/wrapper risks | **PASS** (Console Output) |
| **Task 11** | Implement, Test and Defend the Design | Production-ready multi-level facility, 11 automated test cases (normal, boundary, invalid), hand revenue proof | **11/11 PASS** |
| **Task 12** | Day-4 Design Challenge | Adaptation: `EBike` (flat fee Rs 15), `DeliveryVan` (peak rate Rs 60/hr), `DayPass` (Rs 80), 5 non-uniform floors | **6/6 PASS** |
