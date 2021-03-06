package com.rushabh.remembertocall;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.rushabh.remembertocall.service.UpdateDatabaseService;
import com.rushabh.remembertocall.adapter.ContactAdapter;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;
import com.rushabh.remembertocall.sql.SqlLiteHelper;
import com.rushabh.remembertocall.recyclerViewTouchHelper.ItemClickListener;
import com.rushabh.remembertocall.recyclerViewTouchHelper.SwipeHelper;

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
    private static final int REQUEST_READ_CONTACT = 20;
    private static final int REQUEST_PHONE = 21;
    private static final int DEFAULT_REMINDER = 15 ;
    private static final int DEFAULT_HOUR = 1 ; //1 a.m
    private static final int DEFAULT_MINUTE = 00 ;
    Uri uriContact;
    Cursor cursor;
    int id;
    int contactClickedPosition = -1;
    String displayName, lookUpKey;
    long daySinceLastCall;
    int lastCallDuration;
    SqlLiteHelper sql;

    RecyclerView contactListView;
    TextView noContact;
    ContactAdapter adapter;

    SharedPreferenceHelper sharedPreferenceHelper;
    ArrayList<Contact> contacts;

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

        setUpContactListTouch();

        ActionButtonInit();

        setDatabaseServiceIfFirstLaunch();

        requestPhonePermission();

    }

    private void setUpContactListTouch() {
        contactListView.addOnItemTouchListener(new ItemClickListener(getApplicationContext(), new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                contactClickedPosition = position;
                //Get Look_UP URI
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        if (contactClickedPosition != -1) {
                            openClickedContact(contactClickedPosition);
                        }
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                            Toast.makeText(getApplicationContext(), "Read Contact permission is needed so that you can directly click on the contact and call" , Toast.LENGTH_SHORT).show();
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                    }
                }else{
                    openClickedContact(contactClickedPosition);
                }

            }
        }));


    }

    private void openClickedContact(int position) {
        Cursor cursor_lookup = adapter.getCursorFromLookUpKey(adapter.getItem(position).getID(), adapter.getItem(position).getLookUpKey());
        Contact c = null;
        try {
            c = getContact(cursor_lookup);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, c.getID());

        Intent intent = new Intent(Intent.ACTION_VIEW, contactUri);
        startActivity(intent);
        contactClickedPosition = -1;
    }

    private void requestPhonePermission(){
        //Get Look_UP URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                    Toast.makeText(getApplicationContext(), "Permission needed to keep track of days since last call" , Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE);
            }
        }
    }

    private void AdapterInit() {
        contacts = (ArrayList) sql.getAllContacts();
        adapter = new ContactAdapter(getApplicationContext() , contacts, sql);
        contactListView.setAdapter(adapter);
    }

    private void updateAdapter(){
        adapter.refershContacts();
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
        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(contactListView);
    }

    private void ActionButtonInit() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.attachToRecyclerView(contactListView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
    }

    private void setDatabaseServiceIfFirstLaunch() {

        if(sharedPreferenceHelper.readFirstLaunch() == true){

            sharedPreferenceHelper.writeIsReminderNotification(true);
            sharedPreferenceHelper.writeReminder(DEFAULT_REMINDER);
            sharedPreferenceHelper.writeNotificationHour(DEFAULT_HOUR);
            sharedPreferenceHelper.writeNotificationMinute(DEFAULT_MINUTE);
            sharedPreferenceHelper.writeCallNumber("0");
            sharedPreferenceHelper.writeIsRefreshRequired(false);

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

            Intent intent = new Intent(this , SettingActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_about){

            Intent intent = new Intent(this , AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            cursor = getApplicationContext().getContentResolver().query(uriContact, null, null, null, null);

            try {
                Contact contact = getContact(cursor);

                boolean contactAddedSuccess = adapter.addContact(contact);
                if (contactAddedSuccess) {
                    adapter.notifyDataSetChanged();
                    adapter.viewVisibilityToggle(contactListView, noContact);
                } else {
                    Toast.makeText(getApplicationContext(), contact.getDisplayName() + " is already added", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

        }
    }

    @NonNull
    public Contact getContact(Cursor cursor) throws Exception {
        if (cursor.moveToFirst()) {
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
            lookUpKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
            displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            daySinceLastCall = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LAST_TIME_CONTACTED)));
            int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if(hasPhone == 0){
                throw  new Exception("Contact " + displayName + " has No Phone Number");
            }

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

        return new Contact(id ,lookUpKey, displayName, daySinceLastCall,lastCallDuration);
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


        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_CONTACTS:
                getDuration(displayName);
                break;
            case REQUEST_READ_CONTACT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openClickedContact(contactClickedPosition);
                }else{
                    Toast.makeText(getApplicationContext(), "Read Contact Permission was not granted", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                lastCallDuration = PERMISSION_FOR_DURATION_DENIED;
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sharedPreferenceHelper.readIsRefreshRequired()) {
            sharedPreferenceHelper.writeIsRefreshRequired(false);
            updateAdapter();
        }

    }
}
