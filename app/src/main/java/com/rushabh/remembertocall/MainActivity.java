package com.rushabh.remembertocall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.rushabh.remembertocall.UpdateDatabaseService.UpdateDatabaseService;
import com.rushabh.remembertocall.adapter.ContactAdapter;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.notification.ContactNotification;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;
import com.rushabh.remembertocall.sql.SqlLiteHelper;
import com.rushabh.remembertocall.touchHelper.TouchHelper;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 2;
    private static final int NEVER_CONTACTED = -1;
    private static final int PERMISSION_FOR_DURATION_DENIED = -2;
    private static final int CANNOT_ASK_FOR_PERMISSION_ON_RUNTIME = -3;
    Uri uriContact;
    Cursor cursor;
    int id;
    String displayName;
    long daySinceLastCall;
    int lastCallDuration;
    SqlLiteHelper sql;

    RecyclerView contactListView;
    TextView noContact;
    ContactAdapter adapter;

    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sql =  new SqlLiteHelper(getApplicationContext());
        sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());

        widgetInit();

        AdapterInit();

        adapter.viewVisibilityToggle(contactListView, noContact);

        SwipeInit();

        ActionButtonInit();

        setDatabaseServiceIfFirstLaunch();

    }

    private void AdapterInit() {
        ArrayList<Contact> contacts = (ArrayList) sql.getAllContacts();
        adapter = new ContactAdapter(contacts, sql);
        contactListView.setAdapter(adapter);
    }

    private void widgetInit() {
        contactListView = (RecyclerView)findViewById(R.id.phone_list);
        contactListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactListView.setLayoutManager(layoutManager);
        contactListView.setItemAnimator(new DefaultItemAnimator());

        noContact = (TextView)findViewById(R.id.txt_nocontact);
    }

    private void SwipeInit() {
        ItemTouchHelper.Callback callback = new TouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(contactListView);
    }

    private void ActionButtonInit() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(contactListView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
    }

    private void setDatabaseServiceIfFirstLaunch() {

        if(sharedPreferenceHelper.readFirstLaunch() == true){

            sharedPreferenceHelper.writeFirstLaunch();
            Intent i= new Intent(getApplicationContext(), UpdateDatabaseService.class);
            getApplicationContext().startService(i);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {

            //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            cursor = getApplicationContext().getContentResolver().query(uriContact, null, null, null, null);

            Contact contact = getContact(cursor);

            adapter.addContact(contact);
            adapter.notifyDataSetChanged();
            adapter.viewVisibilityToggle(contactListView, noContact);

        }
    }

    @NonNull
    public Contact getContact(Cursor cursor) {
        if (cursor.moveToFirst()) {
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
            displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            daySinceLastCall = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LAST_TIME_CONTACTED)));

            //Check if contact was ever contacted
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

        return new Contact(id , displayName, daySinceLastCall,lastCallDuration);
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
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},
                        MY_PERMISSIONS_REQUEST_CALL_CONTACTS);
            } else{
                return CANNOT_ASK_FOR_PERMISSION_ON_RUNTIME;
            }

        }


        String selection = CallLog.Calls.CACHED_NAME + "=?";
        String[] args = {displayName};

        Cursor cur = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, args, null);
        if(cur.moveToFirst()){
            String duration = cur.getString(cur.getColumnIndexOrThrow(CallLog.Calls.DURATION));
            return Integer.parseInt(duration);
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_CONTACTS:
                getDuration(displayName);
                break;
            default:
                lastCallDuration = PERMISSION_FOR_DURATION_DENIED;
        }
    }
}
