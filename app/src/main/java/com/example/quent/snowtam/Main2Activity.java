package com.example.quent.snowtam;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    ArrayList<String> locations = new ArrayList<String>();
    Map<String, String> snowtam = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();

        locations.add(intent.getStringExtra("code1"));
        locations.add(intent.getStringExtra("code2"));
        locations.add(intent.getStringExtra("code3"));
        locations.add(intent.getStringExtra("code4"));

        try {
            GetAndDispSnowtam();
        } catch(Exception ex) {
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText("erreur exception");
        }

        final Button button = findViewById(R.id.buttonBack);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final Button buttonC1 = findViewById(R.id.buttonC1);
        buttonC1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goActivitySimple();
            }
        });
    }

    private void goActivitySimple() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MapsActivity.class);
        startActivity(intent);
    }

    private void GetAndDispSnowtam() {
        final TextView tv = (TextView) findViewById(R.id.textView);

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        String url;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://v4p4sz5ijk.execute-api.us-east-1.amazonaws.com/anbdata/states/notams/notams-list?api_key=2fa58190-ee45-11e8-852d-f95e0e648a6f&format=json&type=&Qcode=&locations=");
        for(int i = 0; i < locations.size(); i++) {
            if(i == (locations.size()-1)) {
                urlBuilder.append(locations.get(i));
            }
            else {
                urlBuilder.append(locations.get(i));
                urlBuilder.append(",");
            }
        }
        urlBuilder.append("&qstring=&states=&ICAOonly=");
        url = urlBuilder.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        for(int i = 0; i < locations.size(); i++) {
                            snowtam.put(locations.get(i), GetSnowtam(response, locations.get(i)));
                        }
                        String out = new String();
                        for (String key : snowtam.keySet()) {
                            out += key + " : " + snowtam.get(key) + "\n\n\n";
                        }
                        tv.setText(out);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        tv.setText(error.toString());
                    }
                });
        mRequestQueue.add(stringRequest);
    }


    private String GetSnowtam(String response, String code) {
        if (response != null) {
            try {
                JSONArray json = new JSONArray(response);
                for(int i = 0; i < json.length(); i++) {
                    JSONObject e = json.getJSONObject(i);
                    if (e.getString("location").compareToIgnoreCase(code) == 0 && e.getString("all").contains("SNOWTAM")) {
                        //Si on trouve la localisation et que all est bien un snowtam
                        return e.getString("all");
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }
}
