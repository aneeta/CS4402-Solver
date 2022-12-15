import solver.Ascending;
import solver.Solver;
import solver.algorithms.ForwardChecking;
import csp.binary.BinaryCSP;
import csp.binary.BinaryCSPReader;

public class Main {
    public static void main(String[] args) {
        // Get the parameters
        if (args.length < 4) {
            System.out.println(
                    "Usage: java Main <file.csp> <algorithm> <variable ordering> <value ordering>");
            return;
        }
        String filePath = args[0];
        String algorithm = args[1];
        String varOrder = args[2];
        String valOrder = args[3];

        // Parse input file
        BinaryCSPReader reader = new BinaryCSPReader();
        BinaryCSP csp = reader.readBinaryCSP(filePath);

        ForwardChecking fc = new ForwardChecking(new Ascending(), null, csp);
        fc.solve();
        // if (fc.solve()) {
        //     String sols = fc.printSolution();
        //     System.out.println();
        // } else {
        //     System.err.println("No Solutions Found!");
        // }

        // map

        // Solve
        // Solver solver = new Solver(csp);

        // Print output
    }
}