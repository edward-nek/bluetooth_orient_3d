package ru.nekrasoved.naviblue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class BeaconsList extends AppCompatActivity {

    public ArrayList <String> test;

    public Intent intent;

    DBBeacon dbBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);

        Button bt_Back = (Button)findViewById(R.id.bt_Back);

        dbBeacon = new DBBeacon(this); //БД Маяков
        dbBeacon.open();

        Cursor cursor = dbBeacon.getAllData();

        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconsList.this, MainActivity.class);
                    startActivity(intent);
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
            int xIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_X);
            int yIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Y);
            int zIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Z);

            int i = 0;

            do {
                test.add(i,  cursor.getString(nameIndex) +
                        " : " + cursor.getString(addressIndex) +
                        " : X =" + cursor.getInt(xIndex) +
                        " : Y =" + cursor.getInt(yIndex) +
                        " : Z =" + cursor.getInt(zIndex));

                i ++;
            } while (cursor.moveToNext());
        }


        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, test);
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
                    int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);

                    int i = 0;

                    do {
                        if (cursor.getString(addressIndex).equals(MainActivity.filtrAddress)){
                            MainActivity.filtrAddressId = i;
                        }
                        i ++;
                    } while (cursor.moveToNext());
                }


                //test

//                Toast.makeText(BeaconsList.this, MainActivity.filtrAddressId + " " + MainActivity.filtrAddress, Toast.LENGTH_LONG).show();

                intent = new Intent(BeaconsList.this, BeaconInfo.class);
                startActivity(intent);
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
}
