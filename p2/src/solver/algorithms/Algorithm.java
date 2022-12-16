package solver.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import csp.binary.BinaryCSP;
import models.Solution;
import models.Variable;
import solver.heuristics.value.ValueHeuristic;
import solver.heuristics.variable.VariableHeuristic;

public class Algorithm {
    List<Variable> unassignedVarList;
    VariableHeuristic varOrdering;
    ValueHeuristic valOrdering;
    Map<Integer, Integer> assignments;
    int searchNodeCount;
    int arcRevisions;
    BinaryCSP csp;
    List<Solution> solutions;
    boolean allSolutions;
    List<Variable> allVarList;

    public Algorithm(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp, boolean allSolutions) {
        this.varOrdering = varOrdering;
        this.valOrdering = valOrdering;
        this.csp = csp;
        this.allSolutions = allSolutions;
        this.unassignedVarList = toVariableList(csp);
        this.assignments = new HashMap<>();
        this.searchNodeCount = 0;
        this.arcRevisions = 0;
        this.solutions = new ArrayList<>();
        allVarList = new ArrayList<>();
        allVarList.addAll(unassignedVarList);
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

    public void printSolution(int i) {
        Solution sol = solutions.get(i);

        System.out.println(sol.getSearchNodeCount());
        System.out.println(sol.getArcRevisions());
        for (Integer j : sol.getAssignments().values()) {
            System.out.println(j);
        }
    }

    public void saveSolution(int nodes, int arcs, Map<Integer, Integer> assignments) {
        Map<Integer, Integer> copyAssignments = new TreeMap<>();
        copyAssignments.putAll(assignments);
        solutions.add(new Solution(nodes, arcs, copyAssignments));
    }

    public boolean solve() {
        return false;

    }

    public Variable getVariable(int idx) {
        return allVarList.stream().filter(x -> x.getIndex() == idx).collect(Collectors.toList()).get(0);

    }
}