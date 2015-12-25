package com.rushabh.remembertocall.updateDatabaseServiceReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by rushabh on 24/12/15.
 */
public class ActionReceiver extends BroadcastReceiver {
    final int UPDATE_DATABASE_REQUEST_CODE = 10;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED) ){

            //Log.i("name", intent.getAction());
            // Set the alarm to start at approximately 1:00 a.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 22);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent updateDatabaseIntent;

            Intent updateIntent = new Intent(context, UpdateDatabaseReceiver.class);
            updateDatabaseIntent = PendingIntent.getBroadcast(context, UPDATE_DATABASE_REQUEST_CODE, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);

           alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, updateDatabaseIntent);

            /*alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    0,
                    1000 * 60 * 2,
                    updateDatabaseIntent);
*/        }
    }
}
