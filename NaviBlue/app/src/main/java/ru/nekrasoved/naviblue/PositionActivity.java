package ru.nekrasoved.naviblue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PositionActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;

    Button btBack; //кнопка назад
    Button btFiltr; //кнопка фильтра по кол-ву маяков

    EditText etFiltr;
    EditText etCount;

    DBBeacon dbBeacon; //База данных маячков

    Integer filtr;
    Integer count;

    Integer countBeacon; //количество видимых маяков

    public static ArrayList <String> filtrBeacons; //фильтрация маячков с расстоянием для вывода



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        dbBeacon = new DBBeacon(this);
        dbBeacon.open();
        dbBeacon.clearSignalTable();
        dbBeacon.close();

        btBack = (Button) findViewById(R.id.bt_pos_Back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    stopScanning();

                    //вывод добавленных сигналов
                    Cursor cursor;
                    dbBeacon.open();
                    cursor = dbBeacon.getAllDataSignal();

                    if (cursor.moveToFirst()) {
                        int idIndex = cursor.getColumnIndex(DBBeacon.KEY_SIGNAL_ID);
                        int signalIndex = cursor.getColumnIndex(DBBeacon.KEY_SIGNAL_RSSI);
                        int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_BEACON_ADDRESS);
                        do {
                            String s;
                            s = "id = "+ cursor.getString(idIndex) + " : rssi = " + cursor.getString(signalIndex) +
                                    " : address = " + cursor.getString(addressIndex) + " ;";
                            Log.d("Logm", s);

                        } while (cursor.moveToNext());
                    }
                    dbBeacon.close();


                    Intent intent = new Intent(PositionActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

        btFiltr = (Button) findViewById(R.id.bt_pos_Filtr);
        btFiltr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //скрыть клавиатуру по нажатию кнопки
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btFiltr.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    filtr();
                }catch (Exception e){

                }
            }
        });

        etFiltr = (EditText) findViewById(R.id.et_pos_Filtr);
        etCount = (EditText) findViewById(R.id.et_pos_Count);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

    }

    public void filtr() {

        stopScanning();
        countBeacon = 0;
        if ((etFiltr.getText().toString().length() > 0)&&(etCount.getText().toString().length() > 0)){
            startScanning();
            dbBeacon.open();
            filtr = new Integer(etFiltr.getText().toString()); //введенное количество маяков для фильтра
            count = new Integer(etCount.getText().toString()); //введенное количество измерений для фильтра

            //dbBeacon.query(DBBeacon.TABLE_BEACONS, null, );
        }
        else{
            Toast.makeText(this,"Для начала заполните поля!", Toast.LENGTH_LONG).show();
        }
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            //проверка на то, добавлен ли данный маяк
            boolean check = false;
            Cursor d;
            dbBeacon.open();
            d = dbBeacon.getAllData();
            if (d.moveToFirst()) {
                int addressIndex = d.getColumnIndex(DBBeacon.KEY_ADDRESS);
                do {
//                    Log.d("Logm", result.getDevice().getAddress() + " :: " + d.getString(addressIndex));
                    if (result.getDevice().getAddress().equals(d.getString(addressIndex))){
                        check = true;
                    }
                } while (d.moveToNext());
            }

            //вносим данные по маячкам
            if (check) {
                dbBeacon.addRecSignal(result.getDevice().getAddress(), result.getRssi());
//                Log.d("Logm",result.getDevice().getAddress() +" : "+ result.getRssi());
            }

            //отображаем маяки
            showBeacons();
        }
    };

    public void startScanning() {
        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    public void showBeacons() {
        dbBeacon.open();
        Cursor cursor = dbBeacon.beaconList(filtr);

        if (countBeacon > count){
            countBeacon = 0;
            filtrBeacons = new ArrayList<String>();

            if (cursor.moveToFirst()){
                int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
                int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
                do {
                    double rssi = dbBeacon.beaconSignal(cursor.getString(addressIndex), count);
                    Log.d("Logm", cursor.getString(nameIndex) + " :: " + cursor.getString(addressIndex) +
                            " : " + rssi);
                    filtrBeacons.add(cursor.getString(nameIndex) + " :: " + cursor.getString(addressIndex) +
                            " : rssi = " + rssi);

                    // адаптер
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filtrBeacons);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    ListView list_beacon = (ListView) findViewById(R.id.list_pos_beacon);
                    list_beacon.setAdapter(adapter);

                } while (cursor.moveToNext());
            }
        }
        else {
            countBeacon ++;
        }
    }
}
