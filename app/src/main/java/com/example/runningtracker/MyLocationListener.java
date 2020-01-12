package com.example.runningtracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyLocationListener implements LocationListener {

    private boolean isRunning;
    private ArrayList<Location> locations = new ArrayList<>();

    @Override
    public void onLocationChanged(Location location) {
        Log.d("g53mdp onChange", location.getLatitude() + " " + location.getLongitude());
        Log.d(TAG, "locations" + locations.size());

        if(isRunning){
            locations.add(location);
        }
    }

    public float getDistanceWalked(){
//        Log.d(TAG, "getDistanceWalked locations" + locations);
        if(locations.size() <= 1){
            return 0;
        }else {
            return locations.get(0).distanceTo(locations.get(locations.size() - 1));
        }
    }

    public void startTracking(){
        isRunning = true;
    }

    public void pauseTracking(){
        isRunning = false;
    }

    public void createNewRecord(){
        locations = new ArrayList<>();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
         // the user enabled (for example) the GPS
        Log.d("g53mdp", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //user disabled gps
        Log.d("g53mdp", "onProviderDisabled: " + provider);
    }
}
