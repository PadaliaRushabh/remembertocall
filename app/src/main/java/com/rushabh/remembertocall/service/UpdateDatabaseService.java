package com.rushabh.remembertocall.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.rushabh.remembertocall.adapter.ContactAdapter;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.notification.ContactNotification;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;
import com.rushabh.remembertocall.sql.SqlLiteHelper;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rushabh on 24/12/15.
 */
public class UpdateDatabaseService extends Service{


    private static final String TAG = "UpdateDatabaseService";
    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 2;
    private static final int CANNOT_ASK_FOR_PERMISSION_FROM_SERVICE = -4;
    private static final int NEVER_CONTACTED = -1;
    private static final int DURATION_NOT_AVAILABLE = -5 ;

    private boolean isRunning  = false;
    SqlLiteHelper sql;
    ArrayList<Contact> contacts;
    ArrayList<Contact> updateContacts;
    Cursor cursor;
    private int id;
    private String displayName;
    private long daySinceLastCall;
    private int lastCallDuration;
    private ContactAdapter adapter;

    boolean callNotification = false;
    SharedPreferenceHelper sharedPreferenceHelper;
    int reminderDays;
    int contactToCall = 0;

    Thread updateThread;
    Runnable run;

    ContactNotification contactNotification;
    private String lookUpKey;


    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        sql = new SqlLiteHelper(getApplicationContext());
        contacts = (ArrayList) sql.getAllContacts();
        updateContacts = new ArrayList<Contact>();

        sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
        reminderDays = sharedPreferenceHelper.readReminder();
        adapter = new ContactAdapter(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateContacts.clear();


            run = new Runnable() {
                @Override
                public void run() {

                    for (Contact contact : contacts) {

                        try {


                            long ID = contact.getID();
                            String LookupKey = contact.getLookUpKey();
                            Cursor cursor = adapter.getCursorFromLookUpKey(ID, LookupKey);
                            Contact lookup_contact = getContact(cursor);

                            cursor = getApplicationContext().getContentResolver().query(
                                    ContactsContract.Contacts.CONTENT_URI,
                                    null,
                                    ContactsContract.Contacts._ID + " = ?",
                                    new String[]{lookup_contact.getID() + ""}, null);

                            Contact c = getContact(cursor);

                            int i = sql.updateContact(c);
                            updateContactToCallVariable();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                }
            };

            updateThread = new Thread(run);
            updateThread.start();
            try {
                updateThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sharedPreferenceHelper.writeIsRefreshRequired(true);

            if (callNotification) {

                contactNotification = new ContactNotification(getApplicationContext(), contactToCall);
                contactNotification.sendNotification();

                callNotification = false;

            }
        stopSelf();

        return  START_STICKY;
    }

    @NonNull
    public Contact getContact(Cursor cursor) {
        if (cursor.moveToFirst()) {
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
            lookUpKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
            displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            daySinceLastCall = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LAST_TIME_CONTACTED)));

            if (daySinceLastCall != 0) {
                DateTime endDate = new DateTime();
                DateTime startDate = DateTime.parse(getDate(daySinceLastCall), DateTimeFormat.forPattern("dd/MM/yyyy"));
                daySinceLastCall = Days.daysBetween(startDate, endDate).getDays();

                lastCallDuration = getDuration(displayName);
            } else {
                daySinceLastCall = NEVER_CONTACTED;
                lastCallDuration = NEVER_CONTACTED;
            }

        }

        //Log.v("sharedpref", sharedPreferenceHelper.readIsReminderNotification()+"");


        return new Contact(id, lookUpKey,displayName, daySinceLastCall,lastCallDuration);
    }

    private void updateContactToCallVariable() {
        if(sharedPreferenceHelper.readIsReminderNotification() && (daySinceLastCall >= reminderDays || daySinceLastCall == NEVER_CONTACTED)){
            contactToCall++;
            callNotification = true;
            //Log.v("notification", contactToCall + "");
        }
    }

    private String getDate(long milliSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private int getDuration(String displayName){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return CANNOT_ASK_FOR_PERMISSION_FROM_SERVICE;
            }

        }


        String selection = CallLog.Calls.CACHED_NAME + "=?";
        String[] args = {displayName};

        Cursor cur = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, args, null);
        if(cur.moveToFirst()) {
            String duration = cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION));
            if(duration.equals("-1")){
                return DURATION_NOT_AVAILABLE;
            }
            //Log.v("Duration" , duration);
            return Integer.parseInt(duration);
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("db 1", "destroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
