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

        Intent i= new Intent(context, UpdateDatabaseService.class);
        context.startService(i);
    }
}
