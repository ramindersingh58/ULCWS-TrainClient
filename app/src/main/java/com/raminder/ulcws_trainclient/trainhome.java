package com.raminder.ulcws_trainclient;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static com.raminder.ulcws_trainclient.GlobalApp.isStopped;

public class trainhome extends AppCompatActivity {

    AlertDialog.Builder ad;
    LocationManager lm;
    boolean isNetworkProviderEnabled;
    boolean isGPSProviderEnabled;
    Intent in4;
    boolean flag=true;
    TextView tv2;
    private SharedPreferences sharedPreferences;
    boolean mIsReceiverRegistered = false;
    MyBR myBR = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainhome);
        tv2=(TextView)findViewById(R.id.tv2);
        sharedPreferences= getSharedPreferences("mypref", MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        isNetworkProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGPSProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isNetworkProviderEnabled && !isGPSProviderEnabled) {
            showLocationSettings();
            isStopped=false;
            in4 = new Intent(this, LocationBroadcastService.class);
            startService(in4);

        }
        else {
            isStopped=false;
            in4 = new Intent(this, LocationBroadcastService.class);
            startService(in4);

        }

//        new Thread(new update()).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsReceiverRegistered) {
            if (myBR == null)
                myBR = new MyBR();
            registerReceiver(myBR, new IntentFilter("newlocs"));
            mIsReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        if (mIsReceiverRegistered) {
            unregisterReceiver(myBR);
            myBR = null;
            mIsReceiverRegistered = false;
        }
        super.onPause();
    }

    class MyBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final String newloc=intent.getStringExtra("s");
            if (action.contains("newlocs")) {
//                Log.d("Raminder",locations+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            String s=tv2.getText().toString();
                            s=newloc+"\n"+s;
                            tv2.setText(s);
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


//    class update implements Runnable
//    {
//
//        @Override
//        public void run() {
//            while(flag){
//                final String locations=sharedPreferences.getString("locations", "");
//                Log.d("Raminder",locations+"")
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try
//                        {
//                            tv2.setText(locations);
//                        }catch(Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
    void showLocationSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(trainhome.this);
        builder.setTitle("Location Access");
        builder.setMessage("This application requires location access. Do you agree ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(it);
            }
        });
        builder.setNegativeButton("NO", null);
        builder.create().show();
    }
    public void stop(View view)
    {
        stopService(in4);
        isStopped=true;
        Intent in=new Intent(trainhome.this,SetDestination.class);
        startActivity(in);
        flag=false;
    }
}
