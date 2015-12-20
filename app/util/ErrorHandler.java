package util;

/**
 * Created by nareshdon on 12-12-2015.
 */

import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import views.html.error_404;
import views.html.error_500;
import views.html.notFoundPage;

import javax.inject.Inject;
import javax.inject.Provider;

public class ErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    protected Promise<Result> onProdServerError(RequestHeader request, UsefulException exception) {
        return Promise.<Result>pure(Results.ok(notFoundPage.render()));

    }

    protected Promise<Result> onDevServerError(RequestHeader var1, UsefulException var2) {
        return Promise.pure(Results.status(500,error_500.render()));
    }

    protected Promise<Result> onForbidden(RequestHeader request, String message) {
        return Promise.<Result>pure(Results.ok(notFoundPage.render())
        );
    }


    protected F.Promise<Result> onNotFound(RequestHeader request, String message){
        return Promise.pure(Results.status(404, error_404.render()));
    }
}
