package com.rushabh.remembertocall.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rushabh.remembertocall.R;
import com.rushabh.remembertocall.adapter.ContactAdapter;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;
import com.rushabh.remembertocall.sql.SqlLiteHelper;

import java.util.ArrayList;

/**
 * Created by rushabh on 27/12/15.
 */
public class UpdateDatabaseOnCallService extends Service {


    private SharedPreferenceHelper sharedPreferenceHelper;
    private SqlLiteHelper sql;
    private ArrayList contacts;
    private ContactAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
       
        sql = new SqlLiteHelper(getApplicationContext());
        contacts = (ArrayList) sql.getAllContacts();

        sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());

        adapter = new ContactAdapter(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Contact c;
        String phoneNumber = sharedPreferenceHelper.readCallNumber();

        if (!phoneNumber.equals("0")) {
            sharedPreferenceHelper.writeCallNumber("0");

            ContentResolver cr = getApplicationContext().getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri, null, ContactsContract.PhoneLookup.DISPLAY_NAME + "= ?", new String[]{phoneNumber}, null);

            if (cursor != null) {
                ServiceHelper s = new ServiceHelper(getApplicationContext());
                c = s.getContact(cursor);
                Log.d("c" , c.toString());
                if(c.getDisplayName() != null && c.getID() != 0) {
                    adapter = new ContactAdapter(getApplicationContext(), contacts, sql);
                    if (adapter.isContactAlreadyAdded(c)) {
                        sql.updateContact(c);
                        sharedPreferenceHelper.writeIsRefreshRequired(true);

                    }
                }
            }

        }
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
