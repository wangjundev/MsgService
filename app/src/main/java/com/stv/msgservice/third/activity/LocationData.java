package com.stv.msgservice.third.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class LocationData implements Serializable {
    private double lat;
    private double lng;
    private String title;
    private String description;
    private byte[] thumbnailData;


    public LocationData(double lat, double lng, String title, String description, Bitmap thumbnail) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
        this.thumbnailData = baos.toByteArray();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getThumbnail() {
        return BitmapFactory.decodeByteArray(thumbnailData, 0, thumbnailData.length);
    }
}
