package ru.nekrasoved.naviblue;

import java.util.ArrayList;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseBeacon {

//    public String name; //имя устройства
//    public String address; //адрес устройства
//    public Integer pos_x; //координата X
//    public Integer pos_y; //координата Y
//    public Integer pos_z; //координата Z

    public ArrayList<String> name = new ArrayList<String>(); //имя устройства
    public ArrayList<String> address = new ArrayList<String>(); //адрес устройства
    public ArrayList<Integer> pos_x = new ArrayList<Integer>(); //координата X
    public ArrayList<Integer> pos_y = new ArrayList<Integer>(); //координата Y
    public ArrayList<Integer> pos_z = new ArrayList<Integer>(); //координата Z

}
