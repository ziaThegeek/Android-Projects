package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
String currentTime;
String currentDate;
String username,cnic;
Intent loginData;

Location lastLocation;
Button   checkinBtn,checkoutBtn;
TextView currentTimeText,currentDateText,usernameText;

FusedLocationProviderClient mFusedLocationClient;
int PERMISSION_ID = 44;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;
ArrayList<String> checkinTime,checkoutTime,date,name,userKey;
listviewAdapter listviewAdapter;
ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkinBtn = findViewById(R.id.checkinBtn);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        currentTimeText = findViewById(R.id.current_time);
        currentDateText = findViewById(R.id.current_date);
        usernameText=findViewById(R.id.username);
        listView=findViewById(R.id.listview);

        //-----------------------------------------//
        currentDate = getCurrentDate();
        currentTime = getCurrentTime();
        checkinTime=new ArrayList<>();
        checkoutTime=new ArrayList<>();
        date=new ArrayList<>();
        name=new ArrayList<>();
        userKey=new ArrayList<>();
        listviewAdapter=new listviewAdapter(this,checkinTime,checkoutTime,date,name);
        loginData=getIntent();
        username=loginData.getStringExtra("username");
        cnic=loginData.getStringExtra("cnic");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("userEntry");


        //-----------------------------------------//


        //---------------------------------------------//
        currentDateText.setText(currentDate);
        currentTimeText.setText(currentTime);
        usernameText.setText(username);
        loadDate();
        //---------------------------------------------//


        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();

                    if (!date.isEmpty() && date.get(date.size()-1).equals(getCurrentDate()))
                    {
                        Toast.makeText(MainActivity.this, "Already CheckedIn today  ", Toast.LENGTH_SHORT).show();
                    }
                else if (lastLocation != null) {
                    userEntry userEntry=new userEntry(
                            username,
                            getCurrentDate(),
                            getCurrentTime(),
                            "00:00:00",
                             lastLocation.getLatitude(),
                            lastLocation.getLongitude(),
                            0.0,
                            0.0,
                            cnic);
                    databaseReference.push().setValue(userEntry);
                    Toast.makeText(MainActivity.this, "CheckIn successful", Toast.LENGTH_SHORT).show();
                }
                else
                {

                }
            }
        });
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                if (!checkoutTime.isEmpty() && !checkoutTime.get(checkoutTime.size()-1).equals("00:00:00"))
                {
                    Toast.makeText(MainActivity.this, "Already Checked Out", Toast.LENGTH_SHORT).show();
                }
               else if (lastLocation != null) {

                    databaseReference.child(userKey.get(userKey.size()-1)).child("checkOutTime").setValue(getCurrentTime());
                    databaseReference.child(userKey.get(userKey.size()-1)).child("checkoutLatitude").setValue(lastLocation.getLatitude());
                    databaseReference.child(userKey.get(userKey.size()-1)).child("checkoutLongitude").setValue(lastLocation.getLongitude());
                    Toast.makeText(MainActivity.this, "checkout successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentTimeText.setText(getCurrentTime());
        currentDateText.setText(getCurrentDate());
    }

    public String getCurrentTime() {


        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        return currentTime;
    }

    public String getCurrentDate() {


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new java.util.Date());
        return currentDate;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lastLocation=location;

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }

    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

public void loadDate()
{
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {


            checkinTime.clear();
            checkoutTime.clear();
            date.clear();
            name.clear();
            userKey.clear();
            for (DataSnapshot dsp:snapshot.getChildren())
            {
                userEntry newUser=dsp.getValue(userEntry.class);
                if (newUser.name.equals(username)) {
                    checkinTime.add(newUser.checkinTime);
                    checkoutTime.add(newUser.checkOutTime);
                    date.add(newUser.date);
                    name.add(newUser.name);
                    userKey.add(dsp.getKey());
                }
            }
            listView.setAdapter(listviewAdapter);



        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}






}