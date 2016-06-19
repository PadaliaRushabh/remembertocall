package com.rushabh.remembertocall.broadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

import java.util.Calendar;

/**
 * Created by rushabh on 24/12/15.
 */
public class ActionReceiver extends BroadcastReceiver {
    final int UPDATE_DATABASE_REQUEST_CODE = 10;
    SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED) || intent.getAction().equals("com.rushabh.remenbertocall.NOTIFICATION_TIME_CHANGE")){
            sharedPreferenceHelper = new SharedPreferenceHelper(context);

            Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, sharedPreferenceHelper.readNotificationHour());
            calendar.set(Calendar.MINUTE, sharedPreferenceHelper.readNotificationMinute());

            //Log.d("time", calendar.getTime().toString());
            if(calendar.getTimeInMillis() < System.currentTimeMillis()){
                calendar.add(Calendar.DAY_OF_YEAR , 1);
                //Log.v("time", calendar.getTime().toString());
            }

           // Log.d("action" , intent.getAction().toString());
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent updateDatabaseIntent;

            Intent updateIntent = new Intent(context, UpdateDatabaseReceiver.class);
            updateDatabaseIntent = PendingIntent.getBroadcast(context, UPDATE_DATABASE_REQUEST_CODE, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);

           alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                   AlarmManager.INTERVAL_DAY, updateDatabaseIntent);

        }
    }
}
