package solver.heuristics.value;

public class AscendingVal implements ValueHeuristic {

    @Override
    public int getNext(List<Integer> domain) {
        return Collections.min(domain);
    }
}
