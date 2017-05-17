package com.raminder.ulcws_trainclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.raminder.ulcws_trainclient.GlobalApp.weburl;

public class SetDestination extends AppCompatActivity {
    ArrayList<String> al;
    Spinner sp1;
    String ds,ss;
    SharedPreferences sharedPreferences;
    private String ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination);


        al=new ArrayList<>();
        sp1=(Spinner) (findViewById(R.id.sp1));
        al.add("Select Ending Station");
        al.add("Testing Station 1");
        al.add("Testing Station 2");
        al.add("Testing Station 3");
        al.add("Testing Station 4");
        al.add("Testing Station 5");
        al.add("Testing Station 6");
        al.add("Testing Station 7");
        al.add("Testing Station 8");
        al.add("My Station 1");
        al.add("My Station 2");
        al.add("My Station 3");
        al.add("My Station 4");
        al.add("Amritsar");
        al.add("Ludhiana");
        al.add("Chandigarh");
        al.add("New Delhi");
        al.add("Jaipur");
        al.add("Mumbai");
        al.add("Pune");
        al.add("Chennai");
        al.add("Bengaluru");
        al.add("Nagpur");
        al.add("Hyderabad");
        ArrayAdapter<String> ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al);

        sp1.setAdapter(ad);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ds=sp1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        sharedPreferences= getSharedPreferences("mypref", MODE_PRIVATE);
        ss=sharedPreferences.getString("ss","");
        ti=sharedPreferences.getString("trainid","");
    }

    public void delete(View view) {
        if (ds.trim().equals("Select Ending Station"))
        {
            Toast.makeText(this, "Please Select Ending Station", Toast.LENGTH_SHORT).show();
        }
        else if(isNetworkConnected()) {
            new Thread(new job()).start();
        }
        else
        {
            Toast.makeText(this,"Internet is not accessible. Please enable it first",Toast.LENGTH_LONG).show();
        }

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
    ///
    class job implements Runnable
    {

        @Override
        public void run() {
            try {

                String query = "?ti=" + ti + "&ss=" + ss +"&ds="+ ds;
                URL url = new URL(weburl+"/SaveStations" + query);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("Raminder", "url"+url);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                final StringBuffer sb = new StringBuffer();
                Log.d("Raminder", "go clicked");
                if (connection.getResponseCode() == 200) {
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null)
                            break;
                        sb.append(temp);
                        Log.d("Raminder", sb +"");
                    }
                }
                Log.d("Raminder",sb+"sbset");
                if(sb.toString().equals("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent in = new Intent(SetDestination.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(),"Connection failed"+sb,Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
