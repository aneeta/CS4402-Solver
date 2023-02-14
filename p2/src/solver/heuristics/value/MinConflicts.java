package solver.heuristics.value;

public class MinConflicts implements ValueHeuristic {

    @Override
    public int getNext(List<Integer> domain) {
        int minCount = 10 * 8;
        int retVal = -1;
        for (int i : domain) {
            // Count number of incompatible values
            // in domains of future variables

            // TODO

        }
        return retVal;
    }

}
