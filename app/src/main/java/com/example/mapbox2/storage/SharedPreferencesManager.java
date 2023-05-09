package com.example.mapbox2.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mapbox2.models.Driver;
import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.models.School;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static SharedPreferencesManager mInstance;
    private Context context;
    private static final String SHARED_PREFERENCES_NAME = "shared_preferences";

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesManager(context);
        }
        return mInstance;
    }
    public void saveUser(Driver driver) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", driver.getId());
        editor.putString("email", driver.getEmail());
        editor.putString("name", driver.getName());
        editor.putInt("tripId",  driver.getTripId());
        editor.putString("mobileNumber", driver.getMobileNumber());
        editor.putString("licenseNumber", driver.getLicenseNumber());
        editor.putInt("schoolId", driver.getSchoolId());
        if (driver.getImagePath() != null) {
            editor.putString("image_path", driver.getImagePath());
        }
        editor.apply();
    }

    public Driver getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        Driver driver = new Driver(
                sharedPreferences.getInt("schoolId", -1),
                sharedPreferences.getString("mobileNumber", null),
                sharedPreferences.getString("name", null),
                sharedPreferences.getInt("tripId", -1),
                sharedPreferences.getString("licenseNumber", null),
                sharedPreferences.getInt("id", -1),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("image_path", null)
        );
        return driver;
    }

    public void arrivedAt(StringBuilder parent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parent_ID", String.valueOf(parent));
        editor.apply();
    }

    public String getParentsID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getString("parent_ID", null);
    }



    public void toSchool(boolean toSchoolDestination)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("toSchool",toSchoolDestination);
        editor.apply();
    }

    public Boolean getToSchool()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getBoolean("toSchool",false);
    }

    public void startTrip(boolean toSchoolDestination)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tripStart",toSchoolDestination);
        editor.apply();
    }

    public Boolean isTripStarted()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getBoolean("tripStart",false);
    }




    public List<FathersItem> getWayPointsOfStartTrip(){
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
            Gson gson = new Gson();
            String json = sharedPreferences.getString("WayPoints", null);
            Type type = new TypeToken<ArrayList<FathersItem>>() {}.getType();
            List<FathersItem>  mExampleList = gson.fromJson(json, type);
            return mExampleList;

    }
    public void saveWayPointsOfStartTrip(List<FathersItem> list){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("WayPoints", json);
        editor.apply();

    }


    public List<FathersItem> getWayPointsOfNavigateBack(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        Gson gson = new Gson();
        String json = sharedPreferences.getString("WayPoints_NavigateBack", null);
        Type type = new TypeToken<ArrayList<FathersItem>>() {}.getType();
        List<FathersItem>  mExampleList = gson.fromJson(json, type);
        return mExampleList;

    }
    public void saveWayPointsOfNavigateBack(List<FathersItem> list){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("WayPoints_NavigateBack", json);
        editor.apply();

    }





    public void saveSchool(School school) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(school);
        editor.putString("school", json);
        editor.apply();
    }



    public School getSchool() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        Gson gson = new Gson();
        String json = sharedPreferences.getString("school", null);
        School obj = gson.fromJson(json, School.class);
        return obj;
    }

    public void saveTripIdOfStartTrip(int tripId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tripId", tripId);
        editor.apply();
    }

    public int getTripIdOfStartTrip() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getInt("tripId", -1);
    }

    public void saveTripIdOfNavigateBack(int tripId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tripId_NavigateBack", tripId);
        editor.apply();
    }

    public int getTripIdOfNavigateBack() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getInt("tripId_NavigateBack", -1);
    }

    public void clearTripInfo() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tripId", -1);
        editor.putString("school", null);
        editor.apply();
    }

    public void clearInfoOfNavigateBack() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tripId_NavigateBack", -1);
        editor.apply();
    }


    public void saveToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }



    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        return sharedPreferences.getString("token", null);
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        if (sharedPreferences.getInt("id", -1) != -1) {
            return true;
        }
        return false;
    }


    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);//MODE_PRIVATE keeps the files private and secures the user’s data.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
