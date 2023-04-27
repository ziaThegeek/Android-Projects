package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class userActivities extends AppCompatActivity {
    listviewAdapter listviewAdapter;
    ListView listView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> checkinTime,checkoutTime,date,name,userKey,CNIC;
    ArrayList<Double> checkinLongitude;
    ArrayList<Double> checkinLatitude;
    ArrayList<Double> checkoutLatitude;
    ArrayList<Double> checkoutLongitude;
    private static final int REQUST_PERMISSION_CODE_EXCEL = 12;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.export_to_excel:
                export2Excel();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        listView=findViewById(R.id.listview_users_activity);
        checkinTime=new ArrayList<>();
        checkoutTime=new ArrayList<>();

        date=new ArrayList<>();
        name=new ArrayList<>();
        CNIC=new ArrayList<>();
        userKey=new ArrayList<>();
        checkinLatitude=new ArrayList<>();
        checkinLongitude=new ArrayList<>();
        checkoutLatitude=new ArrayList<>();
        checkoutLongitude=new ArrayList<>();
        listviewAdapter=new listviewAdapter(this,checkinTime,checkoutTime,date,name);


        firebaseDatabase = FirebaseDatabase.getInstance();


        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("userEntry");
        loadDate();




        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showUserActivityDialoge(name.get(i),
                        CNIC.get(i),
                        date.get(i),
                        checkinLatitude.get(i),
                        checkinLongitude.get(i),
                        checkoutLatitude.get(i),
                        checkoutLongitude.get(i)
                        );
                return true;
            }
        });

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
                checkoutLatitude.clear();
                checkoutLongitude.clear();
                checkinLatitude.clear();
                checkinLongitude.clear();
                CNIC.clear();
                for (DataSnapshot dsp:snapshot.getChildren())
                {
                    userEntry newUser=dsp.getValue(userEntry.class);

                        checkinTime.add(newUser.checkinTime);
                        checkoutTime.add(newUser.checkOutTime);
                        date.add(newUser.date);
                        name.add(newUser.name);
                        userKey.add(dsp.getKey());
                        CNIC.add(newUser.getCnic());
                        checkinLatitude.add(newUser.getCheckinLatitude());
                        checkinLongitude.add(newUser.getCheckinLongitude());

                        checkoutLatitude.add(newUser.getCheckoutLatitude());
                        checkoutLongitude.add(newUser.getCheckoutLongitude());


                }
                listView.setAdapter(listviewAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showUserActivityDialoge(String name ,String CNIC,String date,
                                         Double checkinLatitude,
                                         Double checkinlogitude,
                                         Double checkoutLatitude,
                                         Double checkoutlongitude

                                         )
    {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.view_user_activity_dialog);
        bottomSheetDialog.show();
        TextView nameText=bottomSheetDialog.findViewById(R.id.name);
        TextView CNICText=bottomSheetDialog.findViewById(R.id.CNIC);
        TextView dateText=bottomSheetDialog.findViewById(R.id.date);

        nameText.setText(name);
        CNICText.setText(CNIC);
        dateText.setText(date);

        Button checkInLocation=bottomSheetDialog.findViewById(R.id.checkin_location);
        Button checkOutLocation=bottomSheetDialog.findViewById(R.id.checkout_location);
        Button close=bottomSheetDialog.findViewById(R.id.close);



        checkInLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open Map for check in
                if (checkinLatitude!=0.0 && checkinlogitude!=0.0)
                {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", checkinLatitude, checkinlogitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(userActivities.this, "Wrong Coordinates", Toast.LENGTH_SHORT).show();

                }
            }
        });

        checkOutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open Map for check out
                if (checkoutLatitude!=0.0 && checkoutlongitude!=0.0)
                {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", checkoutLatitude, checkoutlongitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(userActivities.this, "Wrong Coordinates", Toast.LENGTH_SHORT).show();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }
    private void export2Excel() {
        String permission= Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(userActivities.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    userActivities.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(userActivities.this,
                        new String[]{permission}, REQUST_PERMISSION_CODE_EXCEL);

            } else {
                ActivityCompat.requestPermissions(userActivities.this,
                        new String[]{permission}, REQUST_PERMISSION_CODE_EXCEL);
            }
        } else {
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = "User Activity "+new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())+".xls";

            File directory = new File(sd.getAbsolutePath()+"/HRIS System");

            //create directory if not exist
            if (!directory.isDirectory()) {
                directory.mkdirs();
            }
            try {

                //file path
                File file = new File(directory, csvFile);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getCountry()));
                WritableWorkbook workbook;
                workbook = Workbook.createWorkbook(file, wbSettings);

                //Excel sheetA first sheetA
                WritableSheet sheetA = workbook.createSheet("User Activity", 0);
                if (!name.isEmpty()) {

                    // column and row titles
                    sheetA.addCell(new Label(0, 0, "Sr.No"));
                    sheetA.addCell(new Label(1, 0, "Name"));
                    sheetA.addCell(new Label(2, 0, "CNIC"));
                    sheetA.addCell(new Label(3, 0, "CheckIn Time"));
                    sheetA.addCell(new Label(4, 0, "Checkout Time"));
                    sheetA.addCell(new Label(5, 0, "Date"));

                    for(int rows=1;rows<=name.size();rows++)
                    {
                        // column and row titles
                        sheetA.addCell(new Label(0, rows, rows+""));
                        sheetA.addCell(new Label(1, rows, name.get(rows-1)));
                        sheetA.addCell(new Label(2, rows, CNIC.get(rows-1)));
                        sheetA.addCell(new Label(3, rows, checkinTime.get(rows-1)));
                        sheetA.addCell(new Label(4, rows, checkoutTime.get(rows-1)));
                        sheetA.addCell(new Label(5, rows, date.get(rows-1)));
                    }

                }




                // close workbook
                workbook.write();
                workbook.close();
                Toast.makeText(this, "File Saved in"+directory, Toast.LENGTH_SHORT).show();



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}