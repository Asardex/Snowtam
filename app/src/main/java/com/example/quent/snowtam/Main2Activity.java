package com.example.quent.snowtam;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    ArrayList<String> locations = new ArrayList<String>();
    ArrayList<String> arp = new ArrayList<String>();
    ArrayList<String> nameAP = new ArrayList<String>();
    Map<String, String> snowtam = new HashMap<String, String>();

    ArrayList<Snowtam> snowtamObjects = new ArrayList<Snowtam>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();

        locations.add(intent.getStringExtra("code1"));

        try {
            GetAndDispARP();
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


    }

    private void goActivitySimple() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MapsActivity.class);

        intent.putExtra("code1",locations.get(0));
        /*
        intent.putExtra("code2",locations.get(1));
        intent.putExtra("code3",locations.get(2));
        intent.putExtra("code4",locations.get(3));
*/
        intent.putExtra("airportLoc1",arp.get(0));
        /*
        intent.putExtra("airportLoc2",arp.get(1));
        intent.putExtra("airportLoc3",arp.get(2));
        intent.putExtra("airportLoc4",arp.get(3));
*/

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
                            //out += key + " : " + snowtam.get(key) + "\n\n\n";
                            if(snowtam.get(key).compareToIgnoreCase("null")!=0) {
                                snowtamObjects.add(new Snowtam(snowtam.get(key)));
                            }
                        }
                        //tv.setText(snowtamObjects.get(0).toString() + "\n\n\n" + snowtamObjects.get(0).translated());
                        final Switch switch1 = findViewById(R.id.switch1);

                        if(!snowtamObjects.isEmpty())
                        {
                            Log.d("salut44", snowtamObjects.get(0).toString());
                            tv.setText(snowtamObjects.get(0).translated());
                            switch1.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v){

                                    if(switch1.isChecked())
                                    {
                                        tv.setText(snowtamObjects.get(0).translated());

                                    }else {
                                        tv.setText(snowtamObjects.get(0).toString());
                                    }
                                }
                            });
                        }else{
                            //Log.d("salut44", snowtamObjects.toString());
                            tv.setText(R.string.snowtamDisp);
                            switch1.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v){

                                    if(switch1.isChecked())
                                    {
                                    }else {
                                    }
                                }
                            });
                        }

                        tv.setMovementMethod(new ScrollingMovementMethod());
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

    private void GetAndDispARP() {
        final StringBuilder urlBuilderPos = new StringBuilder();
        urlBuilderPos.append("https://www.world-airport-codes.com/search/?s=");
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                recuperationARPJsoup(urlBuilderPos);

            }
        });
        t.start();

    }

    public void recuperationARPJsoup(final StringBuilder urle){
        final TextView tva = (TextView) findViewById(R.id.textView2);
        final Button buttonC1 = findViewById(R.id.buttonC1);
        final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        for(int i = 0; i < locations.size(); i++) {
            if(!locations.get(i).startsWith("Code"))
            {
            Log.d("salut2", locations.get(i));
            String url = urle.toString() + locations.get(i);
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
                Element airport = doc.getElementById("map-airport");
                Element name = doc.getElementsByClass("airport-title").first();
                String coord = airport.attr("data-location");
                arp.add(coord);
                nameAP.add(name.ownText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            }else{
                arp.add("Erreur");
                nameAP.add("ErreurName");
            }
            Log.d("salut3", nameAP.get(i));
        }
        String gpsCoord = "";
        for(int i = 0; i< arp.size();i++){
            gpsCoord += nameAP.get(i) + "\n" +arp.get(i) + "\n";
        }
        tva.setText(gpsCoord);
        buttonC1.setEnabled(true);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        if(!gpsCoord.startsWith("Erreur"))
        {

            buttonC1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    goActivitySimple();
                }
            });
        }else{
        }
    }
}
