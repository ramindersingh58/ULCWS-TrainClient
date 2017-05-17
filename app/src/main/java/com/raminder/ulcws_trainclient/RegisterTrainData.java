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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.raminder.ulcws_trainclient.GlobalApp.weburl;

public class RegisterTrainData extends AppCompatActivity {
    EditText et5,et6,et7;
    EditText et8,et9,et10,et11;
    String ti,tn,ss,ak;
    String ti2,ak2,h,m;
    TextView tv4;
    ArrayList<String> al;
    Spinner sp1;
    ToggleButton tb1;
    SharedPreferences sharedPreferences;
    private int flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_train_data);
        et5=(EditText)findViewById(R.id.et5);
        et6=(EditText)findViewById(R.id.et6);
        et7=(EditText)findViewById(R.id.et7);
        tv4=(TextView)findViewById(R.id.tv4);
        al=new ArrayList<>();
        sp1=(Spinner) (findViewById(R.id.sp1));
        al.add("Select Starting Station");
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
                ss=sp1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        sharedPreferences= getSharedPreferences("mypref", MODE_PRIVATE);

        et8=(EditText)findViewById(R.id.et8);
        et9=(EditText)findViewById(R.id.et9);
        et10=(EditText)findViewById(R.id.et10);
        et11=(EditText)findViewById(R.id.et11);
        tb1=(ToggleButton)(findViewById(R.id.tb1));
        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    flag=1;
                else
                    flag=-1;
            }
        });

    }
    public void go(View view) {
        ti=et5.getText().toString();
        tn=et6.getText().toString();
        ak=et7.getText().toString();
        if (ti.trim().equals("") )
        {
            et5.setError("Please Enter Train Id");
            et5.requestFocus();
        }
        else if(tn.trim().equals(""))
        {
            et6.setError("Please Enter Train Name");
            et6.requestFocus();
        }
        else if(ak.trim().equals(""))
        {
            et7.setError("Please Enter Authenticating Key");
            et7.requestFocus();
        }
        else if (ss.trim().equals("Select Starting Station"))
        {
            Toast.makeText(this, "Please Select Starting Station", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(isNetworkConnected())
                 new Thread(new job()).start();
            else
                Toast.makeText(this,"Internet is not accessible. Please enable it first",Toast.LENGTH_SHORT).show();
        }
    }

    public void setup(View view) {
        Intent in = new Intent(RegisterTrainData.this, Settings.class);
        startActivity(in);
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


                String query = "?ti=" + ti + "&tn=" + tn + "&ak="+ak;
                URL url = new URL(weburl+"/SaveTrainData" + query);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("Raminder", "url"+url);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                final StringBuffer sb = new StringBuffer();
                Log.d("Raminder", "register clicked");
                if (connection.getResponseCode() == 200) {
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null)
                            break;
                        sb.append(temp);
                        Log.d("Raminder", sb +"");
                    }
                }
                Log.d("Raminder",sb+"sb");
                if(sb.toString().equals("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("trainid", ti);
                    editor.putString("trainname", tn);
                    editor.putString("ss",ss);
                    editor.putString("ak",ak);
                    editor.commit();
                    Intent in = new Intent(RegisterTrainData.this, trainhome.class);
                    startActivity(in);
                    finish();
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv4.setText(sb);
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void report(View view) {
        ti2=et8.getText().toString();
        ak2=et9.getText().toString();
        h=et10.getText().toString();
        m=et11.getText().toString();
        if (ti2.trim().equals("") )
        {
            et8.setError("Please Enter Train Id");
            et8.requestFocus();
        }
        else if(h.trim().equals(""))
        {
            et10.setError("Please Fill 0 if there is only minutes delay");
            et10.requestFocus();
        }
        else if(ak2.trim().equals(""))
        {
            et9.setError("Please Enter Authenticating Key");
            et9.requestFocus();
        }
        else if (m.trim().equals(""))
        {
            et11.setError("Please Fill 0 if there is only hours delay");
            et11.requestFocus();
        }
        else if(isNetworkConnected()) {
            new Thread(new job2()).start();
        }
        else
        {
            Toast.makeText(this,"Internet is not accessible. Please enable it first",Toast.LENGTH_SHORT).show();
        }
    }
    ///
    class job2 implements Runnable
    {

        @Override
        public void run() {
            try {

                String query = "?ti=" + ti2 + "&ak=" + ak2 + "&h="+h+"&m="+m+"&flag="+flag;
                URL url = new URL(weburl+"/ReportDelay" + query);
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
                Log.d("Raminder",sb+"sb");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv4.setText(sb);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
