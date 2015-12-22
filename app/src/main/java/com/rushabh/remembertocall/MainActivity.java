package com.rushabh.remembertocall;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rushabh.remembertocall.adapter.ContactAdapter;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.sql.SqlLiteHelper;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 2;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sql =  new SqlLiteHelper(getApplicationContext());
        int contactCount = sql.getContactsCount();


        contactListView = (RecyclerView)findViewById(R.id.phone_list);
        contactListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactListView.setLayoutManager(layoutManager);
        contactListView.setItemAnimator(new DefaultItemAnimator());

        noContact = (TextView)findViewById(R.id.txt_nocontact);

        if(contactCount == 0){
            contactListView.setVisibility(View.GONE);
            noContact.setVisibility(View.VISIBLE);

        } else{
            contactListView.setVisibility(View.VISIBLE);
            noContact.setVisibility(View.GONE);

            ArrayList<Contact> contacts = (ArrayList) sql.getAllContacts();

            adapter = new ContactAdapter(contacts);
            contactListView.setAdapter(adapter);

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
        if (id == R.id.action_add_contact) {

            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
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

            if (cursor.moveToFirst()) {
                id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                daySinceLastCall = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LAST_TIME_CONTACTED)));

                if (daySinceLastCall != 0) {
                    DateTime endDate = new DateTime();
                    DateTime startDate = DateTime.parse(getDate(daySinceLastCall), DateTimeFormat.forPattern("dd/MM/yyyy"));
                    daySinceLastCall = Days.daysBetween(startDate, endDate).getDays();

                    lastCallDuration = getDuration(displayName);
                } else {
                    daySinceLastCall = 0;
                    lastCallDuration = 0;
                }

            }

            Contact contact = new Contact(id , displayName, daySinceLastCall,lastCallDuration);

            sql.addContact(contact);

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
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},
                        MY_PERMISSIONS_REQUEST_CALL_CONTACTS);
            } else{
                return -1;
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
                lastCallDuration = -1;
        }
    }
}
