package ru.nekrasoved.naviblue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


public class MainDeviceList extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_devices);

        Button btBack = (Button)findViewById(R.id.btBack);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainDeviceList.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){

                }
            }
        });

    }
}
