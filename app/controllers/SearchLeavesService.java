package controllers;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import model.CommonBean;
import model.FilterBean;
import model.Leave;
import model.LeaveMetaData;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.controller.Secured;
import security.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




/**
 * Created by nareshdon on 28-11-2015.
 */
public class SearchLeavesService extends Controller {

    @Security.Authenticated(Secured.class)
    public Result getDepartments() {
        List<CommonBean> lstDepartments = User.getDepartments();
        return ok(Json.toJson(lstDepartments));
    }

    @Security.Authenticated(Secured.class)
    public Result getDepartmentEmployees() {
        JsonNode json = request().body().asJson();
        ArrayNode results = (ArrayNode) json;
        Iterator it = results.iterator();
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            JsonNode node = (JsonNode) it.next();
            list.add(node.textValue());

        }
        List<CommonBean> lstEmployees = User.getDepartmentsEmployee(list);
        return ok(Json.toJson(lstEmployees));
    }

    @Security.Authenticated(Secured.class)
    public Result searchLeaves() {
        JsonNode json = request().body().asJson();
        FilterBean filterBean = Json.fromJson(json, FilterBean.class);
        PagedList<Leave> pagedList = Leave.searchLeaves(filterBean);
        LeaveMetaData metaData = new LeaveMetaData();
        metaData.setLeaves(pagedList.getList());
        metaData.setTotalRows(pagedList.getTotalRowCount());
        return ok(Json.toJson(metaData));
    }
}