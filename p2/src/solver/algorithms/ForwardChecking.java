package solver.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import csp.binary.BinaryCSP;
import models.Variable;
import solver.Algorithm;
import solver.Heuristic;

public class ForwardChecking {
    private List<Variable> unassignedVarList;
    private Heuristic varOrdering, valOrdering; // val ordering always ascending
    private Map<Integer, Integer> assignments;
    private int searchNodeCount;
    private int arcRevisions;
    private BinaryCSP csp;
    private boolean solutionFound;

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

    public ForwardChecking(Heuristic varOrdering, Heuristic valOrdering, BinaryCSP csp) {
        this.varOrdering = varOrdering;
        this.valOrdering = valOrdering;
        this.csp = csp;
        this.searchNodeCount = 0;
        this.arcRevisions = 0;
        this.assignments = new HashMap();
        this.unassignedVarList = toVariableList(csp);
        this.solutionFound = false;
    }

    public boolean solve() {
        forwardChecking();
        if (assignmentComplete()) {
            return true;
        }
        return false;
    }

    public void forwardChecking() {
        if (assignmentComplete()) {
            printSolution();
            return;
        }
        // select variable from varList (based on a heuristic?)
        Variable nextVar = getNextVar();
        // select value from var domain
        int nextVal = nextVar.getNextVal();
        branchLeft(nextVar, nextVal);
        branchRight(nextVar, nextVal);
    }

    public void branchLeft(Variable var, int val) {
        searchNodeCount++;
        // assign value
        assignments.put(var.getIndex(), val);
        unassignedVarList.remove(var);

        List<Integer> currentVarDomain = var.getDomain();
        // restrict domain
        var.setDomain(new ArrayList<>(List.of(val)));

        checkAndPrune(var);

        // reset domain
        var.setDomain(currentVarDomain);
        // unassign value (no solution found down the branch)
        assignments.remove(var.getIndex());
        unassignedVarList.add(var);
    }

    public void branchRight(Variable var, int val) {
        searchNodeCount++;
        // remove value that didn't work out on the left branch
        var.pruneDomain(val);
        if (var.getDomain().size() > 0) {
            checkAndPrune(var);
        }
        // restore value (no solution found down the branch)
        var.setDomain(new ArrayList<>(List.of(val)));
    }

    public void checkAndPrune(Variable var) {
        Map<Integer, List<Integer>> currentDomains = new HashMap();
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

    public boolean assignmentComplete() {
        // check if any unassigned values are left
        if (this.assignments.size() == csp.getNumberVariables()) {
            return true;
        }
        return false;
    }

    public Variable getNextVar() {
        return varOrdering.getNext(unassignedVarList);

    }

    public void printSolution() {
        // String arcAndNodes = String.format("%d\n%d\n", arcRevisions, searchNodeCount);
        System.out.println(arcRevisions);
        System.out.println(searchNodeCount);
        for (Integer i : assignments.values()) {
            System.out.println(i);
        }
    }
}
