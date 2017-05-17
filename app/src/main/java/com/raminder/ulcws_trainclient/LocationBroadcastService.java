package com.raminder.ulcws_trainclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import static com.raminder.ulcws_trainclient.GlobalApp.isStopped;
import static com.raminder.ulcws_trainclient.GlobalApp.rate;
import static com.raminder.ulcws_trainclient.GlobalApp.weburl;

public class LocationBroadcastService extends Service {
    double lat, lon;
    boolean issending = false;
    SharedPreferences sharedPreferences;

    private String ti;

    public LocationBroadcastService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //---check if GPS_PROVIDER is enabled---
        final boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //---check if NETWORK_PROVIDER is enabled---
        boolean networkStatus = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // check which provider is enabled
        myLocationListener ml = new myLocationListener();


        if (gpsStatus == true) {
            Toast.makeText(this, "GPS is Enabled, using it", Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ml);
        }

        if (networkStatus == true) {
            Toast.makeText(this, "Network Location is Enabled, using it", Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ml);
        }

        Log.d("Raminder", "::in location service");
        sharedPreferences= getSharedPreferences("mypref", MODE_PRIVATE);
        ti=sharedPreferences.getString("trainid","");
        issending = false;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Raminder","service stopped");
        super.onDestroy();
    }

    ///// Inner Class  //////////////////
    class myLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d("Raminder", "isStopped"+isStopped);
            if (issending == false&&isStopped==false) {
                new Thread(new job(lat, lon,ti)).start();
            }
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    class job implements Runnable {

        double lat, lon;
        String ti;

        job(double lat, double lon,String ti) {
            this.lat = lat;
            this.lon = lon;
            this.ti = ti;
            Log.d("Raminder:in loc service", "lat:" + lat + "  lon:" + lon + "ti:" + ti);
        }

        @Override
        public void run() {
            try {

                issending = true;
//                String locations = sharedPreferences.getString("locations", "");
//                locations = "Lat:" + lat + "  Lon:" + lon + "\n" +locations;
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("locations", locations);

                String query = "?lat=" + lat + "&lng=" + lon + "&ti=" + ti+"&ln=unknown";
                URL url = new URL(weburl + "/train_locations" + query);
                Log.d("Raminder", "url" + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                final StringBuffer sb = new StringBuffer();
                if (connection.getResponseCode() == 200) {
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null)
                            break;
                        sb.append(temp);
                        Log.d("Raminder", "from server" + sb);
                    }
                }
                Intent intent=new Intent("newlocs");
                intent.putExtra("s","Time:"+sb+"   Lat:"+lat+" Lon:"+lon);
                sendBroadcast(intent);
                Thread.sleep(rate*1000);

                issending = false;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
