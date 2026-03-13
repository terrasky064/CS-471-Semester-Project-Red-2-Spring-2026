# CS 471/571: Operating Systems Concepts - Course Project

**Spring 2026 | Due: May 3, 2026**

CPU Scheduling and Process Synchronization Simulations in Java

**Team Members:** Spencer Peloquin and Darnell Barnes

---

## Project Overview

This project implements two fundamental operating system concepts:

1. **CPU Scheduling Simulator** - Simulates CPU scheduling using FIFO and Shortest Job First (SJF) algorithms
2. **Producer-Consumer Problem** - Demonstrates thread synchronization using a bounded buffer

Each problem is worth 50 points and includes sample input/output files, executable code, and comprehensive documentation.

---

## Project Structure

```
CS471PROJECT/
├── README.md                           # This file (main documentation)
├── LICENSE.md                          # MIT License
├── [Recorded Video]                    # Video demonstration (submitted separately)
└── Two Problem Directories:

    CPUSCHED/
    ├── README                          # Detailed build and execution instructions
    ├── src/
    │   ├── CPUScheduler.java          # Main scheduler implementation
    │   ├── Process.java               # Process data structure
    │   ├── GenerateData.java          # Test data generator
    │   └── Datafile1-txt.txt          # Sample input file (500 processes)
    ├── bin/
    │   └── [Compiled .class files]    # Executable bytecode
    ├── output/
    │   ├── sample_output_FIFO.txt     # Sample FIFO scheduling output
    │   └── sample_output_SJF.txt      # Sample SJF scheduling output
    
    PRODUCER_CONSUMER/
    ├── README                          # Detailed build and execution instructions
    ├── src/
    │   ├── ProducerConsumer.java      # Main program with test harness
    │   ├── BoundedBuffer.java         # Thread-safe bounded buffer (semaphore-based)
    │   ├── SalesRecord.java           # Sales record data structure
    │   └── SalesData.txt              # Sample sales records (optional)
    ├── bin/
    │   └── [Compiled .class files]    # Executable bytecode
    └── output/
        └── sample_output.txt           # Sample output from 9 test scenarios
```

---

## Quick Start

### CPU Scheduling

For detailed instructions, see **CPUSCHED/README**

```bash
cd CPUSCHED
javac -d bin Process.java CPUScheduler.java
java -cp bin CPUSCHED.CPUScheduler src/Datafile1-txt.txt FIFO
java -cp bin CPUSCHED.CPUScheduler src/Datafile1-txt.txt SJF
```

### Producer-Consumer

For detailed instructions, see **PRODUCER_CONSUMER/README**

```bash
cd PRODUCER_CONSUMER
javac -d bin SalesRecord.java BoundedBuffer.java ProducerConsumer.java
java -cp bin PRODUCER_CONSUMER.ProducerConsumer
```

---

## Problem Descriptions

### Problem 1: CPU Scheduling (50 points)

**Simulates a CPU scheduler handling 500 processes with two algorithms:**

- **FIFO (First-In-First-Out)**: Processes execute in arrival order
- **SJF (Shortest Job First)**: Non-preemptive; shortest burst time executes first

**Input:** 500 process records with `<arrival_time, burst_time>` pairs  
**Output:** Scheduling statistics and performance metrics

**Process Record Format:**
```
arrival_time  burst_time
```

**Required Statistics:**

```
Number of processes: 500
Total elapsed time (for the scheduler): X time units
Throughput (Number of processes executed in one unit of CPU burst time): X processes
CPU utilization: X%
Average waiting time (in CPU burst times): X
Average turnaround time (in CPU burst times): X
Average response time (in CPU burst times): X
```

**Formulas:**
- **Total elapsed time**: Completion time of last process minus arrival time of first process
- **Throughput**: Total burst time / Total number of processes (500)
- **CPU Utilization**: (Total Burst Time / Total elapsed time) × 100%
- **Average Waiting Time**: Total waiting time / 500
- **Average Turnaround Time**: Total turnaround time / 500
  - Turnaround time = Burst time + Waiting time, OR Completion time - Arrival time
- **Average Response Time**: Total response time / 500
  - Response time = Time first scheduled - Arrival time

---

