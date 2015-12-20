package controllers;

/**
 * Created by nareshdon on 29-11-2015.
 */

import play.http.HttpErrorHandler;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;


public class ErrorHandler implements HttpErrorHandler {
    public Promise<Result> onClientError(RequestHeader request, int statusCode, String message) {

        return Promise.<Result>pure(
                Results.status(statusCode, "A client error occurred: " + message)
        );
    }

    public Promise<Result> onServerError(RequestHeader request, Throwable exception) {
        return Promise.<Result>pure(
                Results.internalServerError("A server error occurred: " + exception.getMessage())
        );
    }
}