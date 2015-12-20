package model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import org.springframework.util.StringUtils;
import play.data.format.Formats;
import security.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Created by nareshdon on 03-12-2015.
 */
@Entity
@Table(name="leaves")
public class CalendarEventBean {

    @Column(name="employeeId")
    private String title;

    @Column(name="fromDate")
    @Formats.DateTime(pattern="yyyy-MM-dd")
    private Date start;

    @Column(name="toDate")
    @Formats.DateTime(pattern="yyyy-MM-dd")
    private Date end;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public static Model.Finder<Long, CalendarEventBean> find = new Model.Finder<Long,CalendarEventBean>(CalendarEventBean.class);

    public static List<CalendarEventBean> calendarEvents(FilterBean filterBean)
    {
        Date dateRangeStartDate = null;
        Date dateRangeEndDate = null;
        String userId = null;
        List<CalendarEventBean> leavesFromRaw = null;
        ExpressionList<CalendarEventBean> exprList = find.select("title,start,end").where();
        exprList.in("status", new Integer[]{LeaveStatus.PENDING.ordinal(), LeaveStatus.APPROVED.ordinal()});
        if(!StringUtils.isEmpty(filterBean.getEmployee())) {
            userId = filterBean.getEmployee();
            exprList.eq("employeeid", userId);
        }
        if(!StringUtils.isEmpty(filterBean.getDateRange()) && !StringUtils.isEmpty(filterBean.getDateRange().getStartDate())) {
            dateRangeStartDate = filterBean.getDateRange().getStartDate();
            dateRangeEndDate = filterBean.getDateRange().getEndDate();
            exprList.conjunction()
                    .or(Expr.between("fromdate", dateRangeStartDate, dateRangeEndDate), Expr.or(Expr.eq("fromdate", dateRangeStartDate), Expr.eq("fromdate", dateRangeEndDate)))
                    .endJunction();
        }
        if(!StringUtils.isEmpty(filterBean.getDepartment()) && StringUtils.isEmpty(filterBean.getEmployee()))
        {
            exprList.in("employeeid", User.getGroupMembers(filterBean.getDepartment()));
        }

        leavesFromRaw = exprList.findList();
        return leavesFromRaw;
    }
}
