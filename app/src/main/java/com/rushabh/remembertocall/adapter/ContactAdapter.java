package com.rushabh.remembertocall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rushabh.remembertocall.MainActivity;
import com.rushabh.remembertocall.R;
import com.rushabh.remembertocall.model.Contact;
import com.rushabh.remembertocall.sql.SqlLiteHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

/**
 * Created by rushabh on 17/12/15.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CustomViewHolder> {

    ArrayList<Contact> contacts;
    private static final int NEVER_CONTACTED = -1;
    private static final int PERMISSION_FOR_DURATION_DENIED = -2;
    private static final int CANNOT_ASK_FOR_PERMISSION_ON_RUNTIME = -3;
    private static final int TODAY = 0;
    SqlLiteHelper sql;
    private int contactCount = 0;


    public Contact getItem(int position){
        return contacts.get(position);
    }

    public ContactAdapter(ArrayList<Contact> contacts , SqlLiteHelper sql){
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

    public void  addContact(Contact contact){
        this.contacts.add(contact);
        sql.addContact(contact);
    }

    public void removeContact(int position){
        sql.deleteContact(this.contacts.get(position));

        this.contacts.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        TextView contactName = holder.contactName;
        TextView lastCallDate = holder.lastCallDate;
        TextView lastCallSince = holder.lastCallSince;

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
