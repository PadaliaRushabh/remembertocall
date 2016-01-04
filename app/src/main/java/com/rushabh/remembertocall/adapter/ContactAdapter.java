package com.rushabh.remembertocall.adapter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rushabh.remembertocall.MainActivity;
import com.rushabh.remembertocall.R;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.sql.SqlLiteHelper;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rushabh on 17/12/15.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CustomViewHolder> {

    ArrayList<Contact> contacts;
    private static final int NEVER_CONTACTED = -1;
    private static final int CANNOT_ASK_FOR_PERMISSION_FROM_SERVICE = -4;
    private static final int DURATION_NOT_AVAILABLE = -5 ;
    private static final int PERMISSION_FOR_DURATION_DENIED = -2;
    private static final int CANNOT_ASK_FOR_PERMISSION_ON_RUNTIME = -3;
    private static final int TODAY = 0;
    SqlLiteHelper sql;
    private int contactCount = 0;
    Context context;

    public Contact getItem(int position){
        return contacts.get(position);
    }

    public int getItemPosition(Contact contact){
        int count = 0;
        for(Contact c: contacts){
            if(isContactAlreadyAdded(c)){
                return count;
            }
            count++;
        }
        return -1;
    }

    public ContactAdapter(Context context){
        this.context = context;
    }

    public ContactAdapter(Context context , ArrayList<Contact> contacts , SqlLiteHelper sql){
        this.context = context;
        this.contacts = contacts;
        this.sql = sql;
    }


    public void viewVisibilityToggle(RecyclerView recyclerView, TextView textView) {
        contactCount = sql.getContactsCount();

        if(contactCount == 0){
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

        } else{
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_items, parent,false);

        //TODO:view.OnClickListener()

        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    public boolean addContact(Contact contact){

        if(isContactAlreadyAdded(contact) == false) {
            this.contacts.add(contact);
            sql.addContact(contact);
            return true;
        }

        return false;
    }

    public Cursor getCursorFromLookUpKey(long ID, String lookUpKey){
        Uri lookUpUri = ContactsContract.Contacts.getLookupUri(ID, lookUpKey);
        Uri contentUri = ContactsContract.Contacts.lookupContact(context.getContentResolver(), lookUpUri);

        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);

        return  cursor;
    }


    public void removeContact(int position){
        sql.deleteContact(this.contacts.get(position));

        this.contacts.remove(position);
        notifyItemRemoved(position);
    }

    public void refershContacts(){
        this.contacts.clear();
        this.contacts = (ArrayList) sql.getAllContacts();
        notifyDataSetChanged();
    }

    public boolean isContactAlreadyAdded(Contact contact){

        return  contacts.contains(contact);

    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        TextView contactName = holder.contactName;
        TextView lastCallDate = holder.lastCallDate;
        TextView lastCallSince = holder.lastCallSince;

        //setTextViewMarginTop(contactName);

        String str_lastCallSince;
        String str_dateSinceLastCall;

        DateTime currentDate = new DateTime();
        DateTimeFormatter df  =  DateTimeFormat.forPattern("dd MMM");

        switch ( Integer.parseInt(contacts.get(position).getDaySinceLastCall() + "")){
            case TODAY:
                str_lastCallSince = "Today";
                str_dateSinceLastCall = df.print(currentDate);
                break;
            case NEVER_CONTACTED:
                str_lastCallSince = "Never";
                str_dateSinceLastCall = "Never";
                break;
            default:
                str_lastCallSince = contacts.get(position).getDaySinceLastCall() + " days ago";

                currentDate = currentDate.minusDays(Integer.parseInt(contacts.get(position).getDaySinceLastCall() + ""));
                str_dateSinceLastCall = df.print(currentDate);
                break;
        }


        contactName.setText(contacts.get(position).getDisplayName());
        lastCallSince.setText(str_lastCallSince);
        lastCallDate.setText(str_dateSinceLastCall);

    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class CustomViewHolder extends  RecyclerView.ViewHolder{

        public TextView contactName, lastCallDate, lastCallSince;

        public CustomViewHolder(View itemView){
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.txt_contactname);
            lastCallDate = (TextView) itemView.findViewById(R.id.txt_lastcalldate);
            lastCallSince = (TextView) itemView.findViewById(R.id.txt_lastcallsince);

        }
    }


}
