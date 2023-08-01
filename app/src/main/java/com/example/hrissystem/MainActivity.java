package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import leaveApplications.LeaveApplication;
import userQueries.UserQueries;

public class MainActivity extends AppCompatActivity {
    String currentTime;
    String currentDate;
    String username, cnic, CNIC;
    Intent loginData;

    Location lastLocation;
    Button checkinBtn, checkoutBtn;
    TextView currentTimeText, currentDateText, usernameText;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> checkinTime, checkoutTime, date, name, userKey;
    listviewAdapter listviewAdapter;
    ListView listView;
    ArrayAdapter<CharSequence> leave_types;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkinBtn = findViewById(R.id.checkinBtn);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        currentTimeText = findViewById(R.id.current_time);
        currentDateText = findViewById(R.id.current_date);
        usernameText = findViewById(R.id.username);
        listView = findViewById(R.id.listview);


        //-----------------------------------------//
        currentDate = getCurrentDate();
        currentTime = getCurrentTime();
        checkinTime = new ArrayList<>();
        checkoutTime = new ArrayList<>();
        date = new ArrayList<>();
        name = new ArrayList<>();
        userKey = new ArrayList<>();
        listviewAdapter = new listviewAdapter(this, checkinTime, checkoutTime, date);

        leave_types = ArrayAdapter.createFromResource(this,
                R.array.leave_types, android.R.layout.simple_spinner_item);
        leave_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loginData = getIntent();
        username = loginData.getStringExtra("username");
        cnic = loginData.getStringExtra("cnic");
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

                if (!date.isEmpty() && date.get(date.size() - 1).equals(getCurrentDate())) {
                    Toast.makeText(MainActivity.this, "Already CheckedIn today  ", Toast.LENGTH_SHORT).show();
                } else if (lastLocation != null) {
                    userEntry userEntry = new userEntry(
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
                } else {

                }
            }
        });
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                if (!checkoutTime.isEmpty() && !checkoutTime.get(checkoutTime.size() - 1).equals("00:00:00")) {
                    Toast.makeText(MainActivity.this, "Already Checked Out", Toast.LENGTH_SHORT).show();
                } else if (lastLocation != null) {

                    databaseReference.child(userKey.get(userKey.size() - 1)).child("checkOutTime").setValue(getCurrentTime());
                    databaseReference.child(userKey.get(userKey.size() - 1)).child("checkoutLatitude").setValue(lastLocation.getLatitude());
                    databaseReference.child(userKey.get(userKey.size() - 1)).child("checkoutLongitude").setValue(lastLocation.getLongitude());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_actions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_apply:
                showLeaveApplicationDialog();
                return true;
            case R.id.user_msgs:
                Intent msg_activity = new Intent(MainActivity.this, UserQueries.class);
                msg_activity.putExtra("cnic", cnic);
                msg_activity.putExtra("username", username);

                startActivity(msg_activity);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showLeaveApplicationDialog() {
        EditText fromDateText, toDateText, reasonText;
        Spinner leave_type;
        Button apply_leave;
        AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.leave_appliction_dialog, null);
        //--------------------------------------------------//
        fromDateText = dialogView.findViewById(R.id.from_date);
        toDateText = dialogView.findViewById(R.id.to_date);
        reasonText = dialogView.findViewById(R.id.reason);
        apply_leave = dialogView.findViewById(R.id.apply_leave);
        leave_type = dialogView.findViewById(R.id.leave_type);
        //-----------------------------------------------------//

        //----------------------------------------------------//
        fromDateText.setText(getCurrentDate());
        toDateText.setText(getCurrentDate());
        leave_type.setAdapter(leave_types);
        //----------------------------------------------------//

        customDialog.setView(dialogView);
        customDialog.setTitle("Leave Application");


        AlertDialog dialog = customDialog.create();
        dialog.show();
        apply_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reasonText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Reason Is Required", Toast.LENGTH_SHORT).show();
                } else if (fromDateText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Reason Is Required", Toast.LENGTH_SHORT).show();
                } else if (toDateText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Reason Is Required", Toast.LENGTH_SHORT).show();
                } else if (leave_type.getSelectedItemPosition() == 0) {
                    Toast.makeText(MainActivity.this, "Leave Type Is Required", Toast.LENGTH_SHORT).show();


                } else {
                    applyApplication(username, cnic, fromDateText.getText().toString(), toDateText.getText().toString(), leave_type.getSelectedItem().toString(), reasonText.getText().toString());
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Leave Applied", Toast.LENGTH_SHORT).show();


                }

            }
        });

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFromDialog(fromDateText);
            }
        });
        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFromDialog(toDateText);
            }
        });

    }

    private void applyApplication(String name, String cnic, String fromDate, String toDate, String leave_type, String reson) {
        DatabaseReference applicationReference = FirebaseDatabase.getInstance().getReference("leaves");
        LeaveApplication application = new LeaveApplication(name, cnic, fromDate, toDate, leave_type, reson, getCurrentDate());
        applicationReference.push().setValue(application);
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
                            lastLocation = location;

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

    public void loadDate() {
        CNIC = "";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                checkinTime.clear();
                checkoutTime.clear();
                date.clear();
                name.clear();
                userKey.clear();
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    userEntry newUser = dsp.getValue(userEntry.class);
                    CNIC = newUser.getCnic();
                    if (CNIC.equals(cnic)) {
                        checkinTime.add(newUser.checkinTime);
                        checkoutTime.add(newUser.checkOutTime);
                        date.add(newUser.date);
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


    private void setDateFromDialog(TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        textView.setText(String.format(Locale.ENGLISH, "%s%s%s%s%s", String.format(Locale.ENGLISH, "%02d", dayOfMonth), "-", String.format(Locale.ENGLISH, "%02d", monthOfYear + 1), "-", String.format(Locale.ENGLISH, "%04d", year)));

                    }
                }, getYear(textView.getText().toString().trim()),
                getMonth(textView.getText().toString().trim()),
                getDay(textView.getText().toString())

        );

        datePickerDialog.show();

    }

    private Integer getDay(String date) {
        return Integer.parseInt(date.substring(0, 2));
    }

    private Integer getMonth(String date) {
        return Integer.parseInt(date.substring(3, 5)) - 1;
    }

    private Integer getYear(String date) {
        return Integer.parseInt(date.substring(6));
    }


}