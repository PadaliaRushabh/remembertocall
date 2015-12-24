package com.rushabh.remembertocall;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.rushabh.remembertocall.sharedPreferenceHelper.SharedPreferenceHelper;

/**
 * Created by rushabh on 24/12/15.
 */
public class SettingActivity extends AppCompatActivity {

    int reminderDays = 15;
    boolean notificationIsEnabled = true;

    EditText txt_reminderDays;
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
        notificationIsEnabled = sharedPreferenceHelper.readIsReminderNoticiation();

        txt_reminderDays.setText(Integer.toString(reminderDays));

        swt_notificationIsEnabled.setChecked(notificationIsEnabled);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                sharedPreferenceHelper.writeReminder(Integer.parseInt(txt_reminderDays.getText().toString()));
                sharedPreferenceHelper.writeIsReminderNoticiation(swt_notificationIsEnabled.isChecked());

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_saved_toast) ,Toast.LENGTH_SHORT).show();

                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
