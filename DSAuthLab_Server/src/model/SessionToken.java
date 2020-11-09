package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class SessionToken implements Serializable {
	
    private String uuid;
    private Date expirationTime;
    private static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

    public SessionToken() {
        this.uuid = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        this.expirationTime = new Date(t + (10 * ONE_MINUTE_IN_MILLIS));
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String UUID) {
        this.uuid = UUID;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SessionToken){
            return uuid.equals(((SessionToken) obj).getUUID());
        }
        else if(obj instanceof String){
            return uuid.equals(obj);
        }
        return false;
    }
}
