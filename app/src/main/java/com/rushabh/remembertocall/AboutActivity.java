package com.rushabh.remembertocall;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

import java.util.Calendar;

/**
 * Created by rushabh on 24/12/15.
 */
public class AboutActivity extends AppCompatActivity {

    TextView version, developers;

    SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        version = (TextView) findViewById(R.id.about_app_version);
        version.setText("Version: " +  BuildConfig.VERSION_NAME);

        developers = (TextView) findViewById(R.id.about_app_developers);
        developers.setMovementMethod(LinkMovementMethod.getInstance());


    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                sharedPreferenceHelper.writeReminder(Integer.parseInt(txt_reminderDays.getText().toString()));
                sharedPreferenceHelper.writeIsReminderNotification(swt_notificationIsEnabled.isChecked());

                Intent intent = new Intent();
                intent.setAction("com.rushabh.remenbertocall.NOTIFICATION_TIME_CHANGE");
                sendBroadcast(intent);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_saved_toast) ,Toast.LENGTH_SHORT).show();

                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


}
