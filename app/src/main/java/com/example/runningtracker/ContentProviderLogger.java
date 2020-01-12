package com.example.runningtracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentProviderLogger extends ContentProvider {

    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LogProviderContract.AUTHORITY, "logs", 1);
        uriMatcher.addURI(LogProviderContract.AUTHORITY, "logs/#", 2);
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case 1:
                return db.query("logs", projection, selection, selectionArgs, null, null, sortOrder);
            case 2:
                return db.query("logs", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (uri.getLastPathSegment()==null){
            return "vnd.android.cursor.dir/ContentProviderRecipe.data.text";
        }else{
            return "vnd.android.cursor.item/ContentProviderRecipe.data.text";
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch (uriMatcher.match(uri)){
            case 1:
                tableName = "logs";
                break;
            case 2:
                tableName = "logs";
                break;
            default:
                tableName = "logs";
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri,id);
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case 1:
                count = db.delete("logs" +
                        "", selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updateCount = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case 1:
                updateCount = db.update("logs", values, selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

}
