package com.gtk.localsearch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LocationListener , NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks {


    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "MSG" ;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    EditText searchTerm;
    TextView searchPlace;
    String placeName;
    Button search;
    ListView resultView;
    LatLng latlong;
    GoogleApiClient mGooglePlaceApi , mGoogleLocationApi;
    ArrayList<ResultPlace> resultPlaces = new ArrayList<>();
    ActionBar actionBar;
    Location location;
    LocationRequest mLocationRequest;
    Address user_location_address;
    Boolean LOCATION_PERMISSION_GURRANTED = false;
    String user_current_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkForLocationPermission();
        inialize();

        if(LOCATION_PERMISSION_GURRANTED)
            getCurrentLocation();
        else{
            Toast.makeText(this , "Failed to get Location" , Toast.LENGTH_LONG).show();
        }

        mGooglePlaceApi = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent i = new Intent(MainActivity.this , mapView.class);
                i.putExtra("PlaceResult" , resultPlaces);
                i.putExtra("Latitude" , latlong.latitude);
                i.putExtra("Longitude" , latlong.longitude);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void inialize() {
        searchTerm = (EditText) findViewById(R.id.searchTerm);
        searchPlace = (TextView) findViewById(R.id.searchPlace);
        search = (Button) findViewById(R.id.search);
        resultView = (ListView) findViewById(R.id.results);
        actionBar = getSupportActionBar();
    }

    //Code for getting Current Location
    private void getCurrentLocation() {
        mGoogleLocationApi = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        Log.d("Something", "Starting connecting Location");
        mGoogleLocationApi.connect();
        Log.d("Something", "Starting connecting Location");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Something" , "Connected Suucessfully");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this , "Failed Permission" , Toast.LENGTH_LONG).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocationApi, mLocationRequest, MainActivity.this);
        Log.d("Something" , "Connection Complete");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Geocoder gc = new Geocoder(MainActivity.this);
        try {
            List<Address> list = gc.getFromLocation(location.getLatitude() , location.getLongitude() , 1);
            user_location_address = list.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGoogleLocationApi.disconnect();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
    //End of Code for Getting Current Location

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

  /*  //Check for google play service
    public boolean checkforplay() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(MainActivity.this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(MainActivity.this, "Cant connect to google play", Toast.LENGTH_LONG).show();
        }
        return false;
    }*/

    //Initialize the fields

    //Action perform when search click
    public void Search(View view) {
        //String place = searchPlace.getText().toString();
        if(latlong==null)
        {
            if(user_location_address!=null)
                latlong = new LatLng(user_location_address.getLatitude() , user_location_address.getLongitude());
        }else {
            if(searchTerm.getText().toString() == null)
                Snackbar.make(view, "Fields Can't be empty", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            else
                parseResultFromUrl();
        }


        /*Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT +5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT +5:30"));
        String localTime = date.format(currentLocalTime);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Log.d("Resultt" ,currentDateTimeString);*/

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        Toast.makeText(this, ""+strDate , Toast.LENGTH_LONG).show();


    }

    private void parseResultFromUrl() {

        String term = searchTerm.getText().toString();
        term = term.toLowerCase();
        Toast.makeText(this, term , Toast.LENGTH_LONG).show();
        Downloader download = new Downloader(this);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlong.latitude + "," + latlong.longitude + "&radius=500&types=" + term + "&key=AIzaSyDchqRHH-7UyX1exvpeZd_ZPZKzvL9BBnQ";
        Log.d("Resultu", url);
        download.execute(url);
    }

 /*   public void drawList(ArrayList<String> strings) {
        int length = strings.size();
        String[] result_array = new String[length];
        for(int i=0;i<length;i++)
            result_array[i] = strings.get(i);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this ,android.R.layout.simple_list_item_1, result_array);
        resultView.setAdapter(adapter);
    }*/

    //Create custom list view
    public void drawList(ArrayList<ResultPlace> res) {
        resultPlaces = res;
        ResultAdapter adapter = new ResultAdapter(this , R.layout.row , res);
        resultView.setAdapter(adapter);
        resultView.setOnItemClickListener(MainActivity.this);
    }


    //Google place search
    public void selectPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ResultPlace current_result = resultPlaces.get(position);
        double ll[] = new double[2];
        ll[0] = current_result.getLatitude();
        ll[1] = current_result.getLongitude();
        placeName = current_result.getTitle();
        Log.d("Results" , current_result.getLatitude() + " , "+ current_result.getLongitude());
        Toast.makeText(this , current_result.getAddress() , Toast.LENGTH_LONG).show();
        Intent i = new Intent(MainActivity.this , Map.class);
        i.putExtra("selected_place_detail" ,current_result);
        i.putExtra("LatLng" ,  ll);
        i.putExtra("Place_Name" , placeName);
        startActivity(i);
        Log.d("result" , "MainActivity Pass");
    }


 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mapView: Toast.makeText(this , "OKay" , Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this , mapView.class);
                i.putExtra("PlaceResult" , resultPlaces);
                i.putExtra("Latitude" , latlong.latitude);
                i.putExtra("Longitude" , latlong.longitude);
                startActivity(i);
                *//*try {
                    startActivity(i);
                }catch (Exception e)
                {
                    Log.d("Resultss" , e.getMessage());
                }*//*
        }
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                //placeName = place.getName().toString();
                searchPlace.setText(place.getName());
                latlong = place.getLatLng();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Error " , "Failed To Coonect");
    }

    //Check for user location permission
    private void checkForLocationPermission(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }else
            LOCATION_PERMISSION_GURRANTED = true;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                    LOCATION_PERMISSION_GURRANTED = true;
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.near_atm :
                Toast.makeText(this, "yes" , Toast.LENGTH_LONG).show();
                near_by_search(item);
                break;
            case R.id.near_hospital :
                near_by_search(item);
                break;
            case R.id.near_resturant :
                near_by_search(item);
                break;
            case R.id.near_school :
                near_by_search(item);
                break;
            case R.id.near_bank :
                near_by_search(item);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void near_by_search(MenuItem item) {
        if(user_location_address == null)
            Toast.makeText(this , "Failed to find your Location" , Toast.LENGTH_LONG).show();
        else {
            user_current_address = new String(user_location_address.getSubLocality() + "," + user_location_address.getLocality());
            latlong = new LatLng(user_location_address.getLatitude(), user_location_address.getLongitude());
            searchPlace.setText(user_current_address);
            searchTerm.setText(item.getTitle());
            parseResultFromUrl();
        }
    }

}
