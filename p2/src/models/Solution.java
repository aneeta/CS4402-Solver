package models;

import java.util.Map;

public class Solution {

    private int searchNodeCount;
    private int arcRevisions;
    private Map<Integer, Integer> assignments;

    public Solution(int searchNodeCount, int arcRevisions, Map<Integer, Integer> assignments) {
        this.searchNodeCount = searchNodeCount;
        this.arcRevisions = arcRevisions;
        this.assignments = assignments;
    }

    public int getSearchNodeCount() {
        return this.searchNodeCount;
    }

    public void setSearchNodeCount(int searchNodeCount) {
        this.searchNodeCount = searchNodeCount;
    }

    public int getArcRevisions() {
        return this.arcRevisions;
    }

    public void setArcRevisions(int arcRevisions) {
        this.arcRevisions = arcRevisions;
    }

    public Map<Integer, Integer> getAssignments() {
        return this.assignments;
    }

    public void setAssignments(Map<Integer, Integer> assignments) {
        this.assignments = assignments;
    }

}
