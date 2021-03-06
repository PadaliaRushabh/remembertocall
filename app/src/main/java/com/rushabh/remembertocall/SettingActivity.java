package com.rushabh.remembertocall;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Created by rushabh on 24/12/15.
 */
public class SettingActivity extends AppCompatActivity {

    int reminderDays = 15;
    boolean notificationIsEnabled = true;

    EditText txt_reminderDays;
    TextView txt_hours;
    Switch swt_notificationIsEnabled;

    SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());

        txt_reminderDays = (EditText) findViewById(R.id.editText_reminderDays);
        swt_notificationIsEnabled = (Switch) findViewById(R.id.switch_notification);

        reminderDays = sharedPreferenceHelper.readReminder();
        notificationIsEnabled = sharedPreferenceHelper.readIsReminderNotification();

        txt_hours = (TextView) findViewById(R.id.txt_time);

        LocalTime time1 = new LocalTime(sharedPreferenceHelper.readNotificationHour(), sharedPreferenceHelper.readNotificationMinute());
        String formattedTime = time1.toString("HH:mm");
        txt_hours.setText(formattedTime);

        txt_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        LocalTime time1 = new LocalTime(selectedHour, selectedMinute);
                        String formattedTime = time1.toString("HH:mm");
                        txt_hours.setText(formattedTime);
                        sharedPreferenceHelper.writeNotificationHour(selectedHour);
                        sharedPreferenceHelper.writeNotificationMinute(selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Notification Time");
                mTimePicker.show();

            }
        });

        txt_reminderDays.setText(Integer.toString(reminderDays));

        swt_notificationIsEnabled.setChecked(notificationIsEnabled);
    }


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
    }


}
