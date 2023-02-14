
import solver.algorithms.Algorithm;
import solver.algorithms.ForwardChecking;
import solver.algorithms.MaintainingArcConsistency;
import solver.heuristics.value.MinConflicts;
import solver.heuristics.value.ValueHeuristic;
import solver.heuristics.value.AscendingVal;
import solver.heuristics.value.MinConflicts;
import solver.heuristics.variable.Ascending;
import solver.heuristics.variable.VariableHeuristic;
import solver.heuristics.variable.SmallestDomainFirst;
import csp.binary.BinaryCSP;
import csp.binary.BinaryCSPReader;

public class Main {
    public static void main(String[] args) {
        // Get the parameters
        if (args.length < 4) {
            System.err.println(
                    "Usage: java Main <file.csp> <algorithm> <variable ordering> <value ordering>");
            return;
        }
        String filePath = args[0];
        String algorithm = args[1];
        String varOrder = args[2];
        String valOrder = args[3];
        boolean allSolutions = false;

        // Parse input file
        BinaryCSPReader reader = new BinaryCSPReader();
        BinaryCSP csp = reader.readBinaryCSP(filePath);

        // map input to variable heuristic
        VariableHeuristic varHeuristic;
        if (varOrder.toLowerCase().equals("asc")) {
            varHeuristic = new Ascending();
        } else if (varOrder.toLowerCase().equals("sdf")) {
            varHeuristic = new SmallestDomainFirst();
        } else {
            System.err.println(
                    "Unrecognised variable ordering! Choose 'asc' or 'sdf'.");
            return;
        }

        // map input to value heuristic
        ValueHeuristic valHeuristic;
        if (valOrder.toLowerCase().equals("asc")) {
            valHeuristic = new AscendingVal();
        } else if (valOrder.toLowerCase().equals("mc")) {
            valHeuristic = new MinConflicts(csp);
        } else {
            System.err.println(
                    "Unrecognised variable ordering! Choose 'asc' or 'mc'.");
            return;
        }

        // map input to algorithm
        Algorithm alg;
        if (algorithm.toLowerCase().equals("fc")) {
            alg = new ForwardChecking(varHeuristic, valHeuristic, csp, allSolutions);
        } else if (algorithm.toLowerCase().equals("mac")) {
            alg = new MaintainingArcConsistency(varHeuristic, valHeuristic, csp, allSolutions);
        } else {
            System.err.println(
                    "Unrecognised algorithm! Choose 'fc' or 'mac'.");
            return;
        }

        // solve instance
        alg.solve();

    }
}
