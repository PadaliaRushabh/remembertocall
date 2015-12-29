package com.rushabh.remembertocall.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rushabh.remembertocall.service.UpdateDatabaseOnCallService;
import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

/**
 * Created by rushabh on 26/12/15.
 */
public class CallReceiver extends BroadcastReceiver {
    String phoneNumber;
    String KEY_NUMBER = "key_number";
    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);

    /*    sharedPreferenceHelper = new SharedPreferenceHelper(context);
        Log.v("receiver" , this.getClass().toString());


        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // This code will execute when the phone has an incoming call

            // get the phone number

            //phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            //sharedPreferenceHelper.writeCallNumber(phoneNumber);
            phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.v("number-1" , phoneNumber);



        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            Log.v("number2" , phoneNumber);

            // use this to start and trigger a service
            Intent i= new Intent(context, UpdateDatabaseOnCallService.class);
            //Log.v("number-2", sharedPreferenceHelper.readCallNumber());
            // potentially add data to the intent
            i.putExtra(context.getResources().getString(R.string.key_number), phoneNumber);
            context.startService(i);


            // This code will execute when the call is disconnected


        }*/
/*        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
            Log.v("state" , "ringing");
            //Log.v("number" , intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

        } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){

            *//*Intent i= new Intent(context, UpdateDatabaseOnCallService.class);
            i.putExtra(context.getResources().getString(R.string.key_number), phoneNumber);
            context.startService(i);*//*

            Log.v("state" , "idle");
            Log.v("number" , intent.getStringExtra(intent.EXTRA_PHONE_NUMBER));

        }else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            Log.v("state" , "offhook");
            //Log.v("number" , intent.getStringExtra(intent.EXTRA_PHONE_NUMBER));


        }*/
       // Log.d("CALL_STATE_IDLE_2", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:
                        //Log.d("RINGING", incomingNumber);
                        sharedPreferenceHelper.writeCallNumber(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //Log.d("OFFHOOK", incomingNumber);

                        //Log.d("OFFHOOK", "OFFHOOK");
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:

                        //if outgoing call then it will be 0
                        if(sharedPreferenceHelper.readCallNumber().equals("0")){
                            sharedPreferenceHelper.writeCallNumber(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                        }
                        Log.d("CALL_STATE_IDLE", sharedPreferenceHelper.readCallNumber());
                        //Log.d("CALL_STATE_IDLE_2", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                        Log.d("CALL_STATE_IDLE","CALL_STATE_IDLE" );

                        Intent i= new Intent(context, UpdateDatabaseOnCallService.class);
                        context.startService(i);

                        break;
                    default:
                        Log.d("OFFHOOK", "Default: " + state);
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

    }
}
