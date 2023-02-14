package solver;

import java.util.List;

import models.Variable;

public class Ascending implements Heuristic {

    public Variable getNext(List<Variable> varList) {
        int minIdx = varList.get(0).getIndex();
        Variable nextVar = varList.get(0);
        for (Variable v : varList) {
            if (v.getIndex() < minIdx) {
                minIdx = v.getIndex();
                nextVar = v;
            }
        }
        return nextVar;
    }
}
