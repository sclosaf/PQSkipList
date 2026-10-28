<h1 align="center">PQSkipList</h1>

<p align="center">
  A <b>Priority Queue</b> implemented with a <b>Skip List</b> in Java, with an experimental study
  of how the coin-toss probability <i>α</i> affects performance.
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/language-Java-orange?logo=openjdk&logoColor=white">
  <img alt="Dependencies" src="https://img.shields.io/badge/dependencies-none-brightgreen">
  <img alt="Data structure" src="https://img.shields.io/badge/data%20structure-skip%20list-blue">
</p>

Programming project for the course **Data Structures and Algorithms** (*Dati e Algoritmi*,
University of Padua, 2024–2025).

---

## Overview

A **skip list** (Pugh, 1989) is a probabilistic data structure made of several sorted linked
lists stacked on top of each other. The bottom list contains every entry; each list above
contains a random subset of the one below, where every entry is promoted with probability
**α**. Searching starts from the top-left corner and "skips" large parts of the list, giving
**expected O(log n)** time for search, insertion and deletion, with **O(n)** expected space.

This project uses a skip list to implement a **Priority Queue** of `(Integer, String)` entries.
Unlike a map, a priority queue must accept **duplicate keys**, so every insertion adds a new
entry even when the key already exists.

```
S3   -∞ ──────────────────────────────────────── +∞
S2   -∞ ─────────── 17 ─────────────────── 55 ── +∞
S1   -∞ ─────────── 17 ──── 25 ────────── 55 ── +∞
S0   -∞ ── 12 ───── 17 ──── 25 ── 31 ──── 55 ── +∞
```

## Features

- **Priority Queue operations**: `size`, `min`, `insert`, `removeMin`, `print`.
- **Duplicate keys** supported, as required by a priority queue.
- **Automatic height management**: after `removeMin`, empty upper levels are removed so the
  list keeps exactly one empty level on top.
- **Instrumentation**: `insert` returns the number of nodes traversed from the start node to
  the bottom list, used to measure the real cost of each operation.
- **No external libraries**: only `java.lang`, `java.io` and `java.util`.

## Operations and complexity

| Operation | Description | Expected time |
|---|---|---|
| `size()` | Number of entries | O(1) |
| `min()` | Entry with the smallest key (first entry of S₀) | O(1) |
| `insert(key, value)` | Search the position, then build a tower of random height ℓ | O(log n) |
| `removeMin()` | Remove the first tower, then shrink the empty levels | O(log n) |
| `print()` | Entries in key order, each with the height of its tower | O(n) |

---

## Effect of α: experimental results

The probability α controls a **space–time trade-off**: a higher α means taller towers
(more memory) but shorter searches. With **α = 0** no entry is ever promoted, so the
skip list degenerates into a plain sorted linked list.

Average number of nodes traversed per insert, measured on the provided test files
(each with 98 % inserts):

| Inserts | α = 0 | α = 0.25 | α = 0.5 |
|---:|---:|---:|---:|
| 10,000 | 2,394.40 | 23.62 | 28.05 |
| 100,000 | 24,076.95 | 28.50 | 30.63 |

- With **α = 0** the cost grows **linearly**: 10× more inserts means about 10× more work per
  insert, and the 100K run takes over a minute and a half.
- With **α > 0** the cost grows **logarithmically**: 10× more inserts adds only a few nodes,
  and the 100K runs finish in under half a second.

---

## Getting started

### Requirements

- A JDK (Java 8 or newer)

### Build and run

All classes are in a single file:

```bash
git clone https://github.com/sclosaf/PQSkipList.git
cd PQSkipList/App

javac TestProgram.java
java TestProgram Resources/IO_FILES/input_example_1.txt
```

### Input format

The first line contains the number of operations **N** and the probability **α**.
Each of the next N lines is one operation:

| Code | Operation | Example |
|---|---|---|
| `0` | `min()` | `0` |
| `1` | `removeMin()` | `1` |
| `2` | `insert(key, value)` | `2 16 car` |
| `3` | `print()` | `3` |

With **α = −1** the tower heights are computed deterministically from the key, which makes
the output reproducible for correctness checks.

### Example

Input:

```text
13 -1
2 16 car
2 4 bike
2 48 dog
2 9 woman
2 124 car
3
...
```

Output (`print` shows each entry with the height of its tower):

```text
13 -1.0
4 bike 3, 9 woman 1, 16 car 5, 48 dog 5, 124 car 3
...
```

At the end, the program prints α, the final number of entries, the number of inserts
executed and the average number of nodes traversed per insert.

---

## Repository structure

```
PQSkipList/
├── App/
│   ├── TestProgram.java                 # MyEntry, Node, SkipListPQ and the test program
│   └── Resources/
│       ├── IO_FILES/                    # example inputs with their expected outputs
│       └── alphaEfficiencyTest/         # 10K and 100K inserts with α = 0, 0.25, 0.5
└── Project2425.pdf                      # assignment specification
```

## References

- W. Pugh, *Skip Lists: A Probabilistic Alternative to Balanced Trees*, Communications of the ACM, 1990.
- M. T. Goodrich, R. Tamassia, M. H. Goldwasser, *Data Structures and Algorithms in Java*, 6th ed., Wiley, 2014, Section 10.4.
