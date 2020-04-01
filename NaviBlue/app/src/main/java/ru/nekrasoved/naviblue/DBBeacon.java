package ru.nekrasoved.naviblue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBBeacon{

    public static final int DATABASE_VERSION = 2;
    public static final String  DATABASE_NAME = "beaconDb";
    public static final String  TABLE_BEACONS = "beacons";

    public static final String  KEY_ID = "_id";
    public static final String  KEY_NAME = "name";
    public static final String  KEY_ADDRESS = "address";
    public static final String  KEY_POS_X = "posX";
    public static final String  KEY_POS_Y = "posY";
    public static final String  KEY_POS_Z = "posZ";

    private static final String DB_CREATE =
            "create table " + TABLE_BEACONS + " (" + KEY_ID + " integer primary key," +
                    KEY_NAME + " text," + KEY_ADDRESS + " text," + KEY_POS_X + " integer," +
                    KEY_POS_Y + " integer," + KEY_POS_Z + " integer" + ");";

    private final Context cont;

    private DBHelper helper;
    private SQLiteDatabase database;


    public DBBeacon(Context context) {
        cont = context;
    }

    // открыть подключение
    public void open() {
        helper = new DBHelper(cont, DATABASE_NAME, null, DATABASE_VERSION);
        database = helper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (helper!=null) helper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return database.query(TABLE_BEACONS, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String name, String address, Integer x, Integer y, Integer z) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_POS_X, x);
        cv.put(KEY_POS_Y, y);
        cv.put(KEY_POS_Z, z);

        database.insert(TABLE_BEACONS, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        database.delete(TABLE_BEACONS, KEY_ID + " = " + id, null);
    }


    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_BEACONS);
            onCreate(db);
        }
    }
}
