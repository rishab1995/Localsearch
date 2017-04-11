package com.gtk.localsearch;


import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,  GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    GoogleMap map;
    LatLng latlng;
    ActionBar actionBar;
    String placeName;
    FrameLayout frame;
    Fragment detail_fragment;
    Boolean fragment_visible = false;
    ResultPlace selected_result;
    FragmentManager fm;
    FragmentTransaction fts;
    float frameHeight=500;
    LatLng currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getDataFromParentActivity();
        Log.d("result" , "Getting Data Successfully");
        setLocationDetailOnFragment();

        //Gesture

        actionBar = getSupportActionBar();
        actionBar.setTitle(placeName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        Toast.makeText(Map.this, "Perfect", Toast.LENGTH_LONG).show();

        MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapfragment.getMapAsync(Map.this);


        //Toast.makeText(this ,""+ll[0]+","+ll[1] , Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getDataFromParentActivity() {
        Intent i = getIntent();
        double ll[] = i.getDoubleArrayExtra("LatLng");
        latlng = new LatLng(ll[0] , ll[1]);
        placeName = i.getStringExtra("Place_Name");
        selected_result = (ResultPlace) i.getSerializableExtra("selected_place_detail");
        currentPosition = new LatLng(i.getDoubleExtra("Latitude", 0) , i.getDoubleExtra("Longitude" , 0));
        if(selected_result==null)
            Log.d("result" , "Result is emppty");
        else
            Log.d("result" , "result present");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map= googleMap;
        map.addMarker(new MarkerOptions()
                .position(latlng)
                .title(placeName));
        map.addMarker((new MarkerOptions()
                .position(currentPosition)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlng , 15);
        map.animateCamera(cu);
        map.setOnMapLongClickListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraIdleListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Toast.makeText(this, "Great" , Toast.LENGTH_SHORT).show();
                this.finish();
                return true;
            case R.id.detail:showFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragment() {
        if(fragment_visible){
            frame.animate().setDuration(500).translationY(frameHeight);
            fragment_visible = false;
        }else {
            frame.animate().setDuration(500).translationY(0);
            fragment_visible = true;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("resultlg" , " yea");
        if (!fragment_visible) {

            fragment_visible=true;
        }else {
            frame.animate().setDuration(500).translationY(frameHeight);
            fragment_visible = false;
        }
        //fts.add(R.id.fragmentDetails , detail_fragment);
    }

    private void setLocationDetailOnFragment()
    {
        fm = getSupportFragmentManager();
        fts = fm.beginTransaction();
        Bundle result_bundle = new Bundle();
        result_bundle.putSerializable("selected_place", selected_result );
        detail_fragment = new place_detail_fragment();
        detail_fragment.setArguments(result_bundle);
        frame = (FrameLayout) findViewById(R.id.frame);
        fts.add(frame.getId() , detail_fragment).commit();
        frame.setTranslationY(frameHeight);
    }

    @Override
    public void onCameraIdle() {
        actionBar.show();
    }

    @Override
    public void onCameraMove() {
        actionBar.hide();
        if(fragment_visible) {
            frame.animate().setDuration(500).translationY(frameHeight);
            fragment_visible = false;
        }
    }
}
