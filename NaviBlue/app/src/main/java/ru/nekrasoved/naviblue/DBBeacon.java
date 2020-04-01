package ru.nekrasoved.naviblue;

import java.util.ArrayList;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBBeacon extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String  DATABASE_NAME = "beaconDb";
    public static final String  TABLE_BEACONS = "beacons";

    public static final String  KEY_ID = "_id";
    public static final String  KEY_NAME = "name";
    public static final String  KEY_ADDRESS = "address";
    public static final String  KEY_POS_X = "posX";
    public static final String  KEY_POS_Y = "posY";
    public static final String  KEY_POS_Z = "posZ";


    public DBBeacon(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_BEACONS +
                " (" + KEY_ID + " integer primary key," +
                KEY_NAME + " text," + KEY_ADDRESS + " text," +
                KEY_POS_X + " integer," + KEY_POS_Y + " integer," +
                KEY_POS_Z + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_BEACONS);

        onCreate(db);
    }
}
