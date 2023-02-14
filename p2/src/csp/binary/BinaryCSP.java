package csp.binary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BinaryCSP {
  private int[][] domainBounds;
  private ArrayList<BinaryConstraint> constraints;

  public BinaryCSP(int[][] db, ArrayList<BinaryConstraint> c) {
    domainBounds = db;
    constraints = c;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("CSP:\n");
    for (int i = 0; i < domainBounds.length; i++)
      result.append("Var " + i + ": " + domainBounds[i][0] + " .. " + domainBounds[i][1] + "\n");
    for (BinaryConstraint bc : constraints)
      result.append(bc + "\n");
    return result.toString();
  }

  public int getNumberVariables() {
    return domainBounds.length;
  }

  public int getLB(int varIndex) {
    return domainBounds[varIndex][0];
  }

  public int getUB(int varIndex) {
    return domainBounds[varIndex][1];
  }

  public ArrayList<BinaryConstraint> getConstraints() {
    return constraints;
  }

  public List<BinaryConstraint> getConstraints(int var) {
    return constraints.stream().filter(x -> x.getFirstVar() == var).collect(Collectors.toList());
  }

  public List<BinaryConstraint> getConstraints(int var1, int var2) {
    return constraints.stream().filter(x -> (x.getFirstVar() == var1) && (x.getSecondVar() == var2))
        .collect(Collectors.toList());
  }
}
