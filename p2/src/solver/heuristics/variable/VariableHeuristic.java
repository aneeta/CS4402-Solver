package solver.heuristics.variable;

import java.util.List;

import models.Variable;

public interface VariableHeuristic {

    public Variable getNext(List<Variable> varList);

}
