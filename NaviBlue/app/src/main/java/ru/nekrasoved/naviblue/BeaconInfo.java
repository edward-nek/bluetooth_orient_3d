package ru.nekrasoved.naviblue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BeaconInfo extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        Button bt_Back = (Button)findViewById(R.id.bt_Back_info);

        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(BeaconInfo.this, BeaconsList.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){

                }
            }
        });

        //set name beacon
        text = (TextView) findViewById(R.id.tvNameBeacon);
        text.setText(MainActivity.mBaseBeacon.name.get(MainActivity.filtrAddressId));

        //set address beacon
        text = (TextView) findViewById(R.id.tvAddressBeacon);
        text.setText(MainActivity.mBaseBeacon.address.get(MainActivity.filtrAddressId));

        //set X beacon
        text = (TextView) findViewById(R.id.tvXBeacon);
        text.setText("X = " + MainActivity.mBaseBeacon.pos_x.get(MainActivity.filtrAddressId));

        //set Y beacon
        text = (TextView) findViewById(R.id.tvYBeacon);
        text.setText("Y = " + MainActivity.mBaseBeacon.pos_y.get(MainActivity.filtrAddressId));

        //set Z beacon
        text = (TextView) findViewById(R.id.tvZBeacon);
        text.setText("Z = " + MainActivity.mBaseBeacon.pos_z.get(MainActivity.filtrAddressId));




    }
}
