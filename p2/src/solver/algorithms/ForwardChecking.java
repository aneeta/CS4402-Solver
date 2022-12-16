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

    public ForwardChecking(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp,
            boolean allSolutions) {
        super(varOrdering, valOrdering, csp, allSolutions);
    }

    @Override
    public boolean solve() {
        forwardChecking();
        if (solutions.size() > 0) {
            printSolution(0);
            return true;
        }
        System.out.println(searchNodeCount);
        System.out.println(arcRevisions);
        return false;
    }

    public void forwardChecking() {
        if (assignmentComplete()) {
            saveSolution(searchNodeCount, arcRevisions, assignments);
            return;
        }
        // select variable from varList (based on a heuristic?)
        Variable nextVar = getNextVar();
        // select value from var domain
        int nextVal = nextVar.getNextVal(valOrdering);
        branchLeft(nextVar, nextVal);
        if (solutions.size() > 0 && !allSolutions) {
            return;
        }
        branchRight(nextVar, nextVal);
    }

    public void branchLeft(Variable var, int val) { // true if solution found
        searchNodeCount++;
        // assign value
        assignments.put(var.getIndex(), val);
        unassignedVarList.remove(var);

        List<Integer> currentVarDomain = new ArrayList<>();
        currentVarDomain.addAll(var.getDomain());
        // restrict domain (prune)
        var.setDomain(new ArrayList<>(List.of(val)));

        checkAndPrune(var);

        if (solutions.size() > 0 && !allSolutions) {
            // ending search after first found solution
            return;
        }

        // reset domain (undo pruning)
        var.setDomain(currentVarDomain);
        // unassign value (no solution found down the branch)
        assignments.remove(var.getIndex());
        unassignedVarList.add(var);
    }

    public void branchRight(Variable var, int val) { // true if solution found
        searchNodeCount++;
        List<Integer> currentVarDomain = new ArrayList<>();
        currentVarDomain.addAll(var.getDomain());
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
                if (var.getIndicesNeighbours().contains(v.getIndex())) {
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
            if (var.getIndicesNeighbours().contains(v.getIndex())) {
                v.setDomain(currentDomains.get(v.getIndex()));
            }
        }
    }

    public boolean reviseFutureArcs(Variable var) {
        arcRevisions++;
        for (Variable v : unassignedVarList) {
            // only neighbouring (connected indiced)
            if (var.getIndicesNeighbours().contains(v.getIndex())) {
                boolean consistent = var.checkArcConsistency(v);
                if (!consistent) {
                    return false;
                }
            }
        }
        return true;
    }

}
