package com.rushabh.remembertocall.notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.rushabh.remembertocall.MainActivity;
import com.rushabh.remembertocall.R;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

/**
 * Created by rushabh on 24/12/15.
 */
public class ContactNotification {
    private final int contactToCall;
    Context context;
    SharedPreferenceHelper sharedPreferenceHelper;
    final int MID = 100;

    public ContactNotification(Context context , int contactToCall){
        this.context = context;
        this.contactToCall = contactToCall;
        sharedPreferenceHelper =  new SharedPreferenceHelper(context);
        sendNotification();

    }

    public void sendNotification(){

        String text_str = "You haven't called " + this.contactToCall + " contacts in more than " + sharedPreferenceHelper.readReminder() + " Days";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text_str)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        Intent intent = new Intent(context , MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0 , PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MID, mBuilder.build());

    }
}
