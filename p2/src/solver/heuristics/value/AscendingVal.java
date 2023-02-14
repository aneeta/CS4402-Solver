package solver.heuristics.value;

import models.Variable;

public class AscendingVal implements ValueHeuristic {

    @Override
    public int getNext(Variable var) {
        return Collections.min(var.getDomain());
    }
}
