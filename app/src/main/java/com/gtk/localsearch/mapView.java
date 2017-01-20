package com.gtk.localsearch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class mapView extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    GoogleMap googleMap;
    ArrayList<ResultPlace> resultPlaces = new ArrayList<>();
    LatLng currentPosition;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Map View");
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //actionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);

        getDataFromParentActivity();

        MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapViewFragment);
        mapfragment.getMapAsync(mapView.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_view_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       this.googleMap= googleMap;
        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);
      /*  googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));*/
       for(ResultPlace rp : resultPlaces) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(rp.getLatitude() ,  rp.getLongitude()))
                    .title(rp.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        }
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(currentPosition , 15);
        googleMap.animateCamera(cu);

        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveListener(this);
    }


    private void getDataFromParentActivity() {
        Intent i = getIntent();
        resultPlaces = (ArrayList<ResultPlace>) i.getSerializableExtra("PlaceResult");
        currentPosition = new LatLng(i.getDoubleExtra("Latitude", 0) , i.getDoubleExtra("Longitude" , 0));
        if(resultPlaces!=null)
            Toast.makeText(this, "Done" , Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Toast.makeText(this, "Great" , Toast.LENGTH_SHORT).show();
                this.finish();
                return true;
            case R.id.mapTypeNormal : googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeHybrid : googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.mapTypeSatellite: googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain : googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraIdle() {
        actionBar.show();
    }

    @Override
    public void onCameraMove() {
        actionBar.hide();
    }
}
