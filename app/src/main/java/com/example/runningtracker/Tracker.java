package com.example.runningtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Tracker extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean isPaused;
    private Button startButton;
    private Button pauseButton;
    private TextView distanceText;
    private TextView speedText;
    private TextView timeText;
    private int counter;

    private TrackerService.MyBinder myService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        distanceText = findViewById(R.id.distance);
        speedText = findViewById(R.id.speed);
        timeText = findViewById(R.id.timeText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);

//        this.startService(new Intent(this, TrackerService.class));
        this.bindService(new Intent(this, TrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("g53mdp", "connected");
            myService = (TrackerService.MyBinder) service;
            myService.registerCallback(callback);

            if(myService != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    while (myService != null){
                        float distance = myService.getDistance();
                        float totalDuration = (float) myService.getDuration();
                        float speed = distance / totalDuration;
                        if(Float.isNaN(speed)){
                            speed = 0;
                        }

                        Log.d("OK", "speed trackr" + speed);
                        Log.d("OK", "this duration: " + totalDuration);
                        String displaySpeed = String.format(Locale.getDefault(), "%.2f m/s", speed);
                        String displayDistance = String.format(Locale.getDefault(), "%.2f m", distance);
                        speedText.setText(displaySpeed);
                        distanceText.setText(displayDistance); // appropriately use String formatting
                        Log.d("OK", "run distance " + distance);

                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                     }
                    }
                }).start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService.unregisterCallback(callback);
            myService = null;
        }
    };

    ICallback callback = new ICallback() {
        @Override
        public void counter(final int counter) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long durationHour = (long) counter / 3600;
                    long durationMinute = (long) (counter % 3600) / 60;
                    long durationSeconds = (long) counter % 60;

                    String timeDisplay = String.format(Locale.getDefault(),"%02d:%02d:%02d", TimeUnit.HOURS.toHours(durationHour), TimeUnit.MINUTES.toMinutes(durationMinute), TimeUnit.SECONDS.toSeconds(durationSeconds));

                    timeText.setText(timeDisplay);
                }
            });
        }
    };

    public void onClickStartButton(View v){
        startButton.setEnabled(false);
        this.startService(new Intent(this, TrackerService.class));
        myService.startTrack();
    }

    public void onClickFinishButton(View v){

        myService.stopTrack();

        myService.saveTrack();
        finish();

    }

    public void onClickPauseButton(View v){
        startButton.setEnabled(true);
        isPaused = true;
        myService.pauseTrack();
    }

    @Override
    protected void onDestroy() {
        if(serviceConnection != null){
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
    }
}
