package models;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import csp.binary.BinaryCSP;
import csp.binary.BinaryConstraint;
import csp.binary.BinaryTuple;

public class Variable {
    private int index;
    private List<Integer> domain;
    private BinaryCSP csp;

    public Variable(int index, List<Integer> domain, BinaryCSP csp) {
        this.index = index;
        this.domain = domain;
        this.csp = csp;
    }

    public void pruneDomain(int val) {
        this.domain.remove(Integer.valueOf(val));
    }

    public void pruneDomain(Set<Integer> val) {
        for (Integer i : val) {
            this.domain.remove(i);
        }
    }

    public void restoreDomain(int val) {
        this.domain.add(val);
    }

    public void restoreDomain(Set<Integer> val) {
        for (Integer i : val) {
            this.domain.add(i);
        }
    }

    public boolean checkArcConsistency(Variable otherVar) {
        // X is arc-consistent with respect to another variable Y
        // if for every value in the current domain X there is some value
        // in the domain Y that satisfies the binary constraint on the arc (X,Y)

        // if at least one of the values in the set is also in otherVar's domain
        // then the arc is consistent
        if (getPrunedDomain(otherVar).size() <= 0) {
            // empty domain
            return false;
        }
        // the domain is non-empty
        return true;
    }

    public Set<Integer> getConsistentDomain(Variable otherVar) {
        int var1 = this.index;
        int var2 = otherVar.getIndex();

        Set<Integer> otherVarConsistentVals = new HashSet();

        for (BinaryConstraint bc : csp.getConstraints(var1, var2)) {
            for (BinaryTuple bt : bc.getTuples()) {
                if (domain.contains(bt.getFirstVal())) {
                    otherVarConsistentVals.add(bt.getSecondVal());
                }
            }
        }
        return otherVarConsistentVals;
    }

    public List<Integer> getPrunedDomain(Variable otherVar) {
        Set<Integer> otherVarConsistentVals = getConsistentDomain(otherVar);
        Set<Integer> intersection = new HashSet<Integer>(otherVar.getDomain()); // get the domain of otherVar (Y)
        intersection.retainAll(otherVarConsistentVals);

        return new ArrayList<>(intersection);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Integer> getDomain() {
        return this.domain;
    }

    public void setDomain(List<Integer> domain) {
        this.domain = domain;
    }

    public BinaryCSP getCsp() {
        return this.csp;
    }

    public void setCsp(BinaryCSP csp) {
        this.csp = csp;
    }

    public int getNextVal() {
        return Collections.min(domain);
    }

}
