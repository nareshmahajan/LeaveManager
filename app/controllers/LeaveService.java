package controllers;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import model.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.controller.Secured;
import security.model.User;
import util.MailService;

import javax.inject.Inject;
import java.util.*;


/**
 * Created by nareshdon on 03-11-2015.
 */
public class LeaveService extends Controller {

    @Inject
    MailerClient mailerClient;

    @Security.Authenticated(Secured.class)
    public Result getUserLeavesOfCurrentYear() {

        JsonNode json = request().body().asJson();
        FilterBean filterBean = Json.fromJson(json, FilterBean.class);
        PagedList<Leave> pagedList = Leave.getUserLeavesOfCurrentYear(session().get("userName"), filterBean);
        LeaveMetaData metaData = new LeaveMetaData();
        metaData.setLeaves(pagedList.getList());
        metaData.setTotalRows(pagedList.getTotalRowCount());

        return ok(Json.toJson(metaData));
    }

    @Security.Authenticated(Secured.class)
    public Result getUserLeavesCount() {
        Map<String, Integer> leavesCountMap = new HashMap<String, Integer>();
        leavesCountMap.put("leavesAvailable", Leave.getLeavesAvailableForEmployee(session().get("userName")));
        leavesCountMap.put("leavesTaken", Leave.getLeavesTakenByEmployee(session().get("userName")));
        leavesCountMap.put("leavesPending", Leave.getNoOfPendingLeavesForEmployee(session().get("userName")));
        return ok(Json.toJson(leavesCountMap));
    }

    @Security.Authenticated(Secured.class)
    public Result applyLeave() {
        ActionResult actionResult = new ActionResult();
        actionResult.setStatus("SUCCESS");
        JsonNode json = request().body().asJson();

        Form<Leave> leaveForm = Form.form(Leave.class).bind(json);
        Logger.debug(leaveForm.data().toString());
        if (leaveForm.hasErrors()) {
            actionResult.setStatus("FAILED");
            actionResult.setErrors(leaveForm.errorsAsJson());
            return ok(Json.toJson(actionResult));
        }

        try {
            Leave leave = Json.fromJson(json, Leave.class);
            Logger.debug(leave.getFromDate().toString());
            leave.setApproverId(session().get("approver_id"));
            leave.setEmployeeId(session().get("userName"));
            leave.setLeaveType("PLANNED");
            leave.setStatus(LeaveStatus.PENDING);
            leave.setMaker(session().get("userName"));
            leave.save();

            MailService.applyLeave(mailerClient, leave, session().get("user_description"), session().get("approver_email_id"));
        } catch (Exception exc) {
            Logger.error("Apply Leave", exc);
        }
        return ok(Json.toJson(actionResult));
    }


    @Security.Authenticated(Secured.class)
    public Result cancelLeave(int leaveId) {
        Leave leave = new Leave();
        leave.setLeaveId(leaveId);
        leave.setStatus(LeaveStatus.CANCELLED);
        leave.update();
        return ok(Json.toJson(leave));
    }

    @Security.Authenticated(Secured.class)
    public Result searchLeavesForApprover() {
        JsonNode json = request().body().asJson();
        FilterBean filterBean = Json.fromJson(json, FilterBean.class);
        PagedList<Leave> pagedList = Leave.searchLeavesForApprover(filterBean, session().get("userName"));
        LeaveMetaData metaData = new LeaveMetaData();
        metaData.setLeaves(pagedList.getList());
        metaData.setTotalRows(pagedList.getTotalRowCount());
        return ok(Json.toJson(metaData));

    }


    @Security.Authenticated(Secured.class)
    public Result approveLeave(int leaveId) {
        Leave leave = new Leave();
        leave.setLeaveId(leaveId);
        leave.setStatus(LeaveStatus.APPROVED);
        leave.update();
        // Mail Code
        leave = Leave.find.where().eq("leaveid", leaveId).findUnique();
        User applierUserObj = User.find.where().eq("user_id", leave.getEmployeeId()).findUnique();
        MailService.approveLeave(mailerClient, leave, applierUserObj.getEmailId());
        return ok(Json.toJson(leave));
    }


    @Security.Authenticated(Secured.class)
    public Result rejectLeave(int leaveId) {
        Leave leave = new Leave();
        leave.setLeaveId(leaveId);
        leave.setStatus(LeaveStatus.REJECTED);
        leave.update();

        // Mail Code
        leave = Leave.find.where().eq("leaveid", leaveId).findUnique();
        User applierUserObj = User.find.where().eq("user_id", leave.getEmployeeId()).findUnique();
        MailService.rejectLeave(mailerClient, leave, applierUserObj.getEmailId());
        return ok(Json.toJson(leave));
    }

    @Security.Authenticated(Secured.class)
    public Result getUserCancelLeavesOfCurrentYear() {
        JsonNode json = request().body().asJson();
        FilterBean filterBean = Json.fromJson(json, FilterBean.class);
        PagedList<Leave> pagedList = Leave.getUserCancelLeavesOfCurrentYear(session().get("userName"), filterBean);
        LeaveMetaData metaData = new LeaveMetaData();
        metaData.setLeaves(pagedList.getList());
        metaData.setTotalRows(pagedList.getTotalRowCount());
        return ok(Json.toJson(metaData));

    }

    @Security.Authenticated(Secured.class)
    public Result getCalendarEvents() {
        JsonNode json = request().body().asJson();
        FilterBean filterBean = Json.fromJson(json, FilterBean.class);
        List<CalendarEventBean> lstLeaves = CalendarEventBean.calendarEvents(filterBean);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("eventsList", lstLeaves);
        result.put("userList", getUniqueUserList(lstLeaves));
        return ok(Json.toJson(result));
    }

    private Set<String> getUniqueUserList(List<CalendarEventBean> lstEvents){
            Map<String, String> userList = new HashMap<String, String>();
        for(CalendarEventBean eventBean : lstEvents){
            if(null == userList.get(eventBean.getTitle()))
            {
                userList.put(eventBean.getTitle(), eventBean.getTitle());
            }
        }
        Set<String> userKeyList = userList.keySet();
        return userKeyList;
    }

    @Security.Authenticated(Secured.class)
    public Result getOnBehalfUserList() {
        List<CommonBean> lstEmployees = User.getOnBehaulfUserList(session().get("userName"));
        return ok(Json.toJson(lstEmployees));
    }

    @Security.Authenticated(Secured.class)
    public Result applyOnBehalfLeave() {
        ActionResult actionResult = new ActionResult();
        actionResult.setStatus("SUCCESS");
        JsonNode json = request().body().asJson();

        Form<Leave> leaveForm = Form.form(Leave.class).bind(json);
        Logger.debug(leaveForm.data().toString());
        Leave leave = Json.fromJson(json, Leave.class);

        if (StringUtils.isEmpty(leave.getEmployeeId())) {
            List<ValidationError> lst = new ArrayList<ValidationError>();
            lst.add(0, new ValidationError("employeeId", "Employee is required"));
            if (lst.size() > 0) {
                leaveForm.errors().put("employeeId", lst);
            }
        }
        if (StringUtils.isEmpty(leave.getLeaveType())) {
            List<ValidationError> lst = new ArrayList<ValidationError>();
            lst.add(0, new ValidationError("leaveType", "Leave Type is required"));
            if (lst.size() > 0) {
                leaveForm.errors().put("leaveType", lst);
            }
        }

        if (leaveForm.hasErrors()) {
            actionResult.setStatus("FAILED");
            actionResult.setErrors(leaveForm.errorsAsJson());
            return ok(Json.toJson(actionResult));
        }
        try {

            Logger.debug(leave.getFromDate().toString());
            leave.setApproverId(session().get("userName"));
            leave.setMaker(session().get("userName"));
            leave.setStatus(LeaveStatus.APPROVED);
            leave.save();

            MailService.applyOnBehalfLeave(mailerClient, leave, session().get("user_description"), session().get("user_email_id"));
        } catch (Exception exc) {
            Logger.error("Apply Leave", exc);
        }
        return ok(Json.toJson(actionResult));
    }
}
