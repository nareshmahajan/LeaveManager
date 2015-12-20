package security.controller;

/**
 * Created by nareshdon on 01-11-2015.
 */

import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.model.Login;
import security.model.User;
import views.html.home;
import views.html.login;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class SecurityController extends Controller {

    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "authToken";


    public static User getUser() {
        return (User)Http.Context.current().args.get("user");
    }

    // returns an authToken
    public Result login() {

/*        *//* LDAP Login *//*

        try {

            F.Promise<Boolean> promiseActiveDirectoryCheck = ActiveDirectoryServices.authenticate(userName, password);
            return promiseActiveDirectoryCheck.flatMap(response -> {

                if(response){
                    return Promise.pure(ok("access granted"));
                }


            });

        }catch (AuthenticationException exp) {
            return Promise.pure(ok("access denied"));

        }catch (CommunicationException exp) {
            return Promise.pure(ok("The active directory server is not reachable"));

        }catch (NamingException exp) {
            return Promise.pure(ok("active directory domain name does not exist"));

        }




        */






        Form<Login> loginForm = form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Login loginBean = loginForm.get();

        User user = User.findByEmailAddressAndPassword(loginBean.userId, loginBean.password);

        if (user == null) {

           // return ok(login.render(User.findApproverId(), ""));
            List<ValidationError> lst = new ArrayList<ValidationError>();
            lst.add(0, new ValidationError("password", "Credentials are Incorrect"));
            loginForm.errors().put("password", lst);

           Logger.debug(loginForm.errors().size()+"  ERRORS SIZE");
            return ok(login.render(loginForm));

        }
        else {
            User approverObj = User.find.where().eq("user_id", user.getApproverId()).findUnique();
            Logger.debug(Json.toJson(approverObj).toString());

            //String authToken = user.createToken();
           // ObjectNode authTokenJson = Json.newObject();
            //authTokenJson.put(AUTH_TOKEN, authToken);
            //response().setCookie(AUTH_TOKEN, authToken);
            session().put("userName",user.getUserId());
            session().put("approver_id", user.getApproverId());
            session().put("user_description", user.getUserName());
            session().put("user_email_id", user.getEmailId());
            session().put("isAdmin", user.getIsAdmin());
            session().put("isApprover", ((null == user.getIsApprover() ? "N" : user.getIsApprover())));
            session().put("approver_email_id", approverObj.getEmailId());
            return ok(home.render());
        }
    }

    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect("/");
    }

}