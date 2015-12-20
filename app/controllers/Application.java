package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.controller.Secured;
import views.html.home;
import views.html.login;


public class Application extends Controller {

    public Application() {

    }

    @Security.Authenticated(Secured.class)
    public Result homePage() {
        return ok(home.render());
    }

    public Result loginPage() {
        //Form<User> userForm = Form.form(Application.Login.class);
        return ok(login.render(null));
    }
}
