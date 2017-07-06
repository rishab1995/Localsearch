package com.gtk.localsearch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LocationListener , NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveListener {


    private EditText searchTerm;
    private TextView searchPlace;
    private String placeName;
    private Button search;
    private ListView resultView;
    private LatLng latlong;
    private GoogleApiClient mGooglePlaceApi , mGoogleLocationApi;
    private ArrayList<ResultPlace> resultPlaces = new ArrayList<>();
    private ActionBar actionBar;
    private Location location;
    private LocationRequest mLocationRequest;
    private Address user_location_address;
    private String user_current_address;
    private int distance=500;
    private SharedPreferences getdistance;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    private LatLng user_current_latlong;
    FrameLayout resilt_frame;
    FragmentManager fm;
    FragmentTransaction fts;
    Fragment detail_fragment;
    private boolean circle;
    private int rad;
    private boolean night_map;
    private MapFragment mapfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForLocationPermission();
        showMap();
        inialize();

        if(Constants.LOCATION_PERMISSION_GURRANTED)
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
                mGoogleLocationApi.connect();
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

    private void showMap() {
        mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.MainmapViewFragment);
        mapfragment.getMapAsync(MainActivity.this);
    }


    //Function for check storage Permission
    private void chekForStoragePermission() {

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} , Constants.PERMISSION_ACCESS_WRITE_STORAGE);
        }else
            Constants.WRITE_STORAGE_PERMISSION_GURRANTED = true;
    }


    //Inialize the variables
    private void inialize() {
        searchTerm = (EditText) findViewById(R.id.searchTerm);
        searchPlace = (TextView) findViewById(R.id.searchPlace);
        search = (Button) findViewById(R.id.search);
        actionBar = getSupportActionBar();
        getdistance = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        distance = Integer.parseInt(getdistance.getString("distance" , "500"));
    }

    //Code for getting Current Location
    private void getCurrentLocation() {
        mGoogleLocationApi = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        Log.d("Something", "Starting connecting Location");
        mGoogleLocationApi.connect();
        Log.d("Something", "Starting connecting Location");
    }

    //To get Current Location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Something" , "Connected Suucessfully");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            goToLocation(user_location_address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGoogleLocationApi.disconnect();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
    //End of Code for Getting Current Location

    @Override
    protected void onPostResume() {
        super.onPostResume();
        distance = Integer.parseInt(getdistance.getString("distance" , "500"));
        mapfragment.getMapAsync(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapfragment.getMapAsync(MainActivity.this);
    }

    //Action perform when search click
    public void Search(View view) {
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

    }

    //Get the search result from url
    private void parseResultFromUrl() {

        String term = searchTerm.getText().toString();
        term = term.toLowerCase();
       // Toast.makeText(this, term , Toast.LENGTH_LONG).show();
        Downloader download = new Downloader(this);
        String url;
        if(Constants.Item_selected_from_menu) {
            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlong.latitude + "," + latlong.longitude + "&radius=" + distance + "&types=" + term + "&key=AIzaSyDchqRHH-7UyX1exvpeZd_ZPZKzvL9BBnQ";
            Constants.Item_selected_from_menu = false;
        }else{
            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlong.latitude + "," + latlong.longitude + "&radius=" + distance + "&types=" + term + "&keyword="+ term+ "&key=AIzaSyDchqRHH-7UyX1exvpeZd_ZPZKzvL9BBnQ";
            Constants.Item_selected_from_menu = false;
        }
        Log.d("Resultu", url);
        download.execute(url);
    }

    //Create custom list view
    public void drawList(ArrayList<ResultPlace> res) {
        resultPlaces = res;
        Log.d("resultt" , res.toString());
        if (res.isEmpty()) {
            Toast.makeText(MainActivity.this, "No Result Found", Toast.LENGTH_LONG).show();
            googleMap.clear();
            markers.clear();
            googleMap.addMarker(new MarkerOptions().position(user_current_latlong).title("You are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlong , 15);
            googleMap.animateCamera(cu);
        }else {
            setResultListFragement(res);
            show_result_on_map(res);
        }
    }


    //Google place search
    public void selectPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    //Action Perform when one of the item from the result is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ResultPlace current_result = resultPlaces.get(position);
        double ll[] = new double[2];
        ll[0] = current_result.getLatitude();
        ll[1] = current_result.getLongitude();
        placeName = current_result.getTitle();
        Log.d("Results" , current_result.getLatitude() + " , "+ current_result.getLongitude());
       // Toast.makeText(this , current_result.getAddress() , Toast.LENGTH_LONG).show();
        Intent i = new Intent(MainActivity.this , Map.class);
        i.putExtra("selected_place_detail" ,current_result);
        i.putExtra("LatLng" ,  ll);
        i.putExtra("Place_Name" , placeName);
        i.putExtra("Latitude" , latlong.latitude);
        i.putExtra("Longitude" , latlong.longitude);
        startActivity(i);
        Log.d("result" , "MainActivity Pass");
    }

    //Action Performed when new activity is open
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(Constants.TAG, "Place: " + place.getName());
                //placeName = place.getName().toString();
                searchPlace.setText(place.getName());
                latlong = place.getLatLng();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(Constants.TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }else if(requestCode == Constants.MORE_MENU_OPTION_LIST){
            if (resultCode == RESULT_OK) {
               near_by_search(data.getStringExtra("TERM"));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(Constants.TAG, status.getStatusMessage());

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
                    Constants.PERMISSION_ACCESS_COARSE_LOCATION);
        }else
            Constants.LOCATION_PERMISSION_GURRANTED = true;
    }


    //Asking for Storage and location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                    Constants.LOCATION_PERMISSION_GURRANTED = true;
                    chekForStoragePermission();
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
            case Constants.PERMISSION_ACCESS_WRITE_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Constants.WRITE_STORAGE_PERMISSION_GURRANTED = true;
                    getCurrentLocation();
                }
                else
                    Toast.makeText(this, "Need to Write Storage!", Toast.LENGTH_SHORT).show();
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


    //Action perform when navigation item is clicked
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.near_atm :
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
            case R.id.near_more: Intent menu_intent = new Intent(MainActivity.this , MoreNearPlaces.class);
                startActivityForResult(menu_intent ,Constants.MORE_MENU_OPTION_LIST);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Search perform from Menu Items
    private void near_by_search(MenuItem item) {
        if(user_location_address == null)
            Toast.makeText(this , "Failed to find your Location" , Toast.LENGTH_LONG).show();
        else {
            user_current_address = new String(user_location_address.getSubLocality() + "," + user_location_address.getLocality());
            latlong = new LatLng(user_location_address.getLatitude(), user_location_address.getLongitude());
            searchPlace.setText(user_current_address);
            searchTerm.setText(item.getTitle());
            Constants.Item_selected_from_menu = true;
            parseResultFromUrl();
        }
    }


    //Search perform from input field
   public void near_by_search(String title) {
        if(user_location_address == null)
            Toast.makeText(this , "Failed to find your Location" , Toast.LENGTH_LONG).show();
        else {
            user_current_address = new String(user_location_address.getSubLocality() + "," + user_location_address.getLocality());
            latlong = new LatLng(user_location_address.getLatitude(), user_location_address.getLongitude());
            searchPlace.setText(user_current_address);
            searchTerm.setText(title);
            parseResultFromUrl();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings: Intent i = new Intent(MainActivity.this , PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.list_show : showFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    //When Mao is Created
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap= googleMap;
        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);
        SharedPreferences getMapView = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String map_view_option = getMapView.getString("map_view", "Normal");
        rad = Integer.parseInt(getMapView.getString("distance" , "500"))+ 10;
        circle = getMapView.getBoolean("circle" ,  true);
        night_map = getMapView.getBoolean("night_view" ,  false);
        switch (map_view_option)
        {
            case "Normal" : googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Hybrid" : googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Satellite": googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Terrain" : googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        if (night_map)
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this , R.raw.map_night_mode));

    }

    //Move yo the Particular location
    private void goToLocation(Address user_address)
    {
        double lat = user_address.getLatitude();
        double lon = user_address.getLongitude();
        user_current_latlong = new LatLng(lat , lon);
        googleMap.addMarker(new MarkerOptions().position(user_current_latlong).title("You are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat , lon) , 15);
        googleMap.animateCamera(cu);
    }

    //To display the result on Map
    private void show_result_on_map(ArrayList<ResultPlace> resultPlaces)
    {
        googleMap.clear();
        markers.clear();
        Marker marker;
        googleMap.addMarker(new MarkerOptions().position(latlong).title("You are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        for(ResultPlace rp : resultPlaces) {
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(rp.getLatitude() ,  rp.getLongitude()))
                    .title(rp.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            markers.add(marker);
        }
        if(circle) {
            CircleOptions co = new CircleOptions().center(latlong).radius(rad).fillColor(0x10FF0000).strokeWidth(2);
            googleMap.addCircle(co);
        }

        googleMap.setOnMapClickListener(MainActivity.this);
        googleMap.setOnCameraMoveListener(MainActivity.this);

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlong , 15);
        googleMap.animateCamera(cu);
    }

    //To display the result on fragment
    private void setResultListFragement(ArrayList<ResultPlace> resultplace){
        fm = getSupportFragmentManager();
        fts = fm.beginTransaction();
        Bundle results_bundle = new Bundle();
        results_bundle.putSerializable("RESULT_PLACE" , resultplace);
        detail_fragment = new ResultListFragment();
        detail_fragment.setArguments(results_bundle);
        resilt_frame= (FrameLayout) findViewById(R.id.result_frame);
        fts.add(resilt_frame.getId() , detail_fragment).commit();
        resilt_frame.animate().setDuration(700).translationY(2000);
    }

    //To show or Hide the fragment
    private void showFragment() {
        if(Constants.fragment_visible){
            resilt_frame.animate().setDuration(700).translationY(2000);
            Constants.fragment_visible = false;
        }else {
            resilt_frame.animate().setDuration(700).translationY(0);
            Constants.fragment_visible = true;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(Constants.fragment_visible){
            resilt_frame.animate().setDuration(700).translationY(2000);
            Constants.fragment_visible = false;
        }
    }

    @Override
    public void onCameraMove() {
        if(Constants.fragment_visible){
            resilt_frame.animate().setDuration(700).translationY(2000);
            Constants.fragment_visible = false;
        }
    }
}
