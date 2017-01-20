package com.gtk.localsearch;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class place_detail_fragment extends Fragment {


    TextView name;
    TextView address;
    TextView latitude;
    TextView longitude;
    ResultPlace selected_result;
    public place_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_place_detail_fragment, container, false);
        selected_result = (ResultPlace) getArguments().getSerializable("selected_place");
        initialize(v);
        setValues();
        return v;
    }

    private void setValues() {
        name.setText(selected_result.getTitle());
        address.setText(selected_result.getAddress());
        latitude.setText("Latitude : "+selected_result.getLatitude());
        longitude.setText("Longitude : "+selected_result.getLongitude());
    }

    private void initialize(View v) {
        name = (TextView) v.findViewById(R.id.selected_place_name);
        address = (TextView) v.findViewById(R.id.selected_place_address);
        latitude = (TextView) v.findViewById(R.id.selected_place_latitude);
        longitude = (TextView) v.findViewById(R.id.selected_place_longitude);
    }

}
