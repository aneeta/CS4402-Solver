package solver.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import csp.binary.BinaryCSP;
import models.Variable;
import solver.heuristics.value.ValueHeuristic;
import solver.heuristics.variable.VariableHeuristic;

public class Algorithm {
    List<Variable> unassignedVarList;
    VariableHeuristic varOrdering;
    ValueHeuristic valOrdering; // val ordering always ascending
    Map<Integer, Integer> assignments;
    int searchNodeCount;
    int arcRevisions;
    BinaryCSP csp;
    boolean solutionFound;

    public Algorithm(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp) {
        this.varOrdering = varOrdering;
        this.valOrdering = valOrdering;
        this.csp = csp;
        this.unassignedVarList = toVariableList(csp);
        this.assignments = new HashMap<>();
        this.searchNodeCount = 0;
        this.arcRevisions = 0;
        this.solutionFound = false;
    }

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
        // String arcAndNodes = String.format("%d\n%d\n", arcRevisions,
        // searchNodeCount);

        System.out.println(searchNodeCount);
        System.out.println(arcRevisions);
        for (Integer i : assignments.values()) {
            System.out.println(i);
        }
    }

    public boolean solve() {
        return false;
    }
}