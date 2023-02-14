package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        Class[] testClasses = { HeuristicsTest.class, AlgorithmsTest.class };
        for (Class c : testClasses) {
            System.out.printf(">>> Running tests for %s:\n\n", c.getName());
            Result result = JUnitCore.runClasses(c);

            for (Failure failure : result.getFailures()) {
                System.out.printf("Failed %s:\n%s\n%s\n\n", failure.getTestHeader(), failure.getException(),
                        failure.getTrace());
            }

            System.out.printf("Passed %d out of %d tests.\n\n", result.getRunCount() - result.getFailureCount(),
                    result.getRunCount());

        }

    }
}