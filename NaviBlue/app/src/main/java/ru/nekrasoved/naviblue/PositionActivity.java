package ru.nekrasoved.naviblue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PositionActivity extends AppCompatActivity {

    ListView listBeacon;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Button btBack; //кнопка назад
    Button btFiltr; //кнопка фильтра по кол-ву маяков

    EditText etFiltr;

    DBBeacon dbBeacon; //База данных маячков



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
                    Log.d("Logm", "--------BY-------");
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
                    filtr();
                }catch (Exception e){

                }
            }
        });

        etFiltr = (EditText) findViewById(R.id.et_pos_Filtr);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        startScanning();
        //onClick(btFiltr);
    }

    public void filtr() {

        stopScanning();
        dbBeacon.open();
        Integer Filtr = new Integer(etFiltr.getText().toString()); //введенное количество маяков для фильтра

        Cursor cursor = null; //вывод


        //переменные для query
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        //dbBeacon.query(DBBeacon.TABLE_BEACONS, null, );
        Log.d("Logm", " " + dbBeacon.beaconSignal("7C:20:0A:BD:40:91", Filtr));



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
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
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
}
