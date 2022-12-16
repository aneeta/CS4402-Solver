package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import csp.binary.BinaryCSP;
import csp.binary.BinaryConstraint;
import csp.binary.BinaryTuple;
import models.Variable;

public class AlgorithmsTest {

    private List<Variable> vars;
    private Variable varOne;
    private Variable varTwo;
    private BinaryCSP csp;

    @Before
    public void setUp() {
        int[][] db = { { 2, 4 }, { 0, 1 } };
        ArrayList<BinaryConstraint> constraints = new ArrayList<>();
        ArrayList<BinaryTuple> tuples = new ArrayList<>();
        tuples.add(new BinaryTuple(2, 0));
        tuples.add(new BinaryTuple(4, 0));
        tuples.add(new BinaryTuple(3, 1));
        constraints.add(new BinaryConstraint(0, 1, tuples));
        csp = new BinaryCSP(db, constraints);

        vars = new ArrayList<>();

        varOne = new Variable(0, new ArrayList<>(Arrays.asList(2, 3, 4)), csp);
        varTwo = new Variable(1, new ArrayList<>(Arrays.asList(0, 1)), csp);

        vars.add(varOne);
        vars.add(varTwo);
    }

    @Test
    public void testFC() {
        // TODO
    }

    @Test
    public void testMAC() {
        // TODO

    }

    @Test
    public void testAC3() {
        // TODO
    }

}

    

    