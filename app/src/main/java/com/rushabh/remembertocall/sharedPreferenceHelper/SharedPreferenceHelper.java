package com.rushabh.remembertocall.sharedPreferenceHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.rushabh.remembertocall.R;

/**
 * Created by rushabh on 24/12/15.
 */
public class SharedPreferenceHelper {

    private static final int DEFAULT_REMINDER_DAYS = 15 ;
    SharedPreferences sharedPreferences;
    Context context;
    String preference_string_name ;


    public SharedPreferenceHelper(Context context){

        preference_string_name = context.getString(R.string.preference_file_name);
        sharedPreferences = context.getSharedPreferences(preference_string_name, Context.MODE_PRIVATE);
        this.context = context;
    }

    public void writeFirstLaunch(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_key) , false);
        editor.commit();
    }

    public boolean readFirstLaunch(){
        boolean isFirstLaunch = sharedPreferences.getBoolean(context.getString(R.string.preference_key), true);
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
}
