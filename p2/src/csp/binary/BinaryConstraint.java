package csp.binary;

import java.util.ArrayList;

public final class BinaryConstraint {
  private int firstVar, secondVar;
  private ArrayList<BinaryTuple> tuples;

  public BinaryConstraint(int fv, int sv, ArrayList<BinaryTuple> t) {
    firstVar = fv;
    secondVar = sv;
    tuples = t;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("c(" + firstVar + ", " + secondVar + ")\n");
    for (BinaryTuple bt : tuples)
      result.append(bt + "\n");
    return result.toString();
  }

  // SUGGESTION: You will want to add methods here to reason about the constraint

  public int getFirstVar() {
    return this.firstVar;
  }

  public int getSecondVar() {
    return this.secondVar;
  }

  public ArrayList<BinaryTuple> getTuples() {
    return this.tuples;
  }

}
