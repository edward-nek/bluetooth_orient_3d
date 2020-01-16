package ru.nekrasoved.naviblue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class BeaconsList extends AppCompatActivity {

    public ArrayList <String> test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);

        // open Form add beacons

        Button bt_Back = (Button)findViewById(R.id.bt_Back);

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

        for(int i = 0; i < MainActivity.mBaseBeacon.name.size(); i++){
            test.add(i, MainActivity.mBaseBeacon.name.get(i) + " : "+ MainActivity.mBaseBeacon.address.get(i) + " : X="+
                    MainActivity.mBaseBeacon.pos_x.get(i)  + " : Y="+ MainActivity.mBaseBeacon.pos_y.get(i)  + " : Z="+
                    MainActivity.mBaseBeacon.pos_z.get(i));
        }

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, test);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ListView list_beacon = (ListView) findViewById(R.id.list_beacon);
        list_beacon.setAdapter(adapter);
    }
}
