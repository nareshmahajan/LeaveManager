package model;

import java.util.Date;

/**
 * Created by nareshdon on 28-11-2015.
 */
public class FilterBean
{
    private DateRange dateRange;
    private String[] department;
    private String employee;
    private Integer currentPage;

    public Integer getRowSize() {
        return rowSize;
    }

    public void setRowSize(Integer rowSize) {
        this.rowSize = rowSize;
    }

    private Integer rowSize;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }



    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String[] getDepartment() {
        return department;
    }

    public void setDepartment(String[] department) {
        this.department = department;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }


}

class DateRange
{
    Date startDate;
    Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}