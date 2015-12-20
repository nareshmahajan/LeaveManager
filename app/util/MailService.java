package util;

import model.Leave;
import play.Logger;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmahajan on 12/9/2015.
 */
public class MailService {

    private static String VIEW_DATE_FORMAT = "MMM dd, yyyy";
    private static String MAIL_FROM_ID = "naresh_mahajan@omniscient.co.in";

    public static void applyLeave(MailerClient mailerClient, Leave leave, String userDescription, String approverMailId) {
        Logger.debug("In Apply Leave : Sending Mail");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(VIEW_DATE_FORMAT);
        try {
            Email email = new Email();
            email.setSubject(Messages.get("mail.leave.apply.subject", leave.getEmployeeId(), leave.getFromDate(), leave.getToDate()));
            email.setFrom(MAIL_FROM_ID);
            email.addTo(approverMailId);
            // adds attachment
            Map<String, String> model = new HashMap<String, String>();
            model.put("userDescription", userDescription);
            model.put("leaveReason", leave.getLeaveReason());
            model.put("fromDate", dateFormatter.format(leave.getFromDate()));
            model.put("toDate", dateFormatter.format(leave.getToDate()));
            String body = views.html.templates.applyLeave.render(model).body();
            //mail.sendHtml(body);

            //email.setBodyText("Leave Applied");
            email.setBodyHtml(body);

            mailerClient.send(email);
           Logger.debug("Mail Sent");
        }
        catch(Exception exc){
            Logger.error("Mail Send Exception", exc);
        }

    }

    public static void approveLeave(MailerClient mailerClient, Leave leave, String applierEmailId) {
        Logger.debug("In Approve Leave : Sending Mail");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(VIEW_DATE_FORMAT);
        try {
            Email email = new Email();
            email.setSubject(Messages.get("mail.leave.approve.subject", dateFormatter.format(leave.getFromDate()),
                    dateFormatter.format(leave.getToDate())));
            email.setFrom(MAIL_FROM_ID);
            email.addTo(applierEmailId);

            Map<String, String> model = new HashMap<String, String>();
           // model.put("userDescription", "");
            model.put("leaveReason", leave.getLeaveReason());
            model.put("fromDate", dateFormatter.format(leave.getFromDate()));
            model.put("toDate", dateFormatter.format(leave.getToDate()));
            String body = views.html.templates.approveLeave.render(model).body();
            //mail.sendHtml(body);

            //email.setBodyText("Leave Applied");
            email.setBodyHtml(body);

            mailerClient.send(email);
            Logger.debug("Mail Sent Successfully");
        }
        catch(Exception exc){
            Logger.error("Mail Send Exception", exc);
        }

    }

    public static void rejectLeave(MailerClient mailerClient, Leave leave, String applierEmailId) {
        Logger.debug("In Reject Leave : Sending Mail");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(VIEW_DATE_FORMAT);
        try {
            Email email = new Email();
            email.setSubject(Messages.get("mail.leave.reject.subject", dateFormatter.format(leave.getFromDate()),
                    dateFormatter.format(leave.getToDate())));
            email.setFrom(MAIL_FROM_ID);
            email.addTo(applierEmailId);

            Map<String, String> model = new HashMap<String, String>();
            // model.put("userDescription", "");
            model.put("leaveReason", leave.getLeaveReason());
            model.put("fromDate", dateFormatter.format(leave.getFromDate()));
            model.put("toDate", dateFormatter.format(leave.getToDate()));
            model.put("rejectReason", "Pending Work");
            String body = views.html.templates.rejectLeave.render(model).body();
            //mail.sendHtml(body);

            //email.setBodyText("Leave Applied");
            email.setBodyHtml(body);

            mailerClient.send(email);
            Logger.debug("Mail Sent Successfully");
        }
        catch(Exception exc){
            Logger.error("Mail Send Exception", exc);
        }

    }


    public static void applyOnBehalfLeave(MailerClient mailerClient, Leave leave, String userDescription, String approverMailId) {
        Logger.debug("In Apply Leave : Sending Mail");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(VIEW_DATE_FORMAT);
        try {
            Email email = new Email();
            email.setSubject(Messages.get("mail.leave.apply.subject", leave.getEmployeeId(), leave.getFromDate(), leave.getToDate()));
            email.setFrom(MAIL_FROM_ID);
            email.addTo(approverMailId);
            // adds attachment
            Map<String, String> model = new HashMap<String, String>();
            model.put("userDescription", userDescription);
            model.put("leaveReason", leave.getLeaveReason());
            model.put("fromDate", dateFormatter.format(leave.getFromDate()));
            model.put("toDate", dateFormatter.format(leave.getToDate()));
            String body = views.html.templates.applyLeave.render(model).body();
            //mail.sendHtml(body);

            //email.setBodyText("Leave Applied");
            email.setBodyHtml(body);

            mailerClient.send(email);
            Logger.debug("Mail Sent");
        }
        catch(Exception exc){
            Logger.error("Mail Send Exception", exc);
        }

    }
}
