package com.example.runningtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, // minimum time interval between updates
                    2, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }

//        this.startService(new Intent(MainActivity.this, TrackerService.class));
//        LocationManager locationManager =
//                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        MyLocationListener locationListener = new MyLocationListener();
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    5, // minimum time interval between updates
//                    5, // minimum distance between updates, in metres
//                    locationListener);
//        } catch(SecurityException e) {
//            Log.d("g53mdp", e.toString());
//        }


    }

    public void onClickStartButton(View v){
        Intent intent = new Intent(this, Tracker.class);
        startActivity(intent);
    }

    public void onClickLogsButton(View v){
        Intent intent = new Intent(this, TrackHistory.class);
        startActivity(intent);
    }



}
