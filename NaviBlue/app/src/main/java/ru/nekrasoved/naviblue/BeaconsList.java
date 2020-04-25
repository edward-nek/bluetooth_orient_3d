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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BeaconsList extends AppCompatActivity {




    public static ArrayList <String> test;

    public Intent intent;

    DBBeacon dbBeacon;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);

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
        actionBar.setTitle("Cписок маяков");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#006B53")));


        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

        Cursor cursor = dbBeacon.getAllData();

        //открыть окно PositionActivity (местоположение)
        ImageButton btMap = (ImageButton) findViewById(R.id.bt_spisok_map);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconsList.this, PositionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });

        //открыть окно MainDeviceList (добавить Маяк)
        ImageButton btAdd = (ImageButton) findViewById(R.id.bt_spisok_add);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconsList.this, MainDeviceList.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }catch (Exception e){

                }
            }
        });


        //костыль
        test = new ArrayList<String>();

//        for(int i = 0; i < cursor.getCount(); i++){
//            test.add(i, MainActivity.mBaseBeacon.name.get(i) + " : "+ MainActivity.mBaseBeacon.address.get(i) + " : X="+
//                    MainActivity.mBaseBeacon.pos_x.get(i)  + " : Y="+ MainActivity.mBaseBeacon.pos_y.get(i)  + " : Z="+
//                    MainActivity.mBaseBeacon.pos_z.get(i));
//        }


        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBBeacon.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
            int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);

            do {
                test.add(idIndex,  cursor.getString(nameIndex) +
                        " : " + cursor.getString(addressIndex) + " ");

            } while (cursor.moveToNext());
        }


        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, test);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ListView list_beacon = (ListView) findViewById(R.id.list_beacon);
        list_beacon.setAdapter(adapter);

        list_beacon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                MainActivity.filtrAddress = getAddress(position);
//                for (int i = 0; i < MainActivity.mBaseBeacon.name.size(); i++){
//                    if (MainActivity.mBaseBeacon.address.get(i).equals(MainActivity.filtrAddress)){
//                        MainActivity.filtrAddressId = i;
//                    }
////                    System.out.println(MainActivity.mBaseBeacon.address.get(i) + " = " + MainActivity.filtrAddress);
//                }

                MainActivity.filtrAddress = getAddress(position);
                Cursor cursor = dbBeacon.getAllData();

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBBeacon.KEY_ID);
                    int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);

                    do {
                        if (cursor.getString(addressIndex).equals(MainActivity.filtrAddress)){
                            MainActivity.filtrAddressId = cursor.getInt(idIndex);
                        }
                    } while (cursor.moveToNext());
                }


                //test

//                Toast.makeText(BeaconsList.this, MainActivity.filtrAddressId + " " + MainActivity.filtrAddress, Toast.LENGTH_LONG).show();

                intent = new Intent(BeaconsList.this, BeaconInfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private String getAddress(int pos){
//        System.out.println(test.get(pos));
//        System.out.println(pos);
        String str = test.get(pos);
        boolean a = true;
        int ind = 0;
        while (a) {
            if(str.charAt(ind) == ':'){
                a = false;
            }
            else{
                ind++;
            }
        }
//        System.out.println(ind);
        ind+=2;
        a = true;
        String address = "";
        while (a){
            if(str.charAt(ind) == ' '){
                a = false;
            }
            else{
                address += str.charAt(ind);
                ind++;
            }
        }
//        System.out.println(address);
        return address;
    }

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