### Problem 2: Producer-Consumer Problem (50 points)

**Demonstrates thread synchronization with p producers, c consumers, and bounded buffer of size b.**

**Sales Record Structure:**
```
Sales Date (DD/MM/YY) | Store ID | Register# | Sale Amount
```

- **Date Format**: DD/MM/YY where YY = 16 (always)
- **DD**: Day (1-30)
- **MM**: Month (01-12)
- **Store ID**: 1 to p (where p = number of producers)
- **Register**: 1-6 (any store has 6 registers)
- **Sale Amount**: $0.50 to $999.99

**Simulation Parameters:**
- **Total Items**: 1,000 records produced across all producers
- **Producer Sleep**: Each producer sleeps 5-40 milliseconds between generating records
- **Buffer Size**: b = 10
- **Test Scenarios**: Run for all combinations of p ∈ {2, 5, 10} and c ∈ {2, 5, 10} = **9 runs**

**Required Statistics per Run:**
```
Store-wide total sales (by store ID)
Month-wise total sales (by month across all stores)
Aggregate sales (total sales all items)
Total time for simulation (in seconds)
```

**Synchronization Mechanism:**
- Thread-safe bounded buffer using semaphores:
  - `empty`: Controls producer blocking (initial value = buffer capacity)
  - `full`: Controls consumer blocking (initial value = 0)
  - `mutex`: Ensures exclusive buffer access (initial value = 1)

---

## Deliverables Checklist

- [x] **Recorded video** (≤10 minutes, demonstrates code execution)
- [x] **CPUSCHED subdirectory** with:
  - [x] README with copyable commands
  - [x] Well-documented source code
  - [x] Compiled executable code
  - [x] Sample input data file
  - [x] Sample output file(s)
- [x] **PRODUCER_CONSUMER subdirectory** with:
  - [x] README with copyable commands
  - [x] Well-documented source code
  - [x] Compiled executable code
  - [x] Sample output file(s) (9 test scenarios)
- [x] **LICENSE.md** (MIT License)
- [x] **This README.md** (main project documentation)

---

## Technical Notes

### Java Version
- JDK 8 or higher required
- Both programs compile and run on Windows, macOS, and Linux

### Compilation
- Each program must be compiled in its respective subdirectory
- Class files are stored in `bin/` subdirectory for each program
- Classpath must be specified for execution: `-cp bin`

### Synchronization Details (Producer-Consumer)
- Uses **Semaphore** class from `java.util.concurrent`
- Three semaphores manage bounded buffer access:
  1. `empty` semaphore: Counts available buffer slots
  2. `full` semaphore: Counts items in buffer
  3. `mutex` semaphore: Binary lock for critical section

### Test Scenarios (Producer-Consumer)
All 9 combinations generate 1,000 items total:

| Run | Producers | Consumers | Buffer Size | Items |
|-----|-----------|-----------|-------------|-------|
| 1   | 2         | 2         | 10         | 1000  |
| 2   | 2         | 5         | 10         | 1000  |
| 3   | 2         | 10        | 10         | 1000  |
| 4   | 5         | 2         | 10         | 1000  |
| 5   | 5         | 5         | 10         | 1000  |
| 6   | 5         | 10        | 10         | 1000  |
| 7   | 10        | 2         | 10         | 1000  |
| 8   | 10        | 5         | 10         | 1000  |
| 9   | 10        | 10        | 10         | 1000  |

---

## Grading Criteria

- **Code Quality**: Well-documented, proper naming, clean structure
- **Correctness**: Algorithms implemented per specification
- **Statistics**: All required metrics calculated correctly
- **Synchronization**: Proper use of semaphores for thread safety
- **Output Format**: Matches specification exactly
- **Video Demonstration**: Shows successful compilation and execution (−20% if omitted)

---

## References

- Course Material: Module 2 & 3 (Operating System Concepts)
- CPU Scheduling Algorithms: FIFO and Shortest Job First
- Process Synchronization: Semaphore-based bounded buffer
- Threading: Java `Thread`, `Runnable`, `java.util.concurrent`

---

## License

This project is licensed under the MIT License. See [LICENSE.md](LICENSE.md) for details.

**Copyright © 2026 Spencer Peloquin and Darnell Barnes**


