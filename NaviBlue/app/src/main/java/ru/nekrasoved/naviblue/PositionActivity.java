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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PositionActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;

//    Button btBack; //кнопка назад
    ImageButton btSpisok; //кнопка открытия списка маяков
    ImageButton btAdd; //кнопка открытия добавления маяка
    Button btFiltr; //кнопка фильтра по кол-ву маяков
    Button btCorrect; //кнопка изменить фильтр

    EditText etFiltr;
    EditText etCount;

    DBBeacon dbBeacon; //База данных маячков

    Integer filtr;
    Integer count;

    Integer countBeacon; //количество видимых маяков

    double[][] matrixDistance; //матрица дистанций до маяков
    int[][] matrixCoordinate; //координаты маяков

    public static ArrayList <String> filtrBeacons; //фильтрация маячков с расстоянием для вывода

    double measuredPower = -50; //потери в свободном пространстве на расстоянии d0
    double n = 2; //коэффициент погрешности зависящий от типа помещения и т.п.



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

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

        dbBeacon = new DBBeacon(this);
        dbBeacon.open();
        dbBeacon.clearSignalTable();
        dbBeacon.close();

        btSpisok = (ImageButton) findViewById(R.id.bt_map_spisok);
        btSpisok.setOnClickListener(new View.OnClickListener() {
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


                    Intent intent = new Intent(PositionActivity.this, BeaconsList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

        btAdd = (ImageButton) findViewById(R.id.bt_map_add);
        btAdd.setOnClickListener(new View.OnClickListener() {
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


                    Intent intent = new Intent(PositionActivity.this, MainDeviceList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

//        btBack = (Button) findViewById(R.id.bt_pos_Back);
//        btBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    stopScanning();
//
//                    //вывод добавленных сигналов
//                    Cursor cursor;
//                    dbBeacon.open();
//                    cursor = dbBeacon.getAllDataSignal();
//
//                    if (cursor.moveToFirst()) {
//                        int idIndex = cursor.getColumnIndex(DBBeacon.KEY_SIGNAL_ID);
//                        int signalIndex = cursor.getColumnIndex(DBBeacon.KEY_SIGNAL_RSSI);
//                        int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_BEACON_ADDRESS);
//                        do {
//                            String s;
//                            s = "id = "+ cursor.getString(idIndex) + " : rssi = " + cursor.getString(signalIndex) +
//                                    " : address = " + cursor.getString(addressIndex) + " ;";
//                            Log.d("Logm", s);
//
//                        } while (cursor.moveToNext());
//                    }
//                    dbBeacon.close();
//
//
//                    Intent intent = new Intent(PositionActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);
//                    finish();
//                }catch (Exception e){
//
//                }
//            }
//        });

        btFiltr = (Button) findViewById(R.id.bt_pos_Filtr);
        btCorrect = (Button) findViewById(R.id.bt_pos_Correct);

        etFiltr = (EditText) findViewById(R.id.et_pos_Filtr);
        etCount = (EditText) findViewById(R.id.et_pos_Count);

        btCorrect.setEnabled(false);
        btFiltr.setEnabled(true);

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

        btCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    btCorrect.setEnabled(false);
                    btFiltr.setEnabled(true);
                    etFiltr.setEnabled(true);
                    etCount.setEnabled(true);
                    etFiltr.setText(null);
                    etCount.setText(null);
                    stopScanning();

                }catch (Exception e){

                }
            }
        });

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

            btCorrect.setEnabled(true);
            btFiltr.setEnabled(false);
            etFiltr.setEnabled(false);
            etCount.setEnabled(false);
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

            matrixCoordinate = new int[3][filtr];
            matrixDistance = new double[1][filtr];
            int i = 0;

            if (cursor.moveToFirst()){
                int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
                int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
                int xIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_X);
                int yIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Y);
                int zIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Z);
                do {
                    double rssi = dbBeacon.beaconSignal(cursor.getString(addressIndex), count);
                    double distance = getDistance(rssi);
                    Log.d("Logm", cursor.getString(nameIndex) + " :: " + cursor.getString(addressIndex) +
                            " : " + rssi);
                    filtrBeacons.add(cursor.getString(nameIndex) + " :: " +
                            " : rssi = " + rssi + " : dist = " + distance);

                    //заполняем матрицы
                    matrixDistance[0][i] = distance;
                    matrixCoordinate[0][i]= Integer.valueOf(cursor.getString(xIndex));
                    matrixCoordinate[1][i]= Integer.valueOf(cursor.getString(yIndex));
                    matrixCoordinate[2][i]= Integer.valueOf(cursor.getString(zIndex));
                    Log.d("LogMatrix", matrixDistance[0][i] + ", X: " + matrixCoordinate[0][i] +
                            ", Y: " + matrixCoordinate[1][i] + ", Z: "+ matrixCoordinate[2][i]);
                    i++;

                    // адаптер
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filtrBeacons);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    ListView list_beacon = (ListView) findViewById(R.id.list_pos_beacon);
                    list_beacon.setAdapter(adapter);

                } while (cursor.moveToNext());

                if (filtr < 3){
                    TextView posX = (TextView) findViewById(R.id.tv_pos_X);
                    TextView posY = (TextView) findViewById(R.id.tv_pos_Y);
                    TextView posZ = (TextView) findViewById(R.id.tv_pos_Z);

                    posX.setText("X = NaN");
                    posY.setText("Y = NaN");
                    posZ.setText("Z = NaN");
                }
                else{
                    getPosition();
                }

            }
        }
        else {
            countBeacon ++;
        }
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

    //расчет дистанции до маяка
    public double getDistance(double rssi) {

        double distance = 0;
        distance = Math.pow(10, ((measuredPower - rssi)/(10 * n)));
        Log.d("Logm", "DISTANCE = " + distance + " : " + rssi);
        return distance;
    }
    //конец расчета дистанции до маяка

    //определение координат относительно маяков
    public void getPosition() {

        float u_star = (float) measuredPower;
        float m = -16;
        float sig_u = 5;

        float xop = 1;
        float yop = 1;
        float zop = 1;
        //добавить вычислить среднее

        int[] mn = new int[2];

        mn[0] = 1;
        mn[1] = matrixDistance[0].length;

        double[][] q = new double[mn[0] * mn[1]][mn[0] * mn[1]];
        float[][] h = new float[mn[0] * mn[1]][3];
        double[][] rop = new double[mn[1]][mn[0]];
        double[][] derv = new double[mn[1]][mn[0]];
        double[][] z = new double[mn[1]][mn[0]];

        for (int i = 0; i < (mn[0]*mn[1]); i++){
            for (int j = 0; j < (mn[0]*mn[1]); j++){
                if (i == j){
                    q[i][j] = 1/(Math.pow(sig_u, 2));
                }
                else {
                    q[i][j] = 0;
                }
            }
        }
        for (int en = 0; en < 10; en++){

            for (int j = 0; j < mn[1]; j++){
                for (int i = 0; i < mn[0]; i++){
                    rop[j][i] = Math.sqrt(Math.pow(xop - matrixCoordinate[0][j],2) +
                            Math.pow(yop - matrixCoordinate[1][j],2) +
                            Math.pow(zop - matrixCoordinate[2][j], 2));
                    derv[j][i] = m * 1 / rop[j][i] / Math.log(10);

                    h[(j)*mn[0] + i][0] = (float) ((xop - matrixCoordinate[0][j])/rop[j][0]*derv[j][0]);
                    h[(j)*mn[0] + i][1] = (float) ((yop - matrixCoordinate[1][j])/rop[j][0]*derv[j][0]);
                    h[(j)*mn[0] + i][2] = (float)((zop - matrixCoordinate[2][j])/rop[j][0]*derv[j][0]);

                    z[(j)*mn[0] + i][0] = u_star + m * Math.log10(matrixDistance[i][j]) -
                            (u_star + m * Math.log10(rop[j][i]));
                }
            }

            // транспонируем матрицу H
            float[][] ht = new float[3][mn[0]*mn[1]];
            for (int i = 0; i < mn[1]; i++) {
                for (int j = 0; j < 3; j++) {
                    ht[j][i] = h[i][j];
                }
            }

            // H' * Q
            float[][] htq = new float[3][mn[0]*mn[1]];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < mn[1]; j++) {
                    for (int l = 0; l < mn[1]; l++) {
                        htq[i][j] += ht[i][l] * q[l][j];
                    }
                }
            }

            // H' * Q * H
            float[][] htqh = new float[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int l = 0; l < mn[1]; l++) {
                        htqh[i][j] += htq[i][l] * h[l][j];
                    }
                }
            }

            // H' * Q * Z
            float[][] htqz = new float[1][mn[0]*mn[1]];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < mn[1]; j++) {
                    htqz[0][i] += htq[i][j] * z[j][0];
                }
            }


            // (H' * Q * H)^-1
            float[][] htqho = new float[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    htqho[i][j] = htqh[i][j];
                }
            }

            float[][] e = new float[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == j) {
                        e[i][j] = 1;
                    } else {
                        e[i][j] = 0;
                    }
                }
            }

            float temp;
            for (int i = 0; i < 3; i++) {
                temp = htqho[i][i];
                for (int j = 0; j < 3; j++) {
                    htqho[i][j] /= temp;
                    e[i][j] /= temp;
                }
                for (int l = i + 1; l < 3; l++) {
                    temp = htqho[l][i];
                    for (int j = 0; j < 3; j++) {
                        htqho[l][j] -= htqho[i][j] * temp;
                        e[l][j] -= e[i][j] * temp;
                    }
                }
            }
            for (int i = 3 - 1; i > 0; i--) {
                for (int j = i - 1; j >= 0; j--) {
                    temp = htqho[j][i];
                    for (int l = 0; l < 3; l++) {
                        htqho[j][l] -= htqho[i][l] * temp;
                        e[j][l] -= e[i][l] * temp;
                    }
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    htqho[i][j] = e[i][j];
                }
            }

            // (H' * Q * H)^-1 * (H' * Q * Z)
            float[][] x = new float[1][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    x[0][i] += htqho[i][j] * htqz[0][j];
                }
            }
            xop = xop + x[0][0];
            yop = yop + x[0][1];
            zop = zop + x[0][2];
            if (zop < 0){
                zop = Math.abs(zop);
            }
            Log.d("Coordinates", "X: " + xop);
            Log.d("Coordinates", "Y: " + yop);
            Log.d("Coordinates", "Z: " + zop);
        }

        //вывод координат на экран в приложении

        TextView posX = (TextView) findViewById(R.id.tv_pos_X);
        TextView posY = (TextView) findViewById(R.id.tv_pos_Y);
        TextView posZ = (TextView) findViewById(R.id.tv_pos_Z);

        posX.setText("X = "+ xop);
        posY.setText("Y = "+ yop);
        posZ.setText("Z = "+ zop);

    }
    //конец определения координат
}
