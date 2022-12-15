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
import solver.Algorithm;
import solver.Heuristic;

public class MaintainingArcConsistency {
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

    public MaintainingArcConsistency(Heuristic varOrdering, Heuristic valOrdering, BinaryCSP csp) {
        this.varOrdering = varOrdering;
        this.valOrdering = valOrdering;
        this.csp = csp;
        this.searchNodeCount = 0;
        this.arcRevisions = 0;
        this.assignments = new HashMap();
        this.unassignedVarList = toVariableList(csp);
        this.solutionFound = false;
    }

    public void solve() {
        // arc consistency enforced
        if (AC3()) {
            maintainingArcConsistency();
        } else {
            System.out.println("Problem not arc consistent!");
        }
    }

    public Variable getVariable(int idx) {
        // TODO what if list.size() != 0??? fix!!

        return unassignedVarList.stream().filter(x -> x.getIndex() == idx).collect(Collectors.toList()).get(0);
    }

    public boolean AC3() {

        List<Arc> queue = getQueue();
        // if (queue.size() <= 0) {
        // // TODO
        // System.out.println("Why is the queue empty??");
        // }
        while (queue.size() > 0) {
            Arc arc = queue.get(0);
            queue.remove(0);
            if (revise(arc)) {
                Variable var1 = getVariable(arc.getVar1());
                if (var1.getDomain().size() == 0) {
                    return false;
                }
                for (int i : var1.getIndicesNeighbours()) {
                    if (i != arc.getVar2()) {
                        queue.add(new Arc(i, arc.getVar1()));
                    }
                }
            }
        }
        return true;

    }

    public boolean revise(Arc arc) {
        arcRevisions++;

        boolean revised = false;
        Variable var1 = getVariable(arc.getVar1());
        Variable var2 = getVariable(arc.getVar2());
        List<Integer> domainVar1 = new ArrayList();
        domainVar1.addAll(var1.getDomain());
        for (int i : domainVar1) {
            boolean existsY = false;
            for (BinaryConstraint bc : csp.getConstraints(arc.getVar1(), arc.getVar2())) {
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
            }
        }
        return revised;
    }

    public List<Arc> getQueue() {
        List<Arc> queue = new ArrayList<>();
        for (Variable v : unassignedVarList) {
            for (int i : v.getIndicesNeighbours()) {
                queue.add(new Arc(v.getIndex(), i));
                // adding the reverse, some may be redundant
                queue.add(new Arc(i, v.getIndex()));
            }
        }
        return queue;

    }

    public List<Arc> initQueue() {
        List<Arc> queue = new ArrayList<>();
        for (BinaryConstraint bc : csp.getConstraints()) {
            // arc
            queue.add(new Arc(bc.getFirstVar(), bc.getSecondVar())); // ints
            // reverse arc (since bidirectional)
            queue.add(new Arc(bc.getSecondVar(), bc.getFirstVar()));
        }
        return queue;
    }

    public void maintainingArcConsistency() {
        // if (AC3()) {
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
        // }
        // else inconsistent

        //
        // List<Arc> queue = initQueue();
        // AC3(queue);

        // List<Integer> currentVarDomain = var.getDomain();

        // searchNodeCount++;
        // // assign value
        // assignments.put(nextVar.getIndex(), nextVal);
        // unassignedVarList.remove(nextVar);

        // if (assignmentComplete()) {
        // printSolution();
        // return;
        // } else if () { // branch left

        // }
        // // reset domain
        // var.setDomain(currentVarDomain);
        // // unassign value (no solution found down the branch)
        // assignments.remove(var.getIndex());
        // unassignedVarList.add(var);

        // // branch right
        // searchNodeCount++;
        // // remove value that didn't work out on the left branch
        // nextVar.pruneDomain(nextVal);
        // if (nextVar.getDomain().size() > 0) {
        // checkAndPrune(var);
        // }
        // // restore value (no solution found down the branch)
        // var.setDomain(new ArrayList<>(List.of(val)));

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
        Map<Integer, List<Integer>> currentDomains = new HashMap();
        for (Variable v : unassignedVarList) {
            currentDomains.put(v.getIndex(), v.getDomain());
        }
        if (AC3()) {
            maintainingArcConsistency();
        }
        // unprune domains
        for (Variable v : unassignedVarList) {
            v.setDomain(currentDomains.get(v.getIndex()));
        }
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

    //
    public void printSolution() {
        // String arcAndNodes = String.format("%d\n%d\n", arcRevisions,
        // searchNodeCount);
        System.out.println(arcRevisions);
        System.out.println(searchNodeCount);
        for (Integer i : assignments.values()) {
            System.out.println(i);
        }
    }
}
