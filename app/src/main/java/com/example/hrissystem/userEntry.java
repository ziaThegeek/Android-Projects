package com.example.hrissystem;

import java.sql.Date;
import java.sql.Time;

public class userEntry {
    String date,name,cnic;
    String checkinTime,checkOutTime;
    Double checkinLatitude,checkinLongitude,checkoutLatitude,checkoutLongitude;



    public userEntry(String name, String date, String checkinTime,
                     String checkOutTime, Double checkinLatitude,
                     Double checkinLongitude, Double checkoutLatitude, Double checkoutLongitude,String cnic) {
        this.name=name;
        this.date = date;
        this.checkinTime = checkinTime;
        this.checkOutTime = checkOutTime;
        this.checkinLatitude = checkinLatitude;
        this.checkinLongitude = checkinLongitude;
        this.checkoutLatitude = checkoutLatitude;
        this.checkoutLongitude = checkoutLongitude;
        this.cnic=cnic;
    }
public  userEntry()
{

}

    public String getCnic() {
        return cnic;
    }

    public String getDate() {
        return date;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public Double getCheckinLatitude() {
        return checkinLatitude;
    }

    public Double getCheckinLongitude() {
        return checkinLongitude;
    }

    public Double getCheckoutLatitude() {
        return checkoutLatitude;
    }

    public Double getCheckoutLongitude() {
        return checkoutLongitude;
    }
    public String getName() {
        return name;
    }
}
