package com.rushabh.remembertocall.model;

import android.util.Log;

/**
 * Created by rushabh on 17/12/15.
 */
public class Contact {

    private  long ID;
    private String displayName;
    private long daySinceLastCall;
    private int lastCallDuration;
    private  String lookUpKey;

    public Contact(long ID , String lookUpKey ,String displayName, long daySinceLastCall, int lastCallDuration){
        this.setID(ID);
        this.setLookUpKey(lookUpKey);
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

    @Override
    public boolean equals(Object object) {

        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Contact contact = (Contact) object;
            if (this.getDisplayName().equals(contact.getDisplayName()) && this.getID() == contact.getID()) {
                result = true;
            }
        }

        return result;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }
}
