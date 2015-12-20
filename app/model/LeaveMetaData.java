package model;

import java.util.List;

/**
 * Created by nmahajan on 12/8/2015.
 */
public class LeaveMetaData
{
    List<Leave> leaves;

    public List<Leave> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Leave> leaves) {
        this.leaves = leaves;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    int totalRows;
}
