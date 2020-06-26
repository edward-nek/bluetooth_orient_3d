package ru.nekrasoved.naviblue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainDeviceList extends AppCompatActivity {

    private Button statusButton;
    private BluetoothAdapter bluetooth;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    private ListView listDevices;


    private DeviceListAdapter mDeviceListAdapter;
    private ProgressDialog mProgressDialog;


    public int pos = 0;
    public Spinner mSpinner;
    public EditText etX;
    public EditText etY;
    public EditText etZ;
    public EditText etName;

    public EditText etN;
    public EditText etPower;

    public ArrayAdapter<String> adapter;

    DBBeacon dbBeacon;

    public AdapterView.OnItemSelectedListener itemListener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_devices);

        //скрыть панель навигации начало

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        //скрыть панель навигации конец

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить маяк");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#006B53")));


        etX = (EditText) findViewById(R.id.etX);
        etY = (EditText) findViewById(R.id.etY);
        etZ = (EditText) findViewById(R.id.etZ);
        etName = (EditText) findViewById(R.id.etName);

        etN = (EditText) findViewById(R.id.etN);
        etPower = (EditText) findViewById(R.id.etPower);

        dbBeacon = new DBBeacon(this);
        dbBeacon.open();

        //statusButton = (Button) findViewById(R.id.status_blue);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null) {
            Toast.makeText(this, "Ваше устройство не поддерживает bluetooth!", Toast.LENGTH_LONG).show();
            finish();
        }

