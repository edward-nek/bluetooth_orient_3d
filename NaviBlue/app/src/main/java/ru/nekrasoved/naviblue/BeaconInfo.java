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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeaconInfo extends AppCompatActivity {

    TextView text;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;

    DBBeacon dbBeacon;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

        final Cursor cursor = dbBeacon.getAllData();

        Button bt_Back = (Button)findViewById(R.id.bt_Back_info);

        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconInfo.this, BeaconsList.class);
                    stopScanning();
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

        //удаление данных о маячке из БД

        Button bt_Delete = (Button)findViewById(R.id.bt_deleteBeacon);
        bt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    dbBeacon.delRec(MainActivity.filtrAddressId);

                    Intent intent = new Intent(BeaconInfo.this, BeaconsList.class);
                    stopScanning();
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBBeacon.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
            int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
            int xIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_X);
            int yIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Y);
            int zIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Z);

            int i = 0;

            do {
                if (cursor.getInt(idIndex) == MainActivity.filtrAddressId){
                    //set name beacon
                    text = (TextView) findViewById(R.id.tvNameBeacon);
                    text.setText(cursor.getString(nameIndex));


                    //set address beacon
                    text = (TextView) findViewById(R.id.tvAddressBeacon);
                    text.setText(cursor.getString(addressIndex));

                    //set X beacon
                    text = (TextView) findViewById(R.id.tvXBeacon);
                    text.setText("X = " + cursor.getInt(xIndex));

                    //set Y beacon
                    text = (TextView) findViewById(R.id.tvYBeacon);
                    text.setText("Y = " + cursor.getInt(yIndex));

                    //set Z beacon
                    text = (TextView) findViewById(R.id.tvZBeacon);
                    text.setText("Z = " + cursor.getInt(zIndex));
                }
            } while (cursor.moveToNext());
        }


//        //set name beacon
//        text = (TextView) findViewById(R.id.tvNameBeacon);
//        text.setText(MainActivity.mBaseBeacon.name.get(MainActivity.filtrAddressId));
//
//
//        //set address beacon
//        text = (TextView) findViewById(R.id.tvAddressBeacon);
//        text.setText(MainActivity.mBaseBeacon.address.get(MainActivity.filtrAddressId));
//
//        //set X beacon
//        text = (TextView) findViewById(R.id.tvXBeacon);
//        text.setText("X = " + MainActivity.mBaseBeacon.pos_x.get(MainActivity.filtrAddressId));
//
//        //set Y beacon
//        text = (TextView) findViewById(R.id.tvYBeacon);
//        text.setText("Y = " + MainActivity.mBaseBeacon.pos_y.get(MainActivity.filtrAddressId));
//
//        //set Z beacon
//        text = (TextView) findViewById(R.id.tvZBeacon);
//        text.setText("Z = " + MainActivity.mBaseBeacon.pos_z.get(MainActivity.filtrAddressId));

        //scanning beacon
//        AsyncTask.execute(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void run() {
//                btScanner.startScan();
//            }
//        });
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        startScanning();
    }


    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //set Rssi beacon
            if (result.getDevice().getAddress().equals(MainActivity.filtrAddress)){
                text = (TextView) findViewById(R.id.tvRssiBeacon);
                text.setText("rssi = " + result.getRssi());
            }
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

}
