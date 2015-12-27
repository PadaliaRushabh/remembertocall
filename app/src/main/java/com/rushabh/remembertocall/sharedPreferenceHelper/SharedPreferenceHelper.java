package com.rushabh.remembertocall.sharedPreferenceHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.rushabh.remembertocall.R;

/**
 * Created by rushabh on 24/12/15.
 */
public class SharedPreferenceHelper {

    private static final int DEFAULT_REMINDER_DAYS = 15 ;
    private static final int DEFAULT_REMINDER_HOURS = 1 ; //1 a.m
    private static final int DEFAULT_REMINDER_MINUTE = 00;
    private static final String DEFAULT_INCOMING_NUMBER = "0" ;
    SharedPreferences sharedPreferences;
    Context context;

    public SharedPreferenceHelper(Context context){

        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        this.context = context;
    }

    public void writeFirstLaunch(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_key_firstlaunch) , false);
        editor.commit();
    }

    public boolean readFirstLaunch(){
        boolean isFirstLaunch = sharedPreferences.getBoolean(context.getString(R.string.preference_key_firstlaunch), true);
        return  isFirstLaunch;
    }


    public void writeReminder(int reminder){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_key_reminder), reminder);
        editor.commit();
    }

    public int readReminder(){
        int reminderDays = sharedPreferences.getInt(context.getString(R.string.preference_key_reminder), DEFAULT_REMINDER_DAYS);
        return  reminderDays;
    }


    public void writeNotificationHour(int hours){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_key_hour), hours);
        editor.commit();
    }

    public int readNotificationHour(){
        int notificationHours = sharedPreferences.getInt(context.getString(R.string.preference_key_hour), DEFAULT_REMINDER_HOURS);
        return  notificationHours;
    }

    public void writeNotificationMinute(int minute){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.preference_key_minute), minute);
        editor.commit();
    }

    public int readNotificationMinute(){
        int notificationMinute = sharedPreferences.getInt(context.getString(R.string.preference_key_minute), DEFAULT_REMINDER_MINUTE);
        return  notificationMinute;
    }


    public void writeIsReminderNotification(boolean isEnabled){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_key_reminder_is_enabled), isEnabled);
        editor.commit();
    }

    public boolean readIsReminderNotification(){
        boolean isEnabled = sharedPreferences.getBoolean(context.getString(R.string.preference_key_reminder_is_enabled), true);
        return  isEnabled;
    }

    public void writeCallNumber(String number){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.preference_key_incomming_call_number), number);
        editor.commit();
    }

    public String readCallNumber(){
        String number = sharedPreferences.getString(context.getString(R.string.preference_key_incomming_call_number), DEFAULT_INCOMING_NUMBER);
        return  number;
    }


}
