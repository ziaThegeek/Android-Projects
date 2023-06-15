package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

public class viewHistory extends AppCompatActivity {
    private static final int REQUST_PERMISSION_CODE_EXCEL = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Query query;
    ArrayList<String> checkinTime,checkoutTime,date;

    String name,CNIC;
    listviewAdapter listviewAdapter;
    Intent userData;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        //-----------------initialize layout element----------------//start
        listView=findViewById(R.id.listview);
        //-----------------initialize layout element----------------//end


        //-----------------initialize local variables----------------//start
        date=new ArrayList<>();
        checkinTime=new ArrayList<>();
        checkoutTime=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("userEntry");
        listviewAdapter=new listviewAdapter(this,checkinTime,checkoutTime,date);
        //-----------------initialize local variables----------------//end

        // get data from Intent of Users Page
        userData=getIntent();
        name=userData.getStringExtra("username");
        CNIC=userData.getStringExtra("cnic");
        getSupportActionBar().setTitle(name);
        loadData(CNIC);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_history_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.export_to_excel:
                export2Excel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    private void loadData(String cnic) {
        query=databaseReference.orderByChild("cnic").equalTo(CNIC);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkinTime.clear();
                checkoutTime.clear();
                date.clear();


                for (DataSnapshot dsp:snapshot.getChildren())
                {
                    userEntry newUser=dsp.getValue(userEntry.class);
                    if (newUser.name.equals(name)) {
                        checkinTime.add(newUser.checkinTime);
                        checkoutTime.add(newUser.checkOutTime);
                        date.add(newUser.date);

                    }
                }
                listView.setAdapter(listviewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void export2Excel() {
        String permission= Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(viewHistory.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    viewHistory.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(viewHistory.this,
                        new String[]{permission}, REQUST_PERMISSION_CODE_EXCEL);

            } else {
                ActivityCompat.requestPermissions(viewHistory.this,
                        new String[]{permission}, REQUST_PERMISSION_CODE_EXCEL);
            }
        } else {
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = String.format("User %S %S Activity.xls",
                    name,
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

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

                    for(int rows=1;rows<=date.size();rows++)
                    {
                        // column and row titles
                        sheetA.addCell(new Label(0, rows, rows+""));
                        sheetA.addCell(new Label(1, rows, name));
                        sheetA.addCell(new Label(2, rows, CNIC));
                        sheetA.addCell(new Label(3, rows, checkinTime.get(rows-1)));
                        sheetA.addCell(new Label(4, rows, checkoutTime.get(rows-1)));
                        sheetA.addCell(new Label(5, rows, date.get(rows-1)));
                    }

                }




                // close workbook
                workbook.write();
                workbook.close();
                Toast.makeText(this, "File Saved in "+directory, Toast.LENGTH_SHORT).show();



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}