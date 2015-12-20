package security.controller;

/**
 * Created by nareshdon on 01-11-2015.
 */


import controllers.Assets;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import java.io.FileInputStream;
import java.io.InputStream;


public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        /*User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(SecurityController.AUTH_TOKEN);
        System.out.println("AUTHHH " + authTokenHeaderValues[0]);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = User.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return user.getUserId();
            }
        }
        */
        return ctx.session().get("userName");
    }

    @Override
    public Result onUnauthorized(Context ctx)
    {

        Assets assets = new Assets(null);
       // assets.at("public", "javascripts/jquery.js", true).apply();
        if(null == ctx.session()) {
           // controllers.Assets.Asset.string2Asset()


            return status(401, "Session Expired");
        }
        else{

            InputStream sis = null;
            try {
                sis = new FileInputStream("public/partials/sessionexpired.html");
            }
            catch(Exception exc){
                Logger.debug("FIle IO", exc);
            }
            return status(401,  sis);
        }
    }
}