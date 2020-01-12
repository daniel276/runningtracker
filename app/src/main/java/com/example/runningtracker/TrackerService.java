package com.example.runningtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class TrackerService extends Service {

    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<>();

    private final int NOTIFICATION_ID = 001;
    private final String CHANNEL_ID = "100";
    private boolean isRunning = true;
    private boolean isPaused = false;
    private int duration;

    MyLocationListener locationListener = new MyLocationListener();

    protected TrackerCounter trackerCounter;

    protected class TrackerCounter extends Thread implements Runnable{

//        public TrackerCounter(){
//            Log.d(TAG, "TrackerCounter: start");
//
//            LocationManager locationManager =
//                    (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//            MyLocationListener locationListener = new MyLocationListener();
//            try {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                        1, // minimum time interval between updates
//                        2, // minimum distance between updates, in metres
//                        locationListener);
//            } catch(SecurityException e) {
//                Log.d("g53mdp", e.toString());
//            }
//        }

        public void run(){
            Log.d(TAG, "trackerCounter run()");
            while(isRunning){
                try {
                    Thread.sleep(1000);
                    duration++;
                    doCallbacks(duration);
                    Log.d(TAG, "duration inside run "+ duration);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d(TAG, "run: anything");
            }
            Log.d(TAG, "time: "+ duration);
        }
    }

    public void doCallbacks(int count) {
        final int n = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < n; i++) {
            remoteCallbackList.getBroadcastItem(i).callback.counter(count);
        }
        remoteCallbackList.finishBroadcast();
    }

    public void saveTrack(){
        float distance = locationListener.getDistanceWalked();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String formattedDate = dateFormat.format(date);
        float speed = distance / duration;
        Log.d(TAG, "saveTrack formattedDate: "+ formattedDate );

        ContentValues newValues = new ContentValues();
        newValues.put(LogProviderContract.DATE_TIME, formattedDate);
        newValues.put(LogProviderContract.TIME_ELAPSED, duration);
        newValues.put(LogProviderContract.DISTANCE, distance);
        newValues.put(LogProviderContract.AVG_SPEED, speed);
        getContentResolver().insert(LogProviderContract.LOGS_URI, newValues);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TrackerService", "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d("TrackerService", "onCreate");

        super.onCreate();

        trackerCounter = new TrackerCounter();

        //this one works to show speed
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, // minimum time interval between updates
                    1, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new andg not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(TrackerService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent actionIntent = new Intent(TrackerService.this, TrackerService.class);
        PendingIntent pendingActionIntent = PendingIntent.getService(this, 0, actionIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Tracker App")
                .setContentText("Tracker App is running in the background")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Message Service", pendingActionIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        startForeground(NOTIFICATION_ID, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        isRunning = false;
        duration = 0;

        super.onDestroy();
    }

    public class MyBinder extends Binder implements IInterface {
        ICallback callback;

        @Override
        public IBinder asBinder() {
            return this;
        }

        void registerCallback(ICallback callback){
            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);
        }

        void unregisterCallback(ICallback callback){
            remoteCallbackList.unregister(MyBinder.this);
        }

        boolean getStatus(){
            return isRunning;
        }

        void startTrack(){
            Log.d(TAG, "startTrack function called ");
//            TrackerCounter thread = new TrackerCounter();
            locationListener.startTracking();
            if(!isPaused){ // is isPaused == false, start the counter
                trackerCounter.start();
            }

            if(isPaused){ // if isPaused == true, continue the counter
                Log.d(TAG, "isPaused true");
                isRunning = true;
                TrackerCounter counter = new TrackerCounter();
                Thread thread = new Thread(counter);
                thread.start();
            }
        }

        void saveTrack(){
            TrackerService.this.saveTrack();
        }

        public int getDuration(){
            return duration;
        }

        public float getDistance(){
            float x = locationListener.getDistanceWalked();
            Log.d(TAG, "getDistance() " + x);
            return x;
        }

        public void stopTrack(){
            isRunning = false;
            trackerCounter.interrupt();
            locationListener.pauseTracking();
        }

        public void pauseTrack(){
            isPaused = true;
            isRunning = false;
            locationListener.pauseTracking();
//            trackerCounter.interrupt();
        }
    }

}
