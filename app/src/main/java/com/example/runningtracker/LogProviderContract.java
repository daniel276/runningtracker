package com.example.runningtracker;

import android.net.Uri;

import java.net.URI;

public class LogProviderContract {

    public static final String AUTHORITY = "com.example.runningtracker.ContentProviderLogger";
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");
    public static final Uri LOGS_URI = Uri.parse("content://"+AUTHORITY+"/logs");

    public static final String ID = "_id";

    public static final String LOG_NAME = "name";
    public static final String DATE_TIME = "date_time";
    public static final String TIME_ELAPSED = "time_elapsed";
    public static final String AVG_SPEED = "avg_speed";
    public static final String DISTANCE = "distance";
    public static final String RATING = "rating";
    public static final String COMMENT = "comment";


}
