# CS4402-Solver
Practical 2: Solver for Constraint Satisfaction Problems 

## How to Run

### Running the Solver

```bash
java -jar CS4402-Solver.jar <path to .csp file> <algorithm> <variable heuristic> <value heuristic>
```

The valid options are:
```
algorithm:          fc,mac
variable heuristic: asc,sdf
value heuristic:    asc
```

### Running the tests

```bash
javac -cp "$(printf %s: ../../lib/*.jar)" test/*.java
```

```bash
java -cp "$(printf %s: ../../lib/*.jar)" test.TestRunner
```