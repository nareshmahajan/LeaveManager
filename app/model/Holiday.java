package model;

/**
 * Created by nareshdon on 03-11-2015.
 */
import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

@Entity
@Table(name="holidays")
public class Holiday  extends Model {

    @Id
    @Column(name="holidayid")
    private int holidayId;

    @Column(name="holidaydate")
    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Constraints.Required(message = "Please Provide the Date")
    private Date holidayDate;

    @Constraints.Required(message = "Please Provide the Reason")
    private String reason;

    public Holiday() {

    }
    public Holiday(Date holidayDate,String reason) {
        this.holidayDate = holidayDate;
        this.reason = reason;
    }


    public int getHolidayId() {
        return holidayId;
    }
    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }
    public Date getHolidayDate() {
        return holidayDate;
    }


    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }


    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public static Finder<Long, Holiday> find = new Finder<Long,Holiday>(Holiday.class);

    /**
     * It returns count of holidays between fromDate and toDate
     * @return
     */
    public static int getHolidayCount(Date fromDate,Date toDate) {
       return find.where().between("holidayDate",fromDate, toDate).findRowCount();
    }
}