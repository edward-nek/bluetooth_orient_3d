package ru.nekrasoved.naviblue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;

import android.bluetooth.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


//    private Button statusButton;
    private BluetoothAdapter bluetooth;
//    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
//    private ListView listDevices;
//
    //базы хранения данных о маячках
    public static BaseDevices mBaseDevices = new BaseDevices();
//
//    //public static BaseBeacon mBaseBeacon = new BaseBeacon();
//
//
//    private DeviceListAdapter mDeviceListAdapter;
//    private ProgressDialog mProgressDialog;

    //переменная для фильтра расстояния для beacon = address
    public static String filtrAddress;
    public static Integer filtrAddressId;

    DBBeacon dbBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);


        //скрыть панель навигации начало

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        //скрыть панель навигации конец

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

//        //statusButton = (Button) findViewById(R.id.status_blue);
//        bluetooth = BluetoothAdapter.getDefaultAdapter();
//        if(bluetooth == null){
//            Toast.makeText(this, "Ваше устройство не поддерживает bluetooth!", Toast.LENGTH_LONG).show();
//            finish();
//        }
//
////        if (bluetooth.isEnabled()) {
////                statusButton.setText("On");
////        }
//
//        mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_item, mDevices);

        //Открыть меню

        ImageButton btStart = (ImageButton) findViewById(R.id.bt_start);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, BeaconsList.class);

                    bluetooth = BluetoothAdapter.getDefaultAdapter();
                    if(bluetooth == null){
                        Toast.makeText(MainActivity.this, "Ваше устройство не поддерживает bluetooth!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    bluetooth.enable();

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });


        /*// open Form add beacons

        Button btAddBeacon = (Button)findViewById(R.id.btAddBeacon);

        btAddBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, MainDeviceList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });*/



        /*// open BeaconsList

        Button btBeacons = (Button)findViewById(R.id.btBeacons);

        btBeacons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, BeaconsList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });*/

       /* // open PositionActivity

        Button btPosition = (Button)findViewById(R.id.btPosition);

        btPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });*/

    }

    public void onClick() {
        // во весь экран
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); //спрятать панель навигации
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

//    public void checkBlue(View view) {
//
//        String status;
//        if (bluetooth.isEnabled()) {
//            String mydeviceaddress = bluetooth.getAddress();
//            String mydevicename = bluetooth.getName();
//            status = mydevicename + " : " + mydeviceaddress;
//        }
//        else {
//            status = "Bluetooth отключен";
//        }
//        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
//
//
//    }

//    public void onBlue(View view) {
//        String status;
//        if (bluetooth.isEnabled()) {
//            bluetooth.disable();
//            status = "Bluetooth отключен";
//            Toast.makeText(this, status, Toast.LENGTH_LONG).show();
//            statusButton.setText("Off");
//        }
//        else{
//            bluetooth.enable();
//            status = "Bluetooth включен";
//            Toast.makeText(this, status, Toast.LENGTH_LONG).show();
//            statusButton.setText("On");
//        }
//
//    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void findDevice(View view) throws InterruptedException {
//
//        bluetooth.enable();
//
//        Thread.sleep(400); //для того, чтобы успел включиться блютуз, пауза
//
//
//        checkPermissionLocation();
//
//        if (!bluetooth.isDiscovering()) {
//            bluetooth.startDiscovery();
//        }
//
//        if (bluetooth.isDiscovering()) {
//            bluetooth.cancelDiscovery();
//            bluetooth.startDiscovery();
//        }
//        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
//
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void checkPermissionLocation() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            int check = checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//            check += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//
//            if (check != 0){
//                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1002);
//            }
//        }
//    }
//
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
//                //Toast.makeText(MainActivity.this, "Поиск начался", Toast.LENGTH_LONG).show();
//
//                mProgressDialog = ProgressDialog.show(MainActivity.this, "Поиск устройств", "Пожалуйста подождите");
//
//                mBaseDevices = new BaseDevices();
//            }
//            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
//
//                mProgressDialog.dismiss();
//
//                //Toast.makeText(MainActivity.this, "Поиск завершен", Toast.LENGTH_LONG).show();
//                showListDevices();
//            }
//            if (action.equals(BluetoothDevice.ACTION_FOUND)){
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device != null){
//                    if (!mDevices.contains(device)){
//
//                        //добавляем устройство в базу устройств
//                        if (checkCopy(device.getAddress())) {
//                            mBaseDevices.address.add(device.getAddress());
//
//                            if (device.getName() == null) {
//                                mBaseDevices.name.add("Not Name");
//                                mBaseDevices.spinner_name.add("Not Name : " + device.getAddress()); //костыль
//                            } else {
//                                mBaseDevices.name.add(device.getName());
//                                mBaseDevices.spinner_name.add(device.getName() + " : " + device.getAddress()); //костыль
//                            }
//
//                            mDeviceListAdapter.add(device);
//                        }
//                    }
//                }
//            }
//        }
//    };
//
//    //проверка на копии
//
//    private Boolean checkCopy(String address) {
//        Boolean check = true;
//        Cursor cursor = dbBeacon.getAllData();
//
//        if (cursor.moveToFirst()) {
//            int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
//            do {
//               if (cursor.getString(addressIndex).equals(address)){
//                   check = false;
//               }
//            } while (cursor.moveToNext());
//        }
//        return check;
//    }
//
//    private void showListDevices() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Найденные устройства: ");
//
//        View view = getLayoutInflater().inflate(R.layout.list_devices_view, null);
//        listDevices = view.findViewById(R.id.list_devices);
//        listDevices.setAdapter(mDeviceListAdapter);
//
//        builder.setView(view);
//        builder.setNegativeButton("OK", null);
////        builder.create();
////        builder.show();
//    }

    //отслеживание нажатий на экран

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}


