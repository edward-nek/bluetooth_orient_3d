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
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeaconInfo extends AppCompatActivity {

    TextView text;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    DBBeacon dbBeacon;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        Button bt_Back = (Button)findViewById(R.id.bt_Back_info);

        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

        Cursor cursor = dbBeacon.getAllData();

        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconInfo.this, BeaconsList.class);
                    stopScanning();
                    startActivity(intent);
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
                if (cursor.getInt(idIndex) == MainActivity.filtrAddressId + 1){
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
