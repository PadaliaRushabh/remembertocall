package com.rushabh.remembertocall.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rushabh.remembertocall.service.UpdateDatabaseService;

/**
 * Created by rushabh on 24/12/15.
 */
public class UpdateDatabaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("receiver", this.getClass().toString());
        
        // use this to start and trigger a service
        Intent i= new Intent(context, UpdateDatabaseService.class);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");
        context.startService(i);
    }
}
