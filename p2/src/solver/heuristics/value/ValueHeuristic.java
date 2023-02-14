package solver.heuristics.value;

import models.Variable;

public interface ValueHeuristic {
    public int getNext(Variable var);
}
