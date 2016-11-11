package com.vince.socius;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  OnMapReadyCallback
        ,GoogleApiClient.OnConnectionFailedListener{

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
    public final static String EXTRA_UID = "com.vince.socius.UID";
    public final static String EXTRA_DESCRIPTION = "com.vince.socius.DESCRIPTION";

    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;

    private Marker lastOpened = null;

    private ImageView imgMyLocation;

    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference personRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    public static final String ANONYMOUS = "anonymous";

    private static String mUsername;

    private ArrayList<Person> people;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private TextView infoDescription;
    private TextView infoNumber;
    private Button infoButton;
    private Button editButton;

    private TextView addressTextview;

    private OnInfoWindowElemTouchListener infoButtonListener;
    private OnInfoWindowElemTouchListener editButtonListener;

    private Person currentEdit;

    private Boolean isStaff;

    public static final long DISCONNECT_TIMEOUT = 1000;

    //map to store markers
    private Map<Person, Marker> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        isStaff = false;
        markerMap = new HashMap<Person,Marker>();

        addressTextview = (TextView) findViewById(R.id.realAddress);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //checking if the current user is a staff member or not.
        final HashSet<String> staff = new HashSet<String>();
        DatabaseReference staffRef = mFirebaseDatabaseReference.child("adsf");

        staffRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String email = dataSnapshot.getValue(String.class);

                staff.add(email);
                Log.v("E_STAFF_ADDED", email);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            Log.v("E_STAFF_USER", mFirebaseDatabaseReference.child("Staff").child(mFirebaseUser.getUid()).toString());
            /*while(staff.isEmpty()) {
                isStaff = false;
            }*/
            /*
            if (mFirebaseDatabaseReference.child("Staff").child(mFirebaseUser.getUid()).getRef() != null){
                isStaff = true;
            }else{
                isStaff = false;
            }*/
        }
        Intent intent = new Intent(this, User.class);
        intent.putExtra(EXTRA_UID, mFirebaseUser.getUid());
        startActivityForResult(intent,5);




        // For Google Login
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this /* FragmentActivity */,  MainActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();





        personRef = mFirebaseDatabaseReference.child("People");
        people = new ArrayList<Person>();
        personRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Person p = dataSnapshot.getValue(Person.class);

                people.add(p);

                if (googleMap != null){
                    addPeople();
                }
                Log.v("E_CHILD_ADDED", Boolean.toString(p.getIsNotDelete()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Person p = dataSnapshot.getValue(Person.class);
                Log.v("E_CHILD_ADDED1", Boolean.toString(p.getIsNotDelete()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                Person p = dataSnapshot.getValue(Person.class);
                Log.v("E_CHILD_ADDED2", Boolean.toString(p.getIsNotDelete()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        //set toolbar initially

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        // Initialize my EditTexts

        //addressEditText = (EditText) findViewById(R.id.addressEditText);

        // Initialize my Google Map
        try {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(MainActivity.this);

        } catch (Exception e) {

            e.printStackTrace();

        }


    }


    //Handles inactivity to update the address in address textbox
    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            LatLng newLoc = googleMap.getCameraPosition().target;

            try {
                Geocoder geocoder = new Geocoder(MainActivity.this);
                List<Address> addresses = geocoder.getFromLocation(newLoc.latitude, newLoc.longitude, 1);
                if (addresses.size() == 0) {
                    Toast toast = Toast.makeText(MainActivity.this, "Not a valid address", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getAddressLine(1);
                    String country = addresses.get(0).getAddressLine(2);
                    String newAddress = address + " " + city;
                    addressTextview.setText(newAddress);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    @Override
    public void onMapReady(GoogleMap map ) {
        googleMap = map;

        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));

        // Create different info windows for staff and users.
        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.infowindow, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoDescription = (TextView)infoWindow.findViewById(R.id.description);
        this.infoNumber = (TextView) infoWindow.findViewById(R.id.numberpeop);


        //repeat for delete button
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        this.editButton = (Button)infoWindow.findViewById(R.id.buttonEdit);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.button_pressed),
                getResources().getDrawable(R.drawable.button_pressed))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(MainActivity.this, marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();

                Person temp = (Person) marker.getTag();
                temp.delete();
                addPeople();
                double lat = temp.getLattitude();
                double lng = temp.getLongitude();
                double key = Math.abs(lat* lng);
                String keystring = Double.toString(key);
                String keystringnew = keystring.replaceAll("\\.", "");
                Log.v("E_KEY_ADDED", keystringnew);
                personRef.child("test" + keystringnew).setValue(temp);
/*
                Marker tempMarker = markerMap.get(temp);
                Toast.makeText(MainActivity.this, tempMarker.getSnippet()+ "'s button clicked!", Toast.LENGTH_SHORT).show();
                tempMarker.remove();*/
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        this.editButtonListener = new OnInfoWindowElemTouchListener(editButton,
                getResources().getDrawable(R.drawable.button_pressed),
                getResources().getDrawable(R.drawable.button_pressed)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Person temp = (Person) marker.getTag();
                currentEdit = temp;
                String description = temp.getDescription();
                String address = temp.getAddress();

                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra(EXTRA_MESSAGE, address);
                intent.putExtra(EXTRA_DESCRIPTION, description);
                startActivityForResult(intent, 4);

            }
        };
        this.editButton.setOnTouchListener(editButtonListener);

        map.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                Person temp = (Person)marker.getTag();
                infoDescription.setText("Services needed: " + temp.getDescription());
                infoNumber.setText("Number of people: " + temp.getNumber());
                infoButtonListener.setMarker(marker);
                editButtonListener.setMarker(marker);


                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        //Window things finish here

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Intent intent = new Intent(this, noGPS.class);
            startActivityForResult(intent, 3);
        } else {


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return;
            }else {

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                // Define the criteria how to select the location provider -> use
                // default
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                while (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

                LatLng coordinate = new LatLng(lat, lng);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
                addPeople();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //might need to add grandResults[1]

                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    // Define the criteria how to select the location provider -> use
                    // default
                    Criteria criteria = new Criteria();
                    provider = locationManager.getBestProvider(criteria, true);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    while (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    //Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng,Toast.LENGTH_SHORT ).show();

                    LatLng coordinate = new LatLng(lat, lng);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18.0f));

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
                    addPeople();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent intent = new Intent(this, noGPS.class);
                    startActivityForResult(intent, 3);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
        }else if (id == R.id.action_logout) {
            // User logout
            mFirebaseAuth.signOut();
            isStaff = false;
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;

            // Show login screen again
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
                    LatLng temp = getLocationFromAddress(newAddress);

                    int hourextra = data.getIntExtra("Minutes", 0);

                    Calendar calendar = Calendar.getInstance();
                    //calendar.add(Calendar.MINUTE, (-1 * minusmin));
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int year = calendar.get(Calendar.YEAR);
                    int hour = hourextra;
                    //int minute = calendar.get(Calendar.MINUTE);
                    String amorpm = data.getStringExtra("AmOrPm");

                    String description = data.getStringExtra("Description");
                    int numberPeople = Integer.parseInt(data.getStringExtra("Number"));


                    //time format YYYY/MM/DD/HOUR/MIN
                    String time = year + "/" + month + "/" + day + "/" + hour + "/" + amorpm;
                    Person pers = new Person(newAddress, temp.latitude,temp.longitude,time,mUsername ,description
                                            ,numberPeople);
                    double key = Math.abs( temp.latitude * temp.longitude);
                    String keystring = Double.toString(key);
                    String keystringnew = keystring.replaceAll("\\.", "");
                    Log.v("E_KEY_ADDED", keystringnew);
                    personRef.child("test" + keystringnew).setValue(pers);

                    addPeople();
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

                        int hourextra = data.getIntExtra("Minutes", 0);

                        Calendar calendar = Calendar.getInstance();
                        //calendar.add(Calendar.MINUTE, (-1 * minusmin));
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);
                        int hour = hourextra;
                        //int minute = calendar.get(Calendar.MINUTE);
                        String amorpm = data.getStringExtra("AmOrPm");

                        String description = data.getStringExtra("Description");

                        int numberPeople = Integer.parseInt(data.getStringExtra("Number"));

                        //time format YYYY/MM/DD/HOUR/MIN
                        String time = year + "/" + month + "/" + day + "/" + hour + "/" + amorpm;

                        Person pers = new Person(newAddress, newLoc.latitude,newLoc.longitude,time,mUsername,description
                                                ,numberPeople);
                        double key = Math.abs(newLoc.latitude * newLoc.longitude);
                        String keystring = Double.toString(key);
                        String keystringnew = keystring.replaceAll("\\.", "");
                        Log.v("E_KEY_ADDED", keystringnew);
                        personRef.child("test" + keystringnew).setValue(pers);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addPeople();
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
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
                    addPeople();
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
                    addPeople();
                }
            }else if (requestCode == 4){
                {
                    boolean isConf = data.getBooleanExtra("Confirmation", false);
                    if (isConf) {

                        /*
                        Person temp = (Person) marker.getTag();
                        temp.delete();
                        addPeople();
                        double lat = temp.getLattitude();
                        double lng = temp.getLongitude();
                        double key = Math.abs(lat* lng);
                        String keystring = Double.toString(key);
                        String keystringnew = keystring.replaceAll("\\.", "");
                        Log.v("E_KEY_ADDED", keystringnew);
                        personRef.child("test" + keystringnew).setValue(temp);
                        */

                        int hourextra = data.getIntExtra("Minutes", 0);

                        Calendar calendar = Calendar.getInstance();
                        //calendar.add(Calendar.MINUTE, (-1 * minusmin));
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);
                        int hour = hourextra;
                        //int minute = calendar.get(Calendar.MINUTE);
                        String amorpm = data.getStringExtra("AmOrPm");

                        String description = data.getStringExtra("Description");
                        currentEdit.setDescription(description);

                        //time format YYYY/MM/DD/HOUR/MIN
                        String time = year + "/" + month + "/" + day + "/" + hour + "/" + amorpm;
                        currentEdit.setTime(time);
                        double key = Math.abs( currentEdit.getLattitude() * currentEdit.getLongitude());
                        String keystring = Double.toString(key);
                        String keystringnew = keystring.replaceAll("\\.", "");
                        Log.v("E_KEY_ADDED", keystringnew);
                        personRef.child("test" + keystringnew).setValue(currentEdit);

                        addPeople();
                    }
                }
            }else if(requestCode == 5){
                isStaff = data.getBooleanExtra("IsStaff",false);
                addPeople();
            }
        }
    }

    public void addPeople() {
        googleMap.clear();
        for (int i = 0; i < people.size(); i++) {
            Person temp = people.get(i);
            if (temp.getIsNotDelete() &&(isStaff || temp.getPoster().equals(mUsername))) {
                String[] times = temp.getTime().split("/");
                if (times.length >= 5) {
                    String year = times[0];
                    String month = times[1];
                    String day = times[2];
                    String hour = times[3];
                    String am = times[4];


                    String markerTime = "Time posted: " + hour + " " + am + "  " +
                            " Date Posted: " + month + "/" + day;
                    LatLng newLoc = new LatLng(temp.getLattitude(), temp.getLongitude());
                    addressMarker = googleMap.addMarker(new MarkerOptions()
                            .position(newLoc)
                            .title(temp.getAddress())
                            .snippet(markerTime));
                    addressMarker.setTag(temp);
                    markerMap.put(temp, addressMarker);
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
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

}
