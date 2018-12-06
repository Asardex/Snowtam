package com.example.quent.snowtam;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> locations = new ArrayList<String>();
    ArrayList<String> arp = new ArrayList<String>();
    ArrayList<String> longi = new ArrayList<String>();
    ArrayList<String> lati = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        arp.add(intent.getStringExtra("airportLoc1"));
        /*
        arp.add(intent.getStringExtra("airportLoc2"));
        arp.add(intent.getStringExtra("airportLoc3"));
        arp.add(intent.getStringExtra("airportLoc4"));
*/
        locations.add(intent.getStringExtra("code1"));
        /*
        locations.add(intent.getStringExtra("code2"));
        locations.add(intent.getStringExtra("code3"));
        locations.add(intent.getStringExtra("code4"));
*/

        final Button button = findViewById(R.id.buttonBackResult);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        final TextView tvCode = (TextView) findViewById(R.id.textView3);
        final Switch switch1 = findViewById(R.id.switch1);
        tvCode.setText("Decode");
        switch1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                if(switch1.isChecked())
                {
                    tvCode.setText("Decode");
                }else {
                    tvCode.setText("Code");
                }
            }
        });



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {

            for(int i = 0; i<arp.size();i++)
            {
                if (!arp.get(i).matches("Erreur"))
                {
                    longi.add(arp.get(i).split("\\,")[0]);
                    lati.add(arp.get(i).split("\\,")[1]);
                }

            }
            // Add a marker in Sydney and move the camera
            LatLng airport = new LatLng(Float.parseFloat(longi.get(0)),Float.parseFloat(lati.get(0))); //Integer.parseInt(longi.get(0))
            mMap.addMarker(new MarkerOptions().position(airport).title(arp.get(0)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(airport,14));


        } catch(Exception ex) {

        }

    }

}
