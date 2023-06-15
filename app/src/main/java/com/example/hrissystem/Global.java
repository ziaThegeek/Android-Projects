package com.example.hrissystem;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Global {
public  static String getCurrentDate()
{
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new java.util.Date());
    return currentDate;
}
public static String getCurrentTime()
{
    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
    return currentTime;
}

}
