package model;

import com.avaje.ebean.annotation.EnumMapping;

/**
 * Created by nareshdon on 14-12-2015.
 */
@EnumMapping(nameValuePairs="PENDING=0, APPROVED=1,CANCELLED=2, REJECTED=3")
public enum LeaveStatus {
    PENDING,
    APPROVED,
    CANCELLED,
    REJECTED
}
