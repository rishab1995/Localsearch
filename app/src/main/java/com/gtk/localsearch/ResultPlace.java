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
    private String photo_reference;

    public ResultPlace(String name, String address , double latitude , double longitude , String photo_reference) {
        this.title = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_reference = photo_reference;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoto_reference() {
        return photo_reference;
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
