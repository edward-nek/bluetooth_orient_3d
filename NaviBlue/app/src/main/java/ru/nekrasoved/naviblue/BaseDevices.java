package ru.nekrasoved.naviblue;

// Класс, в котором хранятся найденные устройства

import java.util.ArrayList;

public class BaseDevices {

    public ArrayList<String> name = new ArrayList<String>(); //имя устройства
    public ArrayList<String> address = new ArrayList<String>(); //адрес устройства

    //костыль
    public ArrayList<String> spinner_name = new ArrayList<String>(); //имя устройства + адрес устройства

}