//        if (bluetooth.isEnabled()) {
//                statusButton.setText("On");
//        }

        mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_item, mDevices);


        try {
            findDevice();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ImageButton btSpisok = (ImageButton) findViewById(R.id.bt_add_spisok);
        ImageButton btMap = (ImageButton) findViewById(R.id.bt_add_map);

        btSpisok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainDeviceList.this, BeaconsList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } catch (Exception e) {

                }
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainDeviceList.this, PositionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } catch (Exception e) {

                }
            }
        });

        // слушатель выбора в списке
        itemListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    public void SaveBeacon(View view) {


        if ((MainActivity.mBaseDevices.name.size() > 0) && (etX.length() > 0) &&
                (etY.length() > 0) && (etZ.length() > 0)) {


            //late version
//            MainActivity.mBaseBeacon.name.add(MainActivity.mBaseDevices.name.get(pos));
//            MainActivity.mBaseBeacon.address.add(MainActivity.mBaseDevices.address.get(pos));
//            MainActivity.mBaseBeacon.pos_x.add(new Integer(String.valueOf(etX.getText())));
//            MainActivity.mBaseBeacon.pos_y.add(new Integer(String.valueOf(etY.getText())));
//            MainActivity.mBaseBeacon.pos_z.add(new Integer(String.valueOf(etZ.getText())));
//            Toast.makeText(this,"Добавлен маяк: " + MainActivity.mBaseDevices.name.get(pos)+
//                            " : "+ MainActivity.mBaseDevices.address.get(pos) +
//                            " : " + MainActivity.mBaseBeacon.pos_x + "," +
//                            " : " + MainActivity.mBaseBeacon.pos_y + "," +
//                            " : " + MainActivity.mBaseBeacon.pos_z,
//                            Toast.LENGTH_LONG).show();
//            MainActivity.mBaseDevices.name.remove(pos);
//            MainActivity.mBaseDevices.address.remove(pos);
//            MainActivity.mBaseDevices.spinner_name.remove(pos);


            //считывание данных при нажатии кнопки сохранить

            String nameBeacon;

            if (String.valueOf(etName.getText()).length() > 0) {
                nameBeacon = String.valueOf(etName.getText());
            } else {
                nameBeacon = MainActivity.mBaseDevices.name.get(pos);
            }
            String addressBeacon = MainActivity.mBaseDevices.address.get(pos);
            Integer posXbeacon = new Integer(String.valueOf(etX.getText()));
            Integer posYbeacon = new Integer(String.valueOf(etY.getText()));
            Integer posZbeacon = new Integer(String.valueOf(etZ.getText()));

            Integer nBeacon;
            Integer powerBeacon;

            if (String.valueOf(etN.getText()).length() > 0) {
                nBeacon = new Integer(String.valueOf(etN.getText()));
            } else {
                nBeacon = 2;
            }

            if (String.valueOf(etPower.getText()).length() > 0) {
                powerBeacon = new Integer(String.valueOf(etPower.getText()));
                if (powerBeacon > 0) {
                    powerBeacon = -powerBeacon;
                }
            } else {
                powerBeacon = -68;
            }


            //добавление данных в БД SQLite

            dbBeacon.open(); //открытие БД для записи и чтения

            ContentValues contentValues = new ContentValues();

            dbBeacon.addRec(nameBeacon, addressBeacon, posXbeacon, posYbeacon, posZbeacon, nBeacon, powerBeacon);

            //Вывод логов о добавлении в консоль

            Cursor cursor = dbBeacon.getAllData();

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DBBeacon.KEY_ID);
                int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
                int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
                int xIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_X);
                int yIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Y);
                int zIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Z);

                int nIndex = cursor.getColumnIndex(DBBeacon.KEY_N);
                int powerIndex = cursor.getColumnIndex(DBBeacon.KEY_POWER);

                do {
                    Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                            ", NAME = " + cursor.getString(nameIndex) +
                            ", ADDRESS = " + cursor.getString(addressIndex) +
                            ", X = " + cursor.getInt(xIndex) +
                            ", Y = " + cursor.getInt(yIndex) +
                            ", Z = " + cursor.getInt(zIndex) +
                            ", N = " + cursor.getInt(nIndex) +
                            ", MeasuredPower = " + cursor.getInt(powerIndex));
                } while (cursor.moveToNext());
            } else {
                Log.d("mLog", "0 rows");
            }

            //удаление выбранного маяка из списка выбора

            MainActivity.mBaseDevices.name.remove(pos);
            MainActivity.mBaseDevices.address.remove(pos);
            MainActivity.mBaseDevices.spinner_name.remove(pos);
            //главное меню

            Intent intent = new Intent(MainDeviceList.this, BeaconsList.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {
            Toast.makeText(this, "Для начала заполните поля!", Toast.LENGTH_LONG).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void findDevice() throws InterruptedException {

        bluetooth.enable();

        Thread.sleep(400); //для того, чтобы успел включиться блютуз, пауза


        checkPermissionLocation();

        if (!bluetooth.isDiscovering()) {
            bluetooth.startDiscovery();
        }

        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
            bluetooth.startDiscovery();
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {

        try{
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);

        }catch(Exception e){}

        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int check = checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            check += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (check != 0) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1002);
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                //Toast.makeText(MainActivity.this, "Поиск начался", Toast.LENGTH_LONG).show();

                mProgressDialog = ProgressDialog.show(MainDeviceList.this, "Поиск устройств", "Пожалуйста подождите");

                MainActivity.mBaseDevices = new BaseDevices();
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

                mProgressDialog.dismiss();

                //Toast.makeText(MainActivity.this, "Поиск завершен", Toast.LENGTH_LONG).show();
                showListDevices();
            }
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (!mDevices.contains(device)) {

                        //добавляем устройство в базу устройств
                        if (checkCopy(device.getAddress())) {
                            MainActivity.mBaseDevices.address.add(device.getAddress());

                            if (device.getName() == null) {
                                MainActivity.mBaseDevices.name.add("Not Name");
                                MainActivity.mBaseDevices.spinner_name.add("Not Name : " + device.getAddress()); //костыль
                            } else {
                                MainActivity.mBaseDevices.name.add(device.getName());
                                MainActivity.mBaseDevices.spinner_name.add(device.getName() + " : " + device.getAddress()); //костыль
                            }

                            mDeviceListAdapter.add(device);
                        }
                    }
                }
            }
        }
    };

    //проверка на копии

    private Boolean checkCopy(String address) {
        Boolean check = true;
        Cursor cursor = dbBeacon.getAllData();

        if (cursor.moveToFirst()) {
            int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
            do {
                if (cursor.getString(addressIndex).equals(address)) {
                    check = false;
                }
            } while (cursor.moveToNext());
        }
        return check;
    }

    private void showListDevices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Найденные устройства: ");

        View view = getLayoutInflater().inflate(R.layout.list_devices_view, null);
        listDevices = view.findViewById(R.id.list_devices);
        listDevices.setAdapter(mDeviceListAdapter);

        builder.setView(view);
        builder.setNegativeButton("OK", null);
        builder.create();
        builder.show();

        // адаптер
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.mBaseDevices.spinner_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(adapter);

        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(itemListener);
    }

    //отслеживание нажатий на экран

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
