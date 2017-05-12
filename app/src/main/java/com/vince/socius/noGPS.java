package com.vince.socius;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class noGPS extends AppCompatActivity {

    EditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_gps);
        address = (EditText) findViewById(R.id.address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void submit(View view) {
        String newAddress = address.getText().toString();
        LatLng temp = getLocationFromAddress(newAddress);
        if (temp == null) {
            Toast toast = Toast.makeText(this, "Not a valid address", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{
            Intent goingBack = new Intent();
            goingBack.putExtra("Lat",temp.latitude);
            goingBack.putExtra("Long",temp.longitude);
            goingBack.putExtra("LocOn", false);
            setResult(RESULT_OK, goingBack);
            finish();
        }
    }

    public void settings(View view){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

    }


    public void onResume(){
        super.onResume();
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (enabledGPS
                && !(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            Intent goingBack = new Intent();
            goingBack.putExtra("LocOn", true);
            setResult(RESULT_OK,goingBack);
            finish();
        } else{
            //Toast toast = Toast.makeText(this, "Location not turned on", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER,0,0);
            //toast.show();
        }
    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null || address.size() == 0) {
                return null;
            }else {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng(location.getLatitude(), (location.getLongitude()));

                return p1;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return p1;
    }


    @Override
    public void onBackPressed(){

    }
}
