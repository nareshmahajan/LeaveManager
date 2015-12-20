package security.model;

import play.data.validation.Constraints;

/**
 * Created by nmahajan on 12/9/2015.
 */
public class Login {

    @Constraints.Required
    public String userId;

    @Constraints.Required
    public String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
