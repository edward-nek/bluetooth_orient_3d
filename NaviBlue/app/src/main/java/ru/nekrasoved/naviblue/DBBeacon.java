package ru.nekrasoved.naviblue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DBBeacon{

    public static final int DATABASE_VERSION = 10;
    public static final String  DATABASE_NAME = "beaconDb";
    public static final String  TABLE_BEACONS = "beacons";

    public static final String  KEY_ID = "_id";
    public static final String  KEY_NAME = "name";
    public static final String  KEY_ADDRESS = "address";
    public static final String  KEY_POS_X = "posX";
    public static final String  KEY_POS_Y = "posY";
    public static final String  KEY_POS_Z = "posZ";

    public static final String  KEY_N = "n";
    public static final String  KEY_POWER = "power";


    private static final String DB_CREATE =
            "create table " + TABLE_BEACONS + " (" + KEY_ID + " integer primary key autoincrement," +
                    KEY_NAME + " text," + KEY_ADDRESS + " text," + KEY_POS_X + " integer," +
                    KEY_POS_Y + " integer," + KEY_POS_Z + " integer," + KEY_N + " integer," +
                    KEY_POWER + " integer" +");";

    //таблица измерения сигнала маячков
    public static final String TABLE_SIGNAL = "signal";

    public static final String  KEY_SIGNAL_ID = "_id";
    public static final String  KEY_BEACON_ADDRESS = "address";
    public static final String  KEY_SIGNAL_RSSI = "rssi";

    private static final String DB_SIGNAL_CREATE =
            "create table " + TABLE_SIGNAL + " (" + KEY_SIGNAL_ID + " integer primary key," +
                    KEY_BEACON_ADDRESS + " text," + KEY_SIGNAL_RSSI + " integer" + ");";

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

    public Cursor getAllDataSignal() {
        return database.query(TABLE_SIGNAL, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String name, String address, Integer x, Integer y, Integer z, Integer n, Integer power) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_POS_X, x);
        cv.put(KEY_POS_Y, y);
        cv.put(KEY_POS_Z, z);

        cv.put(KEY_N, n);
        cv.put(KEY_POWER, power);

        database.insert(TABLE_BEACONS, null, cv);
    }

    public void addRecSignal(String address, Integer rssi) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_BEACON_ADDRESS, address);
        cv.put(KEY_SIGNAL_RSSI, rssi);

        database.insert(TABLE_SIGNAL, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        database.delete(TABLE_BEACONS, KEY_ID + " = " + id, null);
    }

    public void delRecSignal(long id) {
        database.delete(TABLE_SIGNAL, KEY_ID + " = " + id, null);
    }

    // запрос в БД
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {
        return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //получить средний сигнал маячка по N записей
    public double beaconSignal(String address, int limit) {
        String sqlQuery = "SELECT s.rssi AS rssi " +
                            "FROM " + TABLE_BEACONS + " AS b " +
                            "INNER JOIN " + TABLE_SIGNAL + " AS s " +
                            "ON b." + KEY_ADDRESS + " = " +
                            "s." + KEY_BEACON_ADDRESS + " " +
                            "WHERE b." + KEY_ADDRESS + " = " + '"'+address + '"' + " " +
                            "ORDER BY " + "s." + KEY_SIGNAL_ID + " DESC "+
                            "LIMIT " + limit + ";";
        Cursor c = null;

//        Log.d("Logm", "SQL1");

        c = database.rawQuery(sqlQuery, null);
//        Log.d("Logm", "SQL2");
        double rssiSignal = 0; //уровень сигнала
//        Log.d("Logm", "SQL3");
        if (c.moveToFirst()){
            int rssiIndex = c.getColumnIndex("rssi");
//            Log.d("Logm", "rssi = " + rssiIndex);
            do {
                rssiSignal = rssiSignal + c.getInt(rssiIndex);
            } while (c.moveToNext());
        }
//        Log.d("Logm", "SQL4");
        rssiSignal = rssiSignal / c.getCount();
//        Log.d("Logm", "SQL5");
        return rssiSignal;
    }

    //получить список действующих маяков
    public Cursor beaconList(int limit) {
        String sqlQuery = "SELECT b.name AS name, b.address AS address, b.posX AS posX, " +
                "b.posY AS posY, b.posZ AS posZ, b.n AS n, b.power AS power " +
                "FROM " + TABLE_BEACONS + " AS b " +
                "INNER JOIN " + TABLE_SIGNAL + " AS s " +
                "ON b." + KEY_ADDRESS + " = " +
                "s." + KEY_BEACON_ADDRESS + " " + "GROUP BY "+ "b.address "+
                "ORDER BY " + "s." + KEY_SIGNAL_RSSI + " DESC "+
                "LIMIT " + limit + ";";
        Cursor c = null;

//        Log.d("Logm", "SQL1");

        c = database.rawQuery(sqlQuery, null);
//        Log.d("Logm", "SQL2");
        return c;
    }

    //очистить таблицу
    public void clearSignalTable() {
        database.execSQL("drop table if exists " + TABLE_SIGNAL);
        database.execSQL(DB_SIGNAL_CREATE);
    }


    // класс по созданию и управлению БД
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_SIGNAL_CREATE);
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_BEACONS);
            db.execSQL("drop table if exists " + TABLE_SIGNAL);
            onCreate(db);
        }
    }
}
