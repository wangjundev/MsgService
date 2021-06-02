package com.stv.msgservice.datamodel.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stv.msgservice.third.activity.LocationData;

import androidx.room.TypeConverter;

public class TypeConverterUtils {
    @TypeConverter
    public LocationData fromJson(String json){
        if(json == null){
            return null;
        }
        return new GsonBuilder().setLenient().create().fromJson(json, LocationData.class);
    }

    @TypeConverter
    public String Location2Json(LocationData locationData){
        if(locationData == null){
            return null;
        }
        return new Gson().toJson(locationData);
    }
}
