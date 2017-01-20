package com.gtk.localsearch;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by RISHAB on 21-12-2016.
 */
public class ResultPlace implements Serializable {
    private String title;
    private String address;
    private double latitude;
    private  double longitude;

    public ResultPlace(String name, String address , double latitude , double longitude) {
        this.title = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {

        return title;
    }
}
