package com.gtk.localsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class MoreNearPlaces extends AppCompatActivity {

    String[] list_name = {"Airpot" ,"ATM" , "Bakery" ,"Bank" , "Doctor" , "Gym" , "Hospital" , "Restaurant", "School","Railway" ,
                        "Spa" , "Chemist", "Saloon" , "Bar" , "Club" , "Mall" , "Store" , "Staitonary"};
    int[] imageid = {R.drawable.ic_airport_menu , R.drawable.ic_atm_menu , R.drawable.ic_bakery_menu , R.drawable.ic_bank_menu,
                    R.drawable.ic_doctor_menu , R.drawable.ic_gym_menu , R.drawable.ic_hospital_menu, R.drawable.ic_restaurant_menu,
                    R.drawable.ic_school_menu , R.drawable.ic_train_station_menu , R.drawable.ic_spa_menu , R.drawable.ic_chemist_menu,
                    R.drawable.ic_unisex_menu , R.drawable.ic_bar_menu , R.drawable.ic_night_club_menu , R.drawable.ic_shopping_mall_menu,
                    R.drawable.ic_department_store_menu , R.drawable.ic_staitionay_menu};
    GridView grid;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_near_places);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        CustomGrid adapter = new CustomGrid(MoreNearPlaces.this, list_name, imageid);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(MoreNearPlaces.this, "You Clicked at " +list_name[position], Toast.LENGTH_SHORT).show();
                String term = new String(list_name[position]);
                Intent i = new Intent();
                i.putExtra("TERM" , term);
                setResult(Activity.RESULT_OK , i);
                finish();
            }
        });
    }
}
