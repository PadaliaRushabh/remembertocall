package com.rushabh.remembertocall.model;

/**
 * Created by rushabh on 17/12/15.
 */
public class Contact {

    private  long ID;
    private String displayName;
    private long daySinceLastCall;
    private int lastCallDuration;

    public Contact(long ID ,String displayName, long daySinceLastCall, int lastCallDuration){
        this.setID(ID);
        this.setDisplayName(displayName);
        this.setDaySinceLastCall(daySinceLastCall);
        this.setLastCallDuration(lastCallDuration);
    }

    public Contact(){

    }


    public String getDisplayName() {
        return displayName;
    }

    public long getDaySinceLastCall() {
        return daySinceLastCall;
    }

    public int getLastCallDuration() {
        return lastCallDuration;
    }


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDaySinceLastCall(long daySinceLastCall) {
        this.daySinceLastCall = daySinceLastCall;
    }

    public void setLastCallDuration(int lastCallDuration) {
        this.lastCallDuration = lastCallDuration;
    }
}
