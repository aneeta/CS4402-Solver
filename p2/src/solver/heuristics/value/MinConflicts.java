package solver.heuristics.value;

import java.util.List;

import csp.binary.BinaryCSP;
import models.Variable;

public class MinConflicts implements ValueHeuristic {

    private BinaryCSP csp;

    public MinConflicts(BinaryCSP csp) {
        this.csp = csp;
    }

    @Override
    public int getNext(Variable var) {
        List<Integer> nextVars = var.getIndicesNeighbours();

        int minCount = 10 * 8;
        int retVal = -1;
        // TODO
        return retVal;
    }

}
