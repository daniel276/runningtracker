package com.example.runningtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TrackHistory extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;
    SimpleCursorAdapter dataAdapter;

    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_history);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        queryLogs();

        getContentResolver().registerContentObserver(LogProviderContract.LOGS_URI, true, new ChangeObserver(h));

    }

    void queryLogs(){
        String[] projection = new String[]{
                LogProviderContract.ID,
                LogProviderContract.DATE_TIME,
                LogProviderContract.DISTANCE
        };

        String[] cols = new String[]{
                LogProviderContract.DATE_TIME,
                LogProviderContract.DISTANCE
        };

        int[] to = new int[]{
                R.id.dateTimeView,
                R.id.distanceView
        };


        Cursor cursor = getContentResolver().query(LogProviderContract.LOGS_URI, projection, null, null, null);
        dataAdapter = new SimpleCursorAdapter(this, R.layout.item_row, cursor, cols, to, 0);

        ListView listView = findViewById(R.id.logList);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TrackDetails.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

    }

    class ChangeObserver extends ContentObserver {

        public ChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryLogs();
        }
    }


}
