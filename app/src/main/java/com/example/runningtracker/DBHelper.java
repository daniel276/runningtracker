package com.example.runningtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context){
        super(context, "myDB", null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE logs (" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(128), " +
                "date_time DATETIME NOT NULL," +
                "time_elapsed BIGINT NOT NULL," +
                "avg_speed DOUBLE(10,2) NOT NULL," +
                "distance DOUBLE(10,2) NOT NULL," +
                "rating INTEGER, " +
                "comment VARCHAR(256)"
                + ");" );

//        db.execSQL("CREATE TABLE recipes (" +
//                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
//                "name VARCHAR(128) NOT NULL," +
//                "instructions VARCHAR(128) NOT NULL," +
//                "rating INTEGER" +
//                ");");
//
//        db.execSQL("CREATE TABLE ingredients (" +
//                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
//                "ingredientName VARCHAR(128) NOT NULL" +
//                ");");
//
//        db.execSQL("CREATE TABLE recipe_ingredients (" +
//                "recipe_id INT NOT NULL, " +
//                "ingredient_id INT NOT NULL," +
//                "CONSTRAINT fk1 FOREIGN KEY (recipe_id) REFERENCES recipes (_id)," +
//                "CONSTRAINT fk2 FOREIGN KEY (ingredient_id) REFERENCES ingredients (_id)," +
//                "CONSTRAINT _id PRIMARY KEY (recipe_id, ingredient_id)" +
//                ");");

        db.execSQL("INSERT INTO logs (name, date_time, time_elapsed, avg_speed, distance, rating, comment) VALUES ('morning run', '2020-01-12 09:00:00', 1500, 1.20, 470.2, 3, 'weather was good')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
