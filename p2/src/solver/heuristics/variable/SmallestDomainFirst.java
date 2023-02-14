package solver.heuristics.variable;

import java.util.List;

import models.Variable;

public class SmallestDomainFirst implements VariableHeuristic {

    @Override
    public Variable getNext(List<Variable> varList) {
        int minDomainIdx = varList.get(0).getDomain().size();
        Variable nextVar = varList.get(0);
        for (Variable v : varList) {
            int domSize = v.getDomain().size();
            if ((domSize < minDomainIdx) || (domSize == minDomainIdx && nextVar.getIndex() > v.getIndex())) {
                minDomainIdx = v.getDomain().size();
                nextVar = v;
            }
        }
        return nextVar;
    }

}
