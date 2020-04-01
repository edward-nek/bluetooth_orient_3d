package ru.nekrasoved.naviblue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainDeviceList extends AppCompatActivity{

    public int pos;
    public Spinner mSpinner;
    public EditText etX;
    public EditText etY;
    public EditText etZ;

    DBBeacon dbBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_devices);

        Button btBack = (Button)findViewById(R.id.btBack);

        etX = (EditText) findViewById(R.id.etX);
        etY = (EditText) findViewById(R.id.etY);
        etZ = (EditText) findViewById(R.id.etZ);

        dbBeacon = new DBBeacon(this);

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


        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.mBaseDevices.spinner_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(adapter);

        // слушатель выбора в списке
        AdapterView.OnItemSelectedListener itemListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        if (MainActivity.mBaseDevices.name.size() > 0) {
            mSpinner.setOnItemSelectedListener(itemListener);
        }

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

            String nameBeacon = MainActivity.mBaseDevices.name.get(pos);
            String addressBeacon = MainActivity.mBaseDevices.address.get(pos);
            Integer posXbeacon = new Integer(String.valueOf(etX.getText()));
            Integer posYbeacon = new Integer(String.valueOf(etY.getText()));
            Integer posZbeacon = new Integer(String.valueOf(etZ.getText()));

            //добавление данных в БД SQLite

            SQLiteDatabase database = dbBeacon.getWritableDatabase(); //открытие БД для записи и чтения

            ContentValues contentValues = new ContentValues();

            //Добавить
            contentValues.put(DBBeacon.KEY_NAME, nameBeacon);
            contentValues.put(DBBeacon.KEY_ADDRESS, addressBeacon);
            contentValues.put(DBBeacon.KEY_POS_X, posXbeacon);
            contentValues.put(DBBeacon.KEY_POS_Y, posYbeacon);
            contentValues.put(DBBeacon.KEY_POS_Z, posZbeacon);

            database.insert(DBBeacon.TABLE_BEACONS, null, contentValues);
            //Конец добавить


            //Вывод логов о добавлении в консоль

            Cursor cursor = database.query(DBBeacon.TABLE_BEACONS, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DBBeacon.KEY_ID);
                int nameIndex = cursor.getColumnIndex(DBBeacon.KEY_NAME);
                int addressIndex = cursor.getColumnIndex(DBBeacon.KEY_ADDRESS);
                int xIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_X);
                int yIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Y);
                int zIndex = cursor.getColumnIndex(DBBeacon.KEY_POS_Z);

                    do {
                        Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                                ", NAME = " + cursor.getString(nameIndex) +
                                ", ADDRESS = " + cursor.getString(addressIndex) +
                                ", X = " + cursor.getInt(xIndex) +
                                ", Y = " + cursor.getInt(yIndex) +
                                ", Z = " + cursor.getInt(zIndex));
                    } while (cursor.moveToNext());
            }
            else {
                Log.d("mLog", "0 rows");
            }
            //главное меню

            Intent intent = new Intent(MainDeviceList.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this,"Для начала заполните поля!", Toast.LENGTH_LONG).show();
        }
    }
}
