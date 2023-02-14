package solver.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import csp.binary.BinaryCSP;
import models.Variable;
import solver.heuristics.value.ValueHeuristic;
import solver.heuristics.variable.VariableHeuristic;

public class ForwardChecking extends Algorithm {

    public ForwardChecking(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp) {
        super(varOrdering, valOrdering, csp);
    }

    @Override
    public boolean solve() {
        forwardChecking();
        if (assignmentComplete()) {
            printSolution();
            return true;
        }
        return false;
    }

    public void forwardChecking() {
        if (assignmentComplete()) {
            // printSolution();
            // TODO don't traverse the whole search tree, stop after frist solution found
            return;
        }
        // select variable from varList (based on a heuristic?)
        Variable nextVar = getNextVar();
        // select value from var domain
        int nextVal = nextVar.getNextVal(valOrdering);
        branchLeft(nextVar, nextVal);
        if (assignmentComplete()) {
            return;
        }
        branchRight(nextVar, nextVal);
    }

    public void checkComplete() {

    }

    public void branchLeft(Variable var, int val) { // true if solution found
        searchNodeCount++;
        // assign value
        assignments.put(var.getIndex(), val);
        unassignedVarList.remove(var);

        List<Integer> currentVarDomain = var.getDomain(); //TODO maybe do a copy instead of pointer??
        // restrict domain
        var.setDomain(new ArrayList<>(List.of(val)));

        checkAndPrune(var);

        // dont reset if finished
        if (assignmentComplete()) {
            return;
        }

        // reset domain
        var.setDomain(currentVarDomain);
        // unassign value (no solution found down the branch)
        assignments.remove(var.getIndex());
        unassignedVarList.add(var);
    }

    public void branchRight(Variable var, int val) { // true if solution found
        searchNodeCount++;
        List<Integer> currentVarDomain = var.getDomain();
        // remove value that didn't work out on the left branch
        var.pruneDomain(val);
        if (var.getDomain().size() > 0) {
            checkAndPrune(var);
        }
        // restore value (no solution found down the branch)
        var.setDomain(currentVarDomain);
    }

    public void checkAndPrune(Variable var) {
        Map<Integer, List<Integer>> currentDomains = new HashMap<>();
        if (reviseFutureArcs(var)) {
            for (Variable v : unassignedVarList) {
                if (!v.equals(var)) {
                    currentDomains.put(v.getIndex(), v.getDomain());
                    // prune domain
                    List<Integer> newDomain = var.getPrunedDomain(v);
                    v.setDomain(newDomain);
                }
            }
            forwardChecking();
        } else {
            // no pruning took place
            return;
        }
        // unprune domain
        for (Variable v : unassignedVarList) {
            v.setDomain(currentDomains.get(v.getIndex()));
        }
    }

    public boolean reviseFutureArcs(Variable var) {
        arcRevisions++;
        for (Variable v : unassignedVarList) {
            if (!v.equals(var)) { // shouldn't be there anyway
                boolean consistent = var.checkArcConsistency(v);
                if (!consistent) {
                    return false;
                }
            }
        }
        return true;
    }

    
}
