package com.rushabh.remembertocall.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rushabh.remembertocall.service.UpdateDatabaseOnCallService;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

/**
 * Created by rushabh on 26/12/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    String incomingNumber;
    String KEY_NUMBER = "key_number";
    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        Log.v("receiver" , this.getClass().toString());

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // This code will execute when the phone has an incoming call

            // get the phone number

            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            sharedPreferenceHelper.writeIncomingCallNumber(incomingNumber);
            Log.v("number-1" , incomingNumber);


        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            // use this to start and trigger a service
            Intent i= new Intent(context, UpdateDatabaseOnCallService.class);
            Log.v("number-2", sharedPreferenceHelper.readIncomingCallNumber());
            // potentially add data to the intent
            //i.putExtra(context.getResources().getString(R.string.key_number), sharedPreferenceHelper.readIncomingCallNumber());
            context.startService(i);


            // This code will execute when the call is disconnected


        }

    }
}
