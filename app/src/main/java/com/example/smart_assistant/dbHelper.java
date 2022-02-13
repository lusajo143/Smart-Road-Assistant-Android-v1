package com.example.smart_assistant;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class dbHelper extends SQLiteOpenHelper {
    private final Context context;

    public dbHelper(Context context) {
        super(context, "smart", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists user (username varchar(100) not null, type varchar(10) not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertUser(String username, String type) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("create table if not exists user (username varchar(100) not null, type varchar(10) not null)");
        database.execSQL("insert into user values ('" + username + "', '" + type + "')");
    }

    public JSONObject getData() throws JSONException {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("create table if not exists user (username varchar(100) not null, type varchar(10) not null)");
        Cursor cursor = database.rawQuery("select * from user where 1", null);
        JSONObject object = new JSONObject();
        if (cursor.moveToFirst()) {
            object.put("username", cursor.getString(0));
            object.put("role", cursor.getString(1));
            object.put("found", true);
        } else {
            object.put("found", false);
        }
        return object;
    }

    public boolean logout() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("drop table if exists user;");
        database.execSQL("create table if not exists user (username varchar(100) not null, type varchar(10) not null)");
        return true;
    }
}
