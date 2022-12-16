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

    private List<Variable> allVarList;

    public MaintainingArcConsistency(VariableHeuristic varOrdering, ValueHeuristic valOrdering, BinaryCSP csp) {
        super(varOrdering, valOrdering, csp);
        allVarList = new ArrayList<>();
        allVarList.addAll(unassignedVarList);
    }

    @Override
    public boolean solve() {
        // arc consistency enforced
        if (AC3(getInitQueue())) {
            maintainingArcConsistency();
        } else {
            System.out.println("Problem not arc consistent!");
            return false;
        }
        if (assignmentComplete()) {
            return true;
        }
        return false;
    }

    public Variable getVariable(int idx) {
        // TODO what if list.size() != 0??? fix!!

        return allVarList.stream().filter(x -> x.getIndex() == idx).collect(Collectors.toList()).get(0);
    }

    public boolean AC3(List<Arc> queue) {
        // List<Arc> queue = getQueue();
        // if (queue.size() <= 0) {
        // // TODO
        // System.out.println("Why is the queue empty??");
        // }
        while (queue.size() > 0) {
            // Arc arc = queue.get(0);
            Arc arc = queue.remove(0);
            if (revise(arc)) {
                Variable var1 = getVariable(arc.getVar1());
                if (var1.getDomain().size() == 0) {
                    return false;
                }
                for (int i : var1.getIndicesNeighbours()) {
                    if (i != arc.getVar2()) {
                        // TODO should be if arc not there already
                        queue.add(new Arc(i, arc.getVar1()));
                    }
                }
            }
        }
        return true;

    }

    public boolean revise(Arc arc) {
        // assuming only one c(X, Y) in a file, and X < Y
        arcRevisions++;

        boolean revised = false;
        Variable var1 = getVariable(arc.getVar1());
        Variable var2 = getVariable(arc.getVar2());
        List<Integer> domainVar1 = new ArrayList<>();
        List<Integer> domainVar2 = new ArrayList<>();

        domainVar1.addAll(var1.getDomain()); // X
        domainVar2.addAll(var2.getDomain()); // Y

        List<BinaryConstraint> constraints = csp.getConstraints(arc.getVar1(), arc.getVar2());
        if (constraints.size() != 0) {
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
                }
            }
        } else {
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
                // adding the reverse, some may be redundant
                queue.add(new Arc(i, v.getIndex()));
            }
        }
        return queue;

    }

    // public List<Arc> initQueue() {
    // List<Arc> queue = new ArrayList<>();
    // for (BinaryConstraint bc : csp.getConstraints()) {
    // // arc
    // queue.add(new Arc(bc.getFirstVar(), bc.getSecondVar())); // ints
    // // reverse arc (since bidirectional)
    // queue.add(new Arc(bc.getSecondVar(), bc.getFirstVar()));
    // }
    // return queue;
    // }

    public void maintainingArcConsistency() {
        if (assignmentComplete()) {
            printSolution();
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

    public void branchLeft(Variable var, int val) {
        searchNodeCount++;
        // assign value
        assignments.put(var.getIndex(), val);
        unassignedVarList.remove(var);

        List<Integer> currentVarDomain = var.getDomain();
        // restrict domain
        var.setDomain(new ArrayList<>(List.of(val)));

        checkAndPrune(var);

        if (assignmentComplete()) {
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
        // for (Variable v : assign) {
        for (int i : var.getIndicesNeighbours()) {
            queue.add(new Arc(var.getIndex(), i));
            // adding the reverse, some may be redundant
            queue.add(new Arc(i, var.getIndex()));
        }
        // }
        if (AC3(queue)) {
            maintainingArcConsistency();
        }
        // if (assignmentComplete()) {
        //     return;
        // }
        // unprune domains
        for (Variable v : unassignedVarList) {
            v.setDomain(currentDomains.get(v.getIndex()));
        }
    }

    }

    


    

    

    
    
    
    
    
        
        
        
    

    
    
    
    

    
    
        
    