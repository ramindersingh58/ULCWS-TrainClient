package com.raminder.ulcws_trainclient;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.raminder.ulcws_trainclient.GlobalApp.cloud;
import static com.raminder.ulcws_trainclient.GlobalApp.localhotspot;
import static com.raminder.ulcws_trainclient.GlobalApp.localwifi;
import static com.raminder.ulcws_trainclient.GlobalApp.rate;
import static com.raminder.ulcws_trainclient.GlobalApp.weburl;

public class Settings extends AppCompatActivity {

    RadioButton r1,r2,r3;
    SharedPreferences sharedPreferences;
    SeekBar seekBar;
    TextView tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        r1=(RadioButton)(findViewById(R.id.r1));
        r2=(RadioButton)(findViewById(R.id.r2));
        r3=(RadioButton)(findViewById(R.id.r3));
        if(weburl==cloud)
        {
            r1.setChecked(true);
        }
        else if(weburl==localwifi)
        {
            r2.setChecked(true);
        }
        else
        {
            r3.setChecked(true);
        }


        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    weburl=cloud;
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    weburl=localwifi;
            }
        });
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    weburl=localhotspot;
            }
        });
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        tv4=(TextView)findViewById(R.id.tv4);
        int prog=rate/5;
        seekBar.setProgress(prog);
        tv4.setText("After "+prog*5+" seconds");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rate=5*progress;
                tv4.setText("After "+rate+" seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
