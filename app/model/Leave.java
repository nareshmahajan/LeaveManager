package model;

/**
 * Created by nareshdon on 03-11-2015.
 */

import com.avaje.ebean.*;
import org.springframework.util.StringUtils;
import play.Logger;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import security.model.User;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.mvc.Controller.session;

@Entity
@Table(name="leaves")
public class Leave extends Model{

    @Id
    @Column(name="leaveid")
    private int leaveId;

    @Column(name="employeeId")
    //@Constraints.Required(message = "Please Provide the UserName")
    private String employeeId;

    @Column(name="fromDate")

    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Constraints.Required(message = "Please Provide the From Date")
    private Date fromDate;

    @Column(name="toDate")
    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Constraints.Required(message = "Please Provide the To Date")
    private Date toDate;

    @Column(name="approverId")
    private String approverId;

    @Column(name="leaveReason")
    @Constraints.Required(message = "Please Provide the Leave Reason")
    private String leaveReason;

    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private LeaveStatus status;

    @Column(name="leavetype")
    //@Constraints.Required(message = "Please Provide the Leave Type")
    private String leaveType;

    @Transient
    private int leaveDays;



    @Column(name="maker")
    private String maker;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    static String weekDays[] = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};


    public static int yearlyLeaves = 20;

    public Leave(){

    }


    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }
    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public Date getFromDate() {
        return fromDate;
    }
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    public String getApproverId() {
        return approverId;
    }
    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }
    public String getLeaveReason() {
        return leaveReason;
    }
    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }
    public LeaveStatus getStatus() {
        return status;
    }
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    public int getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        this.leaveDays = leaveDays;
    }

    public static Finder<Long, Leave> find = new Finder<Long,Leave>(Leave.class);

    public List<ValidationError> validate()
    {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if(this.getFromDate().after(this.getToDate()))
        {

            errors.add(new ValidationError("fromDate", "To Date should be larger than From Date"));
        }
        else
        {
            if(isLeaveNotOverlapping(session().get("userName"), this.getFromDate(), this.getToDate()))
            {
                errors.add(new ValidationError("fromDate", "Leave Already Applied in the entered duration"));
            }
        }
        Logger.debug("In Validate From Date "+this.getFromDate());
        return errors;
    }
    /* Persistance Methods */
    public static PagedList<Leave> getUserLeavesOfCurrentYear(String userName, FilterBean filterBean)
    {
        int currentYear = getCurrentYear();
        Date startDate = getStartDateOfCurrentYear(currentYear);
        Date endDate = getEndDateOfCurrentYear(currentYear);
        PagedList<Leave> leavesFromRaw = null;
        try {

            leavesFromRaw = find.where().eq("employeeid", userName)
                    .conjunction()
                    .or(Expr.between("fromdate", startDate, endDate), Expr.or(Expr.eq("fromdate", startDate), Expr.eq("fromdate", endDate)))
                    .endJunction()
                    .findPagedList(filterBean.getCurrentPage() - 1, filterBean.getRowSize());

        } catch (Exception e) {
            e.printStackTrace();
            //throw new DAOException("Database Error");
        }

        for(Leave leave : leavesFromRaw.getList())
        {
            leave.setLeaveDays(getDaysFromLeave(leave));
        }
        return leavesFromRaw;
    }

    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        return currentYear;
    }

    /**
     * It returns start date of the current year.
     * @param currentYear
     * @return
     */
    public static Date getStartDateOfCurrentYear(int currentYear) {
        String startDate = currentYear+"0101";
        try {
            return sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * It returns end date of the current year.
     * @param currentYear
     * @return
     */
    public static Date getEndDateOfCurrentYear(int currentYear) {
        String startDate = currentYear+"1231";
        try {
            return sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * It returns no. of actual leaves excluding holidays and week ends for a given leave
     * @param
     * @return
     * @throws
     */
    public static int getDaysFromLeave(Leave reqLeave) {
        long days = 0;
        Date fromDate = reqLeave.getFromDate();
        Date toDate = reqLeave.getToDate();
        if(!fromDate.equals(toDate)) {
            days = daysBetween(toDate,fromDate);
            Long temp = new Long(days);
            int intDays = Integer.parseInt(temp.toString());
            int weekends = getWeekendsFromDates(fromDate,toDate);
            int holidays = getHolidaysFromDates(fromDate,toDate);
            intDays = intDays-(weekends+holidays);
            return intDays;
        }
        else {
            return 1;
        }
    }

    /**
     * It returns days between 2 dates including startDate and endDate
     * @param maxDate
     * @param minDate
     * @return
     */
    private static long daysBetween(Date maxDate, Date minDate) {
        return ((maxDate.getTime() - minDate.getTime())/86400000)+1;
    }

    /**
     * It returns no. of holidays in between fromDate and toDate
     * @param fromDate
     * @param toDate
     * @return
     * @throws
     */
    private static int getHolidaysFromDates(Date fromDate,Date toDate){
        int holidays = 0;
        holidays = Holiday.getHolidayCount(fromDate, toDate);
        return holidays;
    }

    /**
     * It returns no. of week ends in between fromDate and toDate
     * @param fromDate
     * @param toDate
     * @return
     */
    private static int getWeekendsFromDates(Date fromDate,Date toDate) {
        int weekends = 0,intLeaves;
		/*
		 * Resolved Bug 61
		 * reordered some statements in the for loop to include weekends
		 * at the start or end of the date range
		 */
        long diff = daysBetween(toDate,fromDate);
        Long temp = new Long(diff);
        intLeaves = Integer.parseInt(temp.toString());
        String leaveDays[] = new String[intLeaves];

        String day = getDayFromDate(fromDate);
        for(int i=0;i<intLeaves;i++) {
            leaveDays[i] = day;
            day = getNextDayFromWeekDays(day);
        }
        weekends = getNoOfWeekends(leaveDays);
        return weekends;
    }

    /**
     * It returns a day , given a date.
     * @param fromDate
     * @return
     */
    private static String getDayFromDate(Date fromDate) {
        String dateString = fromDate.toString();
        String day="";
        if(dateString.contains("Mon"))
            day = "Mon";
        else if(dateString.contains("Tue"))
            day = "Tue";
        else if(dateString.contains("Wed"))
            day =  "Wed";
        else if(dateString.contains("Thu"))
            day =  "Thu";
        else if(dateString.contains("Fri"))
            day =  "Fri";
		/*
		 * Added cases for sat and sun as it can also be a part in the applied date range
		 */
        else if(dateString.contains("Sat"))
            day="Sat";
        else if(dateString.contains("Sun"))
            day="Sun";
        return day;
    }

    /**
     * It returns next day from the given day.(e.g Tue for Mon)
     * @param day
     * @return
     */
    private static String getNextDayFromWeekDays(String day) {
        int index = getIndexFromWeekDays(day);
        if(index==6)
            return Array.get(weekDays, 0).toString();
        else
            //changed index from index+1
            return Array.get(weekDays, index+1).toString();
    }

    /**
     * It returns index of the array 'weekDays' given a day
     * @param day
     * @return
     */
    private static int getIndexFromWeekDays(String day) {
        int indexOfDay=0;
        for(int i=0;i<weekDays.length;i++) {
            if(weekDays[i].equals(day))
                indexOfDay = i;
        }
        return indexOfDay;
    }

    /**
     * It returns no. of week ends given an array of days (e.g 2 for {'Fri','Sat','Sun','Mon'})
     * @param leaveDays
     * @return
     */
    private static int getNoOfWeekends(String leaveDays[]) {
        int noOfWeekends =0;
        for(int i=0;i<leaveDays.length;i++) {
            String day = leaveDays[i];
            if(day.equals("Sat") || day.equals("Sun") )
                noOfWeekends++;
        }
        return noOfWeekends;
    }

    /**
     * It returns no. of leaves available for an employee
     * @param empId
     * @return
     * @throws
     */
    public static int getLeavesAvailableForEmployee(String empId){
        int leavesAvailable = 0;
        try {
            leavesAvailable = yearlyLeaves - getLeavesTakenByEmployee(empId);
        } catch (Exception e) {
        }
        return leavesAvailable;
    }

    /**
     * It returns no. of leaves employee has taken till now (that has been approved)
     * @param empId
     * @return
     * @throws Exception
     */
    public static int getLeavesTakenByEmployee(String empId) {
        int leavesTaken = 0;
        LinkedList listOfDates = null;
        try {
            listOfDates = getLeaveDates(empId);
        } catch (Exception e) {
        }
        while(!listOfDates.isEmpty()) {
            Date fromDate = (Date)listOfDates.removeFirst();
            Date toDate = (Date)listOfDates.removeFirst();
            if(!fromDate.equals(toDate)) {
                int leaves = calculateLeavesTaken(fromDate,toDate);
                leavesTaken = leavesTaken +leaves;
            }else {
                leavesTaken = leavesTaken + 1;
            }
        }
        return leavesTaken;
    }

    /**
     * It calculates no. of actual leaves excluding week ends and holidays in between fromDate and toDate
     * @param fromDate
     * @param toDate
     * @return
     * @throws
     */
    private static int calculateLeavesTaken(Date fromDate,Date toDate){
        int weakends,holidays,intLeaves;
        long leaves = daysBetween(toDate,fromDate);
        Long temp = new Long(leaves);
        intLeaves = Integer.parseInt(temp.toString());

        holidays = getHolidaysFromDates(fromDate,toDate);
        intLeaves = intLeaves-holidays;

        weakends = getWeekendsFromDates(fromDate,toDate);
        intLeaves = intLeaves-weakends;
        return intLeaves;

    }

    /**
     * It returns a list that contains fromDates and toDates of all 'approved' leaves for an employee
     * @param empId
     * @return
     * @throws Exception
     */
    private static LinkedList getLeaveDates(String empId) throws Exception {

        int currentYear = getCurrentYear();
        Date startDate = getStartDateOfCurrentYear(currentYear);
        Date endDate = getEndDateOfCurrentYear(currentYear);

        HashMap paramMap = new HashMap();
        paramMap.put("userId",empId );
        paramMap.put("status",LeaveStatus.APPROVED.ordinal());
        paramMap.put("startDate",startDate);
        paramMap.put("endDate",endDate);
        List<Leave> listOfApprovedLeaves = null;
        try {
            listOfApprovedLeaves = Leave.getEmpoyeeLeavesInCurrentYear(paramMap);
        } catch (Exception e) {
          //  throw new ServiceException(e);
        }

        LinkedList listOfDates = new LinkedList();
        for(Leave leave : listOfApprovedLeaves) {
            listOfDates.add(leave.getFromDate());
            listOfDates.add(leave.getToDate());
        }
        return listOfDates;
    }

    public static List<Leave> getEmpoyeeLeavesInCurrentYear(HashMap paramMap) {
        List<Leave> listOfLeaves = null;
        try {
            Date startDate = (Date)paramMap.get("startDate");
            Date endDate = (Date)paramMap.get("endDate");

            return find.where().eq("status", paramMap.get("status"))
                    .eq("employeeid", paramMap.get("userId"))
                    .conjunction()
                    .or(Expr.between("fromdate", startDate, endDate), Expr.or(Expr.eq("fromdate", startDate), Expr.eq("fromdate", endDate)))
                    .endJunction()
                    .findList();

            } catch (Exception e) {
                e.printStackTrace();
                //throw new DAOException("Database Error");
            }
        return null;
    }

    public static int getNoOfPendingLeavesForEmployee(String empId){
        int leavesTaken = 0;
        LinkedList listOfDates = null;
        try {
            listOfDates = getPendingLeaveDates(empId);
        } catch (Exception e) {
        }
        while(!listOfDates.isEmpty()) {
            Date fromDate = (Date)listOfDates.removeFirst();
            Date toDate = (Date)listOfDates.removeFirst();
            if(!fromDate.equals(toDate)) {
                int leaves = calculateLeavesTaken(fromDate,toDate);
                leavesTaken = leavesTaken +leaves;
            }else {
                leavesTaken = leavesTaken + 1;
            }
        }
        return leavesTaken;
    }

    private static LinkedList getPendingLeaveDates(String userId){

        int currentYear = getCurrentYear();
        Date startDate = getStartDateOfCurrentYear(currentYear);
        Date endDate = getEndDateOfCurrentYear(currentYear);

        HashMap paramMap = new HashMap();
        paramMap.put("userId", userId);
        paramMap.put("status","pending");
        paramMap.put("startDate",startDate);
        paramMap.put("endDate",endDate);
        List<Leave> listOfApprovedLeaves = null;
        try {
            listOfApprovedLeaves = Leave.getEmpoyeeLeavesInCurrentYear(paramMap);
        } catch (Exception e) {
           // throw new ServiceException(e);
        }

        LinkedList listOfDates = new LinkedList();
        for(int i=0;i<listOfApprovedLeaves.size();i++) {
            Leave tempObj = listOfApprovedLeaves.get(i);
            listOfDates.add(tempObj.getFromDate());
            listOfDates.add(tempObj.getToDate());
        }
        return listOfDates;
    }


    public static PagedList<Leave> searchLeaves(FilterBean filterBean)
    {
        Date dateRangeStartDate = null;
        Date dateRangeEndDate = null;
        String userId = null;

        ExpressionList<Leave> exprList = find.where();

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

        PagedList<Leave> leavesFromRaw =  exprList.findPagedList(filterBean.getCurrentPage() - 1, filterBean.getRowSize());

        for(Leave leave : leavesFromRaw.getList())
        {
            Logger.debug("leave "+leave.getFromDate()+ " "+leave.getToDate());
            leave.setLeaveDays(getDaysFromLeave(leave));
        }
        return leavesFromRaw;
    }

    public static PagedList<Leave> searchLeavesForApprover(FilterBean filterBean, String approverId)
    {
        //status = 'Pending' AND approverid=:approverId"

        Date dateRangeStartDate = null;
        Date dateRangeEndDate = null;
        String userId = null;

        ExpressionList<Leave> exprList = find.where().eq("status", LeaveStatus.PENDING.ordinal()).eq("approverid", approverId);

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

        PagedList<Leave> leavesFromRaw =  exprList.findPagedList(filterBean.getCurrentPage() - 1, filterBean.getRowSize());

        for(Leave leave : leavesFromRaw.getList())
        {
            Logger.debug("leave "+leave.getFromDate()+ " "+leave.getToDate());
            leave.setLeaveDays(getDaysFromLeave(leave));
        }
        return leavesFromRaw;
    }

    public static PagedList<Leave> getUserCancelLeavesOfCurrentYear(String userName, FilterBean filterBean)
    {
        int currentYear = Leave.getCurrentYear();
        Date startDate = Leave.getStartDateOfCurrentYear(currentYear);
        Date endDate = Leave.getEndDateOfCurrentYear(currentYear);

        ExpressionList<Leave> exprList = find.where();

        PagedList<Leave> leavesFromRaw = exprList.in("status", new Integer[]{LeaveStatus.PENDING.ordinal(), LeaveStatus.APPROVED.ordinal()})
                .eq("employeeid", userName)
                .conjunction()
                .or(Expr.between("fromdate", startDate, endDate), Expr.or(Expr.eq("fromdate", startDate), Expr.eq("fromdate", endDate)))
                .endJunction()
                .findPagedList(filterBean.getCurrentPage() - 1, filterBean.getRowSize());

        for(Leave leave : leavesFromRaw.getList())
        {
            leave.setLeaveDays(getDaysFromLeave(leave));
        }
        return leavesFromRaw;
    }


    public static boolean isLeaveNotOverlapping(String empId, Date fromDate, Date toDate)
    {
        StringBuilder sb = new StringBuilder(" SELECT count(*) as leaveCount from leaves where employeeid=:employeeId and (status in ('Pending','Approved')) " +
                "and ((fromdate between :fromDate and :toDate) or (todate between :fromDate and :toDate) or (:fromDate between fromdate and todate) " +
                "or (:toDate between fromdate and todate))");


        SqlQuery sqlQuery = Ebean.createSqlQuery(sb.toString());
        sqlQuery.setParameter("employeeId", empId)
            .setParameter("fromDate", fromDate)
            .setParameter("toDate", toDate);

        // execute the query returning a List of MapBean objects
        List<SqlRow> list = sqlQuery.findList();
        Logger.debug(list.size()+"");
        Logger.debug("From Date "+fromDate);
        Logger.debug("To Date "+toDate);
        if(list.size() > 0 && Integer.parseInt(list.get(0).get("leaveCount").toString()) > 0)
        {
            return true;
        }
        return false;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }
}
