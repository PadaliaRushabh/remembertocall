package com.rushabh.remembertocall.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rushabh.remembertocall.R;
import com.rushabh.remembertocall.model.Contact;

import java.util.ArrayList;

/**
 * Created by rushabh on 17/12/15.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CustomViewHolder> {

    ArrayList<Contact> contacts;


    public ContactAdapter(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_items, parent,false);

        //TODO:view.OnClickListener()

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        TextView contactName = holder.contactName;
        TextView lastCallDuration = holder.lastCallDuration;
        TextView lastCallSince = holder.lastCallSince;


        contactName.setText(contacts.get(position).getDisplayName());
        lastCallSince.setText(contacts.get(position).getDaySinceLastCall() + "");
        lastCallDuration.setText(contacts.get(position).getLastCallDuration() + "");

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class CustomViewHolder extends  RecyclerView.ViewHolder{

        public TextView contactName, lastCallDuration, lastCallSince;

        public CustomViewHolder(View itemView){
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.txt_contactname);
            lastCallDuration = (TextView) itemView.findViewById(R.id.txt_lastcallduration);
            lastCallSince = (TextView) itemView.findViewById(R.id.txt_lastcallsince);

        }
    }
}
