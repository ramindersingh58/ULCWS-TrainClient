package com.raminder.ulcws_trainclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar pb1;
    int mProgressStatus = 0;
    Handler mHandler = new Handler();
    int PERMISSION_REQUEST_CODE = 200;
    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb1=(ProgressBar)(findViewById(R.id.pb1));
        mainLayout=(LinearLayout)findViewById(R.id.mainLayout);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkLocationPermission())
            {
               // Toast.makeText(this, "All Permissions Already Granted", Toast.LENGTH_SHORT).show();
                launch();
            }
            else
            {
                String permissions[] = {ACCESS_FINE_LOCATION};

                ActivityCompat.requestPermissions(this,permissions,PERMISSION_REQUEST_CODE);
            }
        }

        else
        {
            launch();
        }
    }
    private void launch()
    {
        // Start lengthy operation in a background thread
        Thread t1=new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < 2) {

                    try{
                        Thread.sleep(1000);
                        mProgressStatus ++;
                    }catch(Exception e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            pb1.setProgress(mProgressStatus);
                        }
                    });
                }
                sharedPreferences= getSharedPreferences("mypref", MODE_PRIVATE);
                if(isNetworkConnected()) {
                    if(sharedPreferences.getString("trainid", "").equals(""))
                    {
                        Intent in = new Intent(MainActivity.this, RegisterTrainData.class);
                        startActivity(in);
                        finish();
                    }
                    else {
                        Intent in = new Intent(MainActivity.this, trainhome.class);
                        startActivity(in);
                        finish();
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(),"Internet is not accessible. Please enable it first",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        t1.start();
    }
    private boolean isNetworkConnected() {

        // get Connectivity Manager to get network status
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true; //we have a connection
        } else {
            return false; // no connection!
        }
    }
    boolean checkLocationPermission()
    {
        int ans1 = ContextCompat.checkSelfPermission(getBaseContext(),
                ACCESS_FINE_LOCATION);
        return (ans1== PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launch();
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //User has deny from permission dialog
                Snackbar.make(mainLayout, "Enable Location Permission for tracking location of Train",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_CODE);
                            }
                        })
                        .show();
            } else {
                // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                Snackbar.make(mainLayout, "Enable location Permission from Settings",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



    private static long back_pressed;


    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();

        else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}