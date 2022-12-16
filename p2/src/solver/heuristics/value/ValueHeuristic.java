package solver.heuristics.value;

import java.util.List;

import models.Variable;

public interface ValueHeuristic {
    public int getNext(Variable var);
}
