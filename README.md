# System Analysis — Algorithms
**Semester 4 | Wroclaw University of Science and Technology (PWR)**

Implementations of combinatorial optimisation algorithms covered in the System Analysis course.

---

## Travelling Salesman Problem — Simulated Annealing
**File:** `SimulatedAnnealingTSP.java`

Solves the NP-hard **Travelling Salesman Problem (TSP)** using the **Simulated Annealing** metaheuristic.

### How it works
1. Generates N random cities on a 2D coordinate plane
2. Starts from a random route (permutation of cities)
3. At each iteration, generates a neighbouring route using one of four operators:
   - **Swap** — swaps two random cities in the route
   - **Inverse** — reverses a random sub-segment of the route
   - **Insert** — moves a single city to a new position
   - **Insert Subroute** — moves a contiguous block of cities to a new position
4. Accepts improvements always; accepts worse solutions with probability `e^(-ΔE/T)` (Boltzmann criterion) to escape local optima
5. Cools temperature each iteration: `T = T * (1 - coolingRate)`
6. Terminates when either the accepted-solutions limit or no-improvement limit is reached

### Parameters
| Parameter | Default | Description |
|-----------|---------|-------------|
| `numberOfCities` | 25 | Number of cities to generate |
| `maxCoordinate` | 100.0 | Bounding box size for city placement |
| `initialTemp` | 5000 | Starting temperature |
| `coolingRate` | 0.003 | Rate at which temperature decreases per iteration |

### Run
```bash
javac SimulatedAnnealingTSP.java
java SimulatedAnnealingTSP
```

### Output
Prints progress every 100 iterations, then a final statistics report:
- Best route distance found
- Total iterations, accepted moves, improved moves
- Per-operator usage breakdown
- Full best route with city coordinates

---

## Partition Problem — Greedy Heuristic
**File:** `partitionproblem.py`

Solves the NP-complete **Partition Problem**: given a set of integers, divide them into two subsets whose sums are as equal as possible.

### How it works
Uses a **greedy** approach:
1. Sort the numbers in descending order
2. Assign each number to whichever subset currently has the smaller sum
3. Return both subsets and the absolute difference between their sums

This is a known approximation — it does not guarantee an optimal split but runs in O(n log n) and performs well in practice.

### Run
```bash
python partitionproblem.py
```

### Example output
```
subset  [20, 2]
subset2 [15, 5, 0]
difference 2
```
