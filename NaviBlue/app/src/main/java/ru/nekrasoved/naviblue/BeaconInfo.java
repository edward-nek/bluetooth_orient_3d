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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.util.ArrayList;
import java.util.List;

public class BeaconInfo extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;

    Button btBack; //назад
    Button btDelete; //удалить маяк

    LineChart chart; //блок с графиком

    TextView tvName; //имя
    TextView tvAddress; //адрес
    TextView tvX; //координата X
    TextView tvY; //координата Y
    TextView tvZ; //координата Z
    TextView tvRssi; //уровень сигнала Rssi

    DBBeacon dbBeacon;


    int count; //счетчик для усреднения
    int count_beacon; //счетчик для X
    int sum; //усреднение

    int LIMIT_CHECK = 2; //кол-во измерений для усреднения
    int LIMIT_GRAPH = 15; //кол-во точек на графике

    List<Entry> entries;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        count_beacon = 0;
        sum = 0;
        count = 0;
        entries = new ArrayList<Entry>();
        chart = (LineChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);

        tvName = (TextView) findViewById(R.id.tv_inf_Name);

        entries.add(new Entry(0, 0));
        LineDataSet dataSet = new LineDataSet(entries, " "); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.WHITE);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        entries = new ArrayList<Entry>();

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);

        //скрыть панель навигации начало

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
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

        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

        final Cursor cursor = dbBeacon.getAllData();

        btBack = (Button)findViewById(R.id.bt_inf_Back);
        btBack.setOnClickListener(new View.OnClickListener() {
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

        btDelete = (Button)findViewById(R.id.bt_inf_Delete);
        btDelete.setOnClickListener(new View.OnClickListener() {
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
                    tvName = (TextView) findViewById(R.id.tv_inf_Name);
                    tvName.setText(cursor.getString(nameIndex));


                    //set address beacon
                    tvAddress = (TextView) findViewById(R.id.tv_inf_Address);
                    tvAddress.setText(cursor.getString(addressIndex));

                    //set X beacon
                    tvX = (TextView) findViewById(R.id.tv_inf_X);
                    tvX.setText("X = " + cursor.getInt(xIndex));

                    //set Y beacon
                    tvY = (TextView) findViewById(R.id.tv_inf_Y);
                    tvY.setText("Y = " + cursor.getInt(yIndex));

                    //set Z beacon
                    tvZ = (TextView) findViewById(R.id.tv_inf_Z);
                    tvZ.setText("Z = " + cursor.getInt(zIndex));
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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //set Rssi beacon
            if (result.getDevice().getAddress().equals(MainActivity.filtrAddress)){
                tvRssi = (TextView) findViewById(R.id.tv_inf_Rssi);
                tvRssi.setText("rssi = " + result.getRssi());

                count++;
                sum += result.getRssi();
                if (count >= LIMIT_CHECK) {
                    count_beacon += LIMIT_CHECK;
                    sum = sum / LIMIT_CHECK;
                    if (count_beacon > LIMIT_CHECK * LIMIT_GRAPH) {
                        entries.remove(0);
                    }
                    entries.add(new Entry(count_beacon, sum));
                    LineDataSet dataSet = new LineDataSet(entries, tvName.getText().toString()); // add entries to dataset
                    dataSet.setColor(Color.RED);
                    dataSet.setCircleColor(Color.WHITE);
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.invalidate(); // refresh
                    count = 0;
                    sum = 0;
                }

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

    //отслеживание нажатий на экран

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
