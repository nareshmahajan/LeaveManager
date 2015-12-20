package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.ActionResult;
import model.Holiday;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.controller.Secured;

import java.util.List;

/**
 * Created by nareshdon on 03-11-2015.
 */
public class HolidayService extends Controller {
    @Security.Authenticated(Secured.class)
    public Result listHolidays() {
        List<Holiday> lstHolidays = Holiday.find.all();
        return ok(Json.toJson(lstHolidays));
    }

    @Security.Authenticated(Secured.class)
    public Result addHoliday() {
        ActionResult actionResult = new ActionResult();
        JsonNode json = request().body().asJson();
        Form<Holiday> leaveForm = Form.form(Holiday.class).bind(json);
        if (leaveForm.hasErrors()) {
            actionResult.setStatus("FAILED");
            actionResult.setErrors(leaveForm.errorsAsJson());
            return ok(Json.toJson(actionResult));
        }
        try {
            Holiday bean = Json.fromJson(json, Holiday.class);
            bean.save();
            actionResult.setStatus("SUCCESS");
        } catch (Exception exc) {
            actionResult = ActionResult.handleErrors(exc);
            Logger.error("Exception Holiday Save", exc);

        }
        return ok(Json.toJson(actionResult));
    }

    @Security.Authenticated(Secured.class)
    public Result updateHoliday() {
        ActionResult result = new ActionResult();
        JsonNode json = request().body().asJson();
        try {
            Holiday bean = Json.fromJson(json, Holiday.class);
            bean.update();
            result.setStatus("SUCCESS");
        } catch (Exception exc) {
            result = ActionResult.handleErrors(exc);
        }
        return ok(Json.toJson(result));
    }

    @Security.Authenticated(Secured.class)
    public Result removeHoliday(int HolidayId) {
        ActionResult result = new ActionResult();
        try {
            Holiday bean = new Holiday();
            bean.setHolidayId(HolidayId);
            bean.delete();
            result.setStatus("SUCCESS");
        } catch (Exception exc) {
            result = ActionResult.handleErrors(exc);
        }
        return ok(Json.toJson(result));
    }
}
