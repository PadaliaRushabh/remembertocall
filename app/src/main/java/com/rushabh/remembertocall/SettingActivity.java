package com.rushabh.remembertocall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by rushabh on 24/12/15.
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
