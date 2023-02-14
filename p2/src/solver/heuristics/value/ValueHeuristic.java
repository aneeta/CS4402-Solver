package solver.heuristics.value;

import java.util.List;

public interface ValueHeuristic {
    public int getNext(List<Integer> domain);
}
