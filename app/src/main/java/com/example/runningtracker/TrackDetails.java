package com.example.runningtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TrackDetails extends AppCompatActivity {

    int RECIPE_ID;

    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText nameField;
    TextView dateTime;
    TextView durationView;
    TextView distanceView;
    TextView speedView;
    EditText commentField;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        Bundle bundle = getIntent().getExtras();
        long getId = bundle.getLong("id");
        int logId = (int) getId;

        if(logId != 0){
            this.RECIPE_ID = logId;
        }

        loadData(RECIPE_ID);

    }

    public void onClickSaveButton(View v){

        nameField = findViewById(R.id.nameField);
        commentField = findViewById(R.id.commentField);
        ratingBar = findViewById(R.id.ratingBar);

        dbHelper = new DBHelper(this.getContext());


        String logID = String.valueOf(RECIPE_ID);
        String selectClause = LogProviderContract.ID + ' = ?";
        String[] selectArgs = { logID };

        String name = nameField.getText().toString();
        String comment = commentField.getText().toString();
        int rating = ((int) ratingBar.getRating());

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(LogProviderContract.LOG_NAME, name);
        updatedValues.put(LogProviderContract.COMMENT, comment);
        updatedValues.put(LogProviderContract.RATING, rating);

        getContentResolver().update(LogProviderContract.LOGS_URI, updatedValues, selectClause, selectArgs);
        finish();

    }

    public void loadData(int logId){

        nameField = findViewById(R.id.nameField);
        dateTime = findViewById(R.id.datetimeTextView);
        durationView = findViewById(R.id.durationView);
        distanceView = findViewById(R.id.distanceView);
        speedView = findViewById(R.id.speedView);
        commentField = findViewById(R.id.commentField);
        ratingBar = findViewById(R.id.ratingBar);

        String id = String.valueOf(logId);
        String selectClause = LogProviderContract.ID + " = ?";
        String[] selectionArgs = { id };

        String[] projection = new String[]{
                LogProviderContract.ID,
                LogProviderContract.LOG_NAME,
                LogProviderContract.DATE_TIME,
                LogProviderContract.TIME_ELAPSED,
                LogProviderContract.DISTANCE,
                LogProviderContract.AVG_SPEED,
                LogProviderContract.COMMENT,
                LogProviderContract.RATING
        };

        Cursor c = getContentResolver().query(LogProviderContract.LOGS_URI, projection, selectClause, selectionArgs, null);

        int idIndex = c.getColumnIndex(LogProviderContract.ID);
        int nameIndex= c.getColumnIndex(LogProviderContract.LOG_NAME);
        int dateTimeIndex = c.getColumnIndex(LogProviderContract.DATE_TIME);
        int durationIndex = c.getColumnIndex(LogProviderContract.TIME_ELAPSED);
        int distanceIndex = c.getColumnIndex(LogProviderContract.DISTANCE);
        int speedIndex = c.getColumnIndex(LogProviderContract.AVG_SPEED);
        int commentIndex = c.getColumnIndex(LogProviderContract.COMMENT);
        int ratingIndex = c.getColumnIndex(LogProviderContract.RATING);

        if(c == null){
            Log.d("cursor null", "null cursor pointer");
        }else if(c.getCount() < 1){
            Log.d("not found", "no matches found");
        }else{
            while (c.moveToNext()){
                int log_id = c.getInt(idIndex);
                String log_name = c.getString(nameIndex);
                String log_datetime = c.getString(dateTimeIndex);
                int log_duration = c.getInt(durationIndex);
                float log_distance = c.getInt(distanceIndex);
                Log.d("OK", "loadData: " + log_distance);
                float log_speed = c.getInt(speedIndex);
                String log_comment = c.getString(commentIndex);
                int log_rating = c.getInt(ratingIndex);

                nameField.setText(log_name);
                dateTime.setText(log_datetime);
                durationView.setText(durationFormatter(log_duration));
                distanceView.setText(String.format(Locale.getDefault(), "%.2f m", log_distance));
                speedView.setText(String.format(Locale.getDefault(), "%.2f m/s", log_speed));
                commentField.setText(log_comment);
                ratingBar.setRating(log_rating);

            }
        }
    }

    String durationFormatter(int time){
        long hour = (long) time / 3600;
        long minutes = (long) (time % 3600) / 60;
        long seconds = (long) time % 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.HOURS.toHours(hour), TimeUnit.MINUTES.toMinutes(minutes), TimeUnit.SECONDS.toSeconds(seconds));
    }

}
