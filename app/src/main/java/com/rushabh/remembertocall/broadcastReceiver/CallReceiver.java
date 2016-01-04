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

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:

                        sharedPreferenceHelper.writeCallNumber(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:

                        //if outgoing call then it will be 0
                        if(sharedPreferenceHelper.readCallNumber().equals("0")){
                            sharedPreferenceHelper.writeCallNumber(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                        }

                        Intent i= new Intent(context, UpdateDatabaseOnCallService.class);
                        context.startService(i);

                        break;
                    default:
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

    }
}
