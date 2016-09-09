package com.vince.socius;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    BackgroundWorker asyncTask = new BackgroundWorker(this);
    NavigationView navigationView = null;
    Toolbar toolbar = null;

    EditText addressEditText;

    // Used to utilize map capabilities
    private GoogleMap googleMap;

    // Stores latitude and longitude data for addresses
    LatLng addressPos;
    // Used to place Marker on my map
    Marker addressMarker;

    private LocationManager locationManager;

    private String provider;

    public final static String EXTRA_MESSAGE = "com.vince.socius.MESSAGE";

    private Marker lastOpened = null;

    private ImageView imgMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        asyncTask.delegate = this;
        /*MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction
                = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        */
        //set toolbar initially
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize my EditTexts

        addressEditText = (EditText) findViewById(R.id.addressEditText);

        // Initialize my Google Map
        try {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabledGPS = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabledGPS) {
                Intent intent = new Intent(this, noGPS.class);
                startActivityForResult(intent, 3);
            } else {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                // Define the criteria how to select the location provider -> use
                // default
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

                LatLng coordinate = new LatLng(lat, lng);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //googleMap.setPadding(0, 200,0,0);
                googleMap.setMyLocationEnabled(true);

                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                googleMap.setTrafficEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);

                imgMyLocation = (ImageView) findViewById(R.id.myMapLocationButton);

                imgMyLocation.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getMyLocation();

                    }
                });
                googleMap.setIndoorEnabled(false);

                googleMap.setBuildingsEnabled(false);


                //googleMap.getUiSettings().setZoomControlsEnabled(true);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {
                        // Check if there is an open info window
                        if (lastOpened != null) {
                            // Close the info window
                            lastOpened.hideInfoWindow();

                            // Is the marker the same marker that was already open
                            if (lastOpened.equals(marker)) {
                                // Nullify the lastOpened object
                                lastOpened = null;
                                // Return so that the info window isn't opened again
                                return true;
                            }
                        }

                        // Open the info window for the marker
                        marker.showInfoWindow();
                        // Re-assign the last opened such that we can close it later
                        lastOpened = marker;

                        // Event was handled by our code do not launch default behaviour.
                        return true;
                    }
                });

                //Database loading, replace this with firebase
                String type = "load";
                asyncTask = new BackgroundWorker(this);
                asyncTask.delegate = this;
                asyncTask.execute(type);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Add fragments here later for profile, etc
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showAddressMarker(View view) {

        // Get the street address entered
        String newAddress = addressEditText.getText().toString();
        LatLng temp = getLocationFromAddress(newAddress);
        if (getLocationFromAddress(newAddress) != null) {
            Intent intent = new Intent(this, Confirmation.class);
            final int result = 1;
            intent.putExtra(EXTRA_MESSAGE, newAddress);
            startActivityForResult(intent, result);

        } else {
            Toast toast = Toast.makeText(this, "Not a valid address", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    public void pinShare(View view) {
        LatLng newLoc = googleMap.getCameraPosition().target;

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = geocoder.getFromLocation(newLoc.latitude, newLoc.longitude, 1);
            if (addresses.size() == 0) {
                Toast toast = Toast.makeText(this, "Not a valid address", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                String newAddress = address + " " + city;
                Intent intent = new Intent(this, Confirmation.class);
                intent.putExtra(EXTRA_MESSAGE, newAddress);
                startActivityForResult(intent, 2);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //most of the changes while migrating to database will be here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //request code for the address button press
            if (requestCode == 1) {
                boolean isConf = data.getBooleanExtra("Confirmation", false);
                if (isConf) {

                    String newAddress = addressEditText.getText().toString();
                    new PlaceAMarker().execute(newAddress);
                    String type = "add";
                    asyncTask = new BackgroundWorker(this);
                    LatLng temp = getLocationFromAddress(newAddress);

                    int minusmin = data.getIntExtra("Minutes", 0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, (-1 * minusmin));
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    //time format YYYY/MM/DD/HOUR/MIN
                    String time = year + "/" + month + "/" + day + "/" + hour + "/" + minute;
                    //replace this part with firebase code
                    asyncTask.delegate = this;
                    asyncTask.execute(type, newAddress, Double.toString(temp.latitude), Double.toString(temp.longitude), time);
                    type = "load";
                    asyncTask = new BackgroundWorker(this);
                    asyncTask.delegate = this;
                    asyncTask.execute(type);
                }
            } else if (requestCode == 2) {
                boolean isConf = data.getBooleanExtra("Confirmation", false);
                if (isConf) {

                    LatLng newLoc = googleMap.getCameraPosition().target;
                    try {
                        Geocoder geocoder = new Geocoder(this);
                        List<Address> addresses = geocoder.getFromLocation(newLoc.latitude, newLoc.longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getAddressLine(1);
                        String country = addresses.get(0).getAddressLine(2);
                        String newAddress = address + " " + city;
                        String type = "add";

                        //replace
                        asyncTask = new BackgroundWorker(this);

                        int minusmin = data.getIntExtra("Minutes", 0);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, (-1 * minusmin));
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        //time format YYYY/MM/DD/HOUR/MIN
                        String time = year + "/" + month + "/" + day + "/" + hour + "/" + minute;

                        //replace
                        asyncTask.delegate = this;
                        asyncTask.execute(type, newAddress, Double.toString(newLoc.latitude), Double.toString(newLoc.longitude), time);
                        type = "load";

                        asyncTask = new BackgroundWorker(this);
                        asyncTask.delegate = this;
                        asyncTask.execute(type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String type = "load";
                    asyncTask = new BackgroundWorker(this);
                    asyncTask.delegate = this;
                    asyncTask.execute(type);
                    /*
                    addressMarker = googleMap.addMarker(new MarkerOptions()
                            .position(newLoc)
                            .title("Person"));*/
                }
            } else if (requestCode == 3) {

                boolean isLocationOn = data.getBooleanExtra("LocOn", false);
                if (isLocationOn) {
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    // Define the criteria how to select the locatioin provider -> use
                    // default
                    Criteria criteria = new Criteria();
                    provider = locationManager.getBestProvider(criteria, true);
                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    // waits for a location from the location Manager
                    while (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

                    LatLng coordinate = new LatLng(lat, lng);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);

                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                    googleMap.getUiSettings().setTiltGesturesEnabled(false);

                    googleMap.setTrafficEnabled(false);

                    imgMyLocation = (ImageView) findViewById(R.id.myMapLocationButton);

                    imgMyLocation.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getMyLocation();

                        }
                    });

                    googleMap.setIndoorEnabled(false);

                    googleMap.setBuildingsEnabled(false);

                    googleMap.getUiSettings().setZoomControlsEnabled(true);


                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        public boolean onMarkerClick(Marker marker) {
                            // Check if there is an open info window
                            if (lastOpened != null) {
                                // Close the info window
                                lastOpened.hideInfoWindow();

                                // Is the marker the same marker that was already open
                                if (lastOpened.equals(marker)) {
                                    // Nullify the lastOpened object
                                    lastOpened = null;
                                    // Return so that the info window isn't opened again
                                    return true;
                                }
                            }

                            // Open the info window for the marker
                            marker.showInfoWindow();
                            // Re-assign the last opened such that we can close it later
                            lastOpened = marker;

                            // Event was handled by our code do not launch default behaviour.
                            return true;
                        }
                    });

                    //replace
                    String type = "load";


                    asyncTask = new BackgroundWorker(this);
                    asyncTask.delegate = this;
                    asyncTask.execute(type);
                } else {

                    double lat = data.getDoubleExtra("Lat", 0);
                    double lng = data.getDoubleExtra("Long", 0);
                    //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

                    LatLng coordinate = new LatLng(lat, lng);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    googleMap.setTrafficEnabled(false);


                    googleMap.setIndoorEnabled(false);

                    googleMap.setBuildingsEnabled(false);

                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                    googleMap.getUiSettings().setTiltGesturesEnabled(false);


                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        public boolean onMarkerClick(Marker marker) {
                            // Check if there is an open info window
                            if (lastOpened != null) {
                                // Close the info window
                                lastOpened.hideInfoWindow();

                                // Is the marker the same marker that was already open
                                if (lastOpened.equals(marker)) {
                                    // Nullify the lastOpened object
                                    lastOpened = null;
                                    // Return so that the info window isn't opened again
                                    return true;
                                }
                            }

                            // Open the info window for the marker
                            marker.showInfoWindow();
                            // Re-assign the last opened such that we can close it later
                            lastOpened = marker;

                            // Event was handled by our code do not launch default behaviour.
                            return true;
                        }
                    });

                    //replace
                    String type = "load";
                    asyncTask = new BackgroundWorker(this);
                    asyncTask.delegate = this;
                    asyncTask.execute(type);
                }
            }
        }
    }

    @Override
    public void processFinish(String output) {
        String[] rows = output.split(";");
        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(":");
            if (cols.length >= 3) {
                String lat = cols[1];
                String lng = cols[2];
                String time = cols[3];
                String[] times = time.split("/");
                if (times.length >= 5) {
                    String year = times[0];
                    String month = times[1];
                    String day = times[2];
                    String hour = times[3];
                    String min = times[4];
                    if (min.length() == 1) {
                        min = "0" + min;
                    }
                    String markerTime = "Time posted: " + hour + ":" + min + "   Date Posted: " + month + "/" + day;
                    LatLng newLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    addressMarker = googleMap.addMarker(new MarkerOptions()
                            .position(newLoc)
                            .title(cols[0])
                            .snippet(markerTime));
                }

            }
        }
    }

    public Object getMyLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        // waits for a location from the location Manager
        while (location == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission
                            (this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

        LatLng coordinate = new LatLng(lat, lng);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

        return null;
    }

    class PlaceAMarker extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get the 1st address passed
            String startAddress = params[0];

            // Replace the spaces with %20
            startAddress = startAddress.replaceAll(" ","%20");

            // Call for the latitude and longitude and pass in that
            // we don't want directions
            getLatLong(startAddress, false);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Draw the marker on the screen
            /*addressMarker = googleMap.addMarker(new MarkerOptions()
                    .position(addressPos)
                    .title("Person"));
            */

        }


    }


    protected void getLatLong(String address, boolean setDestination){

        // Define the uri that is used to get lat and long for our address
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                address + "&sensor=false";

        // Use the get method to retrieve our data
        HttpGet httpGet = new HttpGet(uri);

        // Acts as the client which executes HTTP requests
        HttpClient client = new DefaultHttpClient();

        // Receives the response from our HTTP request
        HttpResponse response;

        // Will hold the data received
        StringBuilder stringBuilder = new StringBuilder();

        try {

            // Get the response of our query
            response = client.execute(httpGet);

            // Receive the entity information sent with the HTTP message
            HttpEntity entity = response.getEntity();

            // Holds the sent bytes of data
            InputStream stream = entity.getContent();
            int byteData;

            // Continue reading data while available
            while ((byteData = stream.read()) != -1) {
                stringBuilder.append((char) byteData);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double lat = 0.0;
        double lng = 0.0;

        // Holds key value mappings
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            // Get the returned latitude and longitude
            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            // Change the lat and long depending on if we want to set the
            // starting or ending destination
            if(setDestination){

            } else {
                addressPos = new LatLng(lat, lng);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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


}
