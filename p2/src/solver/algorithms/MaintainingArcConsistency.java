package solver.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import csp.binary.BinaryCSP;
import csp.binary.BinaryTuple;
import csp.binary.BinaryConstraint;
import models.Arc;
import models.Variable;
import solver.heuristics.value.ValueHeuristic;
import solver.heuristics.variable.VariableHeuristic;

public class MaintainingArcConsistency extends Algorithm {

    public MaintainingArcConsistency(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp,
            boolean allSolutions) {
        super(varOrdering, valOrdering, csp, allSolutions);
        allVarList = new ArrayList<>();
        allVarList.addAll(unassignedVarList);
    }

    @Override
    public boolean solve() {
        // arc consistency enforced
        if (AC3(getInitQueue())) {
            maintainingArcConsistency();
        } else {
            System.err.println("Problem not arc consistent!");
            return false;
        }
        if (solutions.size() > 0) {
            printSolution(0);
            return true;
        }
        System.out.println(searchNodeCount);
        System.out.println(arcRevisions);
        return false;
    }

    public boolean AC3(List<Arc> queue) {
        while (queue.size() > 0) {
            Arc arc = queue.remove(0);
            if (revise(arc)) {
                Variable var1 = getVariable(arc.getVar1());
                if (var1.getDomain().size() == 0) {
                    return false;
                }
                for (int i : var1.getIndicesNeighbours()) {
                    if (i != arc.getVar2()) {
                        // only enqueue if not already in the queue
                        boolean alreadyEnqueued = false;
                        for (Arc a : queue) {
                            if (a.getVar1() == i && a.getVar2() == arc.getVar1()) {
                                alreadyEnqueued = true;
                            }
                        }
                        if (!alreadyEnqueued) {
                            queue.add(new Arc(i, arc.getVar1()));
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean revise(Arc arc) {
        // assuming only one c(X, Y) in a file, and X < Y
        boolean revised = false;
        Variable var1 = getVariable(arc.getVar1());
        Variable var2 = getVariable(arc.getVar2());
        List<Integer> domainVar1 = new ArrayList<>();
        List<Integer> domainVar2 = new ArrayList<>();

        domainVar1.addAll(var1.getDomain()); // X
        domainVar2.addAll(var2.getDomain()); // Y

        List<BinaryConstraint> constraints = csp.getConstraints(arc.getVar1(), arc.getVar2());
        if (constraints.size() != 0) {
            // case where we have an explicit constraint c(X,Y)
            for (int i : domainVar1) {
                boolean existsY = false;
                for (BinaryConstraint bc : constraints) {
                    for (BinaryTuple bt : bc.getTuples()) {
                        if (bt.getFirstVal() == i) {
                            if (var2.getDomain().contains(bt.getSecondVal())) {
                                existsY = true;
                            }
                        }
                    }
                }
                if (!existsY) {
                    var1.pruneDomain(i);
                    revised = true;
                    arcRevisions++;
                }
            }
        } else {
            // case where we are looking at the reverse arc direction c(Y,X)
            // it is not given in the csp explicitly, so have to transpose it here
            constraints = csp.getConstraints(arc.getVar2(), arc.getVar1());
            for (int i : domainVar1) {
                boolean existsY = false;
                for (BinaryConstraint bc : constraints) {
                    for (BinaryTuple bt : bc.getTuples()) {
                        if (bt.getSecondVal() == i) {
                            if (var2.getDomain().contains(bt.getFirstVal())) {
                                existsY = true;
                            }
                        }
                    }
                }
                if (!existsY) {
                    var1.pruneDomain(i);
                    revised = true;
                    arcRevisions++;
                }
            }
        }
        return revised;
    }

    public List<Arc> getInitQueue() {
        List<Arc> queue = new ArrayList<>();
        for (Variable v : unassignedVarList) {
            for (int i : v.getIndicesNeighbours()) {
                queue.add(new Arc(v.getIndex(), i));
                queue.add(new Arc(i, v.getIndex()));
            }
        }
        return queue;
    }

    public void maintainingArcConsistency() {
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

    public void branchLeft(Variable var, int val) {
        searchNodeCount++;
        // assign value
        assignments.put(var.getIndex(), val);
        unassignedVarList.remove(var);

        List<Integer> currentVarDomain = var.getDomain();
        // restrict domain
        var.setDomain(new ArrayList<>(List.of(val)));
        // recursively search lower into the tree
        checkAndPrune(var);
        // stop traversal
        if (solutions.size() > 0 && !allSolutions) {
            return;
        }
        // reset domain
        var.setDomain(currentVarDomain);
        // unassign value (no solution found down the branch)
        assignments.remove(var.getIndex());
        unassignedVarList.add(var);
    }

    public void branchRight(Variable var, int val) {
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
        for (Variable v : unassignedVarList) {
            List<Integer> domainCopy = new ArrayList<>();
            domainCopy.addAll(v.getDomain());
            currentDomains.put(v.getIndex(), domainCopy);            
        }
        List<Arc> queue = new ArrayList<>();
        for (int i : var.getIndicesNeighbours()) {
            queue.add(new Arc(var.getIndex(), i));
            queue.add(new Arc(i, var.getIndex()));
        }

        if (AC3(queue)) {
            maintainingArcConsistency();
        }
        // unprune domains
        for (Variable v : unassignedVarList) {
            v.setDomain(currentDomains.get(v.getIndex()));
        }
    }
}
