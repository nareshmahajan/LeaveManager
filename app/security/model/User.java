package security.model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import model.CommonBean;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class User extends Model {

    @Id
    public Long id;

    private String authToken;

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    @Column(name = "approver_id")
    private String approverId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "name")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "group_id")
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Transient
    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    @JsonIgnore
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Column(name = "is_admin")
    private String isAdmin;

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Column(name = "is_approver")
    private String isApprover;

    public String getIsApprover() {
        return isApprover;
    }

    public void setIsApprover(String isApprover) {
        this.isApprover = isApprover;
    }


    @Column(name ="email_id")
    private String emailId;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }


    public String createToken() {
        authToken = UUID.randomUUID().toString();
        save();
        return authToken;
    }

    public void deleteAuthToken() {
        authToken = null;
        save();
    }

    public User() {

    }

    public User(String emailAddress, String password, String fullName) {

    }


    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Finder<Long, User> find = new Finder<Long, User>(User.class);

    public static User findByEmailAddressAndPassword(String userId, String password) {
        return find.where().eq("user_id", userId.toLowerCase()).findUnique();
    }

    public static String findApproverId(String userName) {
        User user = User.find
                .select("approver_id")
                .where().eq("user_id", userName).findUnique();
        return user.getApproverId();
    }


    public static List<CommonBean> getDepartments()
    {
        String sql = " SELECT distinct group_id as id FROM user";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .create();

        List<CommonBean> departments = Ebean.find(CommonBean.class)
                .setRawSql(rawSql)
                .findList();

        for(CommonBean bean : departments)
        {
            bean.setName(bean.getId());
        }
        return departments;
    }

    public static List<CommonBean> getDepartmentsEmployee(List<String> departmentList)
    {
        String sql = " SELECT user_id as id,name  FROM user";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .create();
        List<CommonBean> departments = Ebean.find(CommonBean.class)
                .setRawSql(rawSql)
                .where().in("group_id",departmentList.toArray())
                .findList();


        return departments;
    }

    public static List<CommonBean> getOnBehaulfUserList(String userName) {
        String sql = " SELECT user_id as id,name  FROM user";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .create();
        List<CommonBean> departments = Ebean.find(CommonBean.class)
                .setRawSql(rawSql)
                .where().eq("approver_id",userName)
                .findList();


        return departments;
    }

    /**
     * It returns an array of employee Ids that belong to given group.
     * @param groupNames
     * @return
     */
    public static List<String> getGroupMembers(String groupNames[]){
        String sql = "SELECT user_id as id  FROM user";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .create();
        List<CommonBean> usersList = Ebean.find(CommonBean.class)
                .setRawSql(rawSql)
                .where().in("group_id", groupNames)
                .findList();

        List<String> users = new ArrayList<String>();
        for(CommonBean user : usersList)
        {
            users.add(user.getId());
        }
        return users;
    }
}