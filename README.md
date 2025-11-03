# Cache Memory Simulation (Java)

This project is a **Java implementation of a cache memory simulator**, developed as part of the **Data Structures** course.  
It was created collaboratively by two students, focusing on the application of core data structure concepts to simulate how cache memory works in computer systems.

The program models how a cache stores and retrieves data blocks from main memory, using specific cache mapping and replacement policies.  
It demonstrates how cache efficiency depends on the organization and management of stored data.

---

## Project Overview

The main purpose of this project was to simulate the basic functionality of a cache memory system.  
The program reads memory access traces, determines whether each access is a **cache hit** or **cache miss**, and manages data replacement according to the selected policy.

The simulation helps visualize how different cache configurations affect performance — including the number of hits, misses, and total access time.

> **Note:** Some comments and outputs in the source code are in **Greek**, since the project was originally developed for a Greek academic environment.

---

## Features

- **Cache organization:** Configurable parameters such as block size, cache size, and number of sets  
- **Cache mapping:** Direct-mapped, fully associative, or set-associative cache types  
- **Replacement policies:** Supports algorithms such as **LRU (Least Recently Used)** and **FIFO**  
- **Hit and miss detection:** Tracks and counts each cache access result  
- **Statistics report:** Displays cache performance metrics after execution  
- **Efficient implementation:** Uses appropriate data structures (e.g., `HashMap`, `LinkedList`) for fast lookups and updates  

---

## Core Logic

1. **Memory Access Simulation:**  
   The program receives a sequence of memory addresses to simulate CPU read/write operations.  

2. **Address Breakdown:**  
   Each address is divided into three parts:  
   - **Tag:** Identifies a unique memory block  
   - **Index:** Specifies which cache set to look in  
   - **Offset:** Indicates the byte within a block  

3. **Cache Lookup:**  
   For each access, the simulator checks if the block is already in cache (hit) or not (miss).  

4. **Replacement Policy:**  
   If the block is missing, it is loaded from main memory.  
   When the cache is full, a replacement algorithm (e.g., LRU or FIFO) determines which block to evict.  

5. **Performance Statistics:**  
   At the end, the program prints:
   - Total memory accesses  
   - Number of hits and misses  
   - Hit ratio and miss ratio  

---

## Technologies Used

- **Programming Language:** Java  
- **Main Data Structures:** HashMap, LinkedList, and custom classes for cache blocks  
- **Development Environment:** IntelliJ IDEA / Eclipse / VS Code  
- **Version Control:** Git  

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/mix05klad/cache-memory.git
   
2. Navigate to the project directory:
    ```bash
    cd cache-memory
    
3. Compile the source code:
    ```bash
    javac CacheSimulator.java
    
4. Run the program:
    ```bash
    java CacheSimulator
    
## What I Learned

This project deepened my understanding of:

- How data structures (linked lists, hash maps) can model real-world systems  
- The mechanics of cache memory and how replacement strategies affect performance  
- Algorithmic thinking for managing limited resources  
- Writing efficient, modular, and readable Java code  
- Collaborating in a team to design, test, and debug a structured simulation program  

It was one of the first projects where I applied theoretical knowledge from computer architecture and data structures to build a realistic simulation.
