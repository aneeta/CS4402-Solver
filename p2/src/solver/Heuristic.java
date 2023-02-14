package solver;

import java.util.List;

import models.Variable;

public interface Heuristic {

    public Variable getNext(List<Variable> varList);

}
