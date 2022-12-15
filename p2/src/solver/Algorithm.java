package solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import csp.binary.BinaryCSP;
import models.Variable;

public abstract class Algorithm {

    private List<Variable> unassignedVarList;
    private Heuristic varOrdering, valOrdering;
    private Map<Integer, Integer> assignments;
    private int searchNodeCount;
    private BinaryCSP csp;

    public List<Variable> toVariableList(BinaryCSP csp) {
        List<Variable> varList = new ArrayList<>();
        for (int i = 0; i < csp.getNumberVariables(); i++) {
            List<Integer> domain = new ArrayList<>();
            for (int j = csp.getLB(i); j <= csp.getUB(i); j++) {
                domain.add(j);
            }
            varList.add(new Variable(i, domain, csp));
        }
        return varList;
    }

    public abstract boolean solve();

    public abstract boolean assignmentComplete();
}
