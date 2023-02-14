package solver.heuristics.variable;

import java.util.List;

import models.Variable;

public class SmallestDomainFirst implements VariableHeuristic {

    @Override
    public Variable getNext(List<Variable> varList) {
        int minDomain = varList.get(0).getDomain().size();
        Variable nextVar = varList.get(0);
        for (Variable v : varList) {
            int domSize = v.getDomain().size();
            if ((domSize < minDomain) || (domSize == minDomain && nextVar.getIndex() > v.getIndex())) {
                minDomain = v.getDomain().size();
                nextVar = v;
            }
        }
        return nextVar;
    }

}
