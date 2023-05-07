package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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
            case R.id.delete_entries:
                TextView fromDateText,toDateText;
                Button   fromDateBtn,toDateBtn;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.delete_entries_dialog, null);
                fromDateText=dialogView.findViewById(R.id.from_date);
                toDateText=dialogView.findViewById(R.id.to_date);
                fromDateBtn=dialogView.findViewById(R.id.select_fromDate_btn);
                toDateBtn=dialogView.findViewById(R.id.select_todate_btn);
                fromDateText.setText(getCurrentDate());
                toDateText.setText(getCurrentDate());
                fromDateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setDateFromDialog(fromDateText);
                    }
                });
                toDateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setDateFromDialog(toDateText);
                    }
                });

                builder.setView(dialogView);
                builder.setTitle("Delete Entries");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the button click
                        deleteEntries deleteEntries=new deleteEntries(userActivities.this);
                        deleteEntries.execute(fromDateText.getText().toString().trim(),
                                toDateText.getText().toString().trim());

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the button click
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDateFromDialog(TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(userActivities.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        textView.setText(String.format("%02d",dayOfMonth) + "-" + String.format("%02d",monthOfYear+1) + "-" + String.format("%04d",year));

                    }
                }, getYear(textView.getText().toString().trim()),
                getMonth(textView.getText().toString().trim()),
                getDay(textView.getText().toString())

                );

        datePickerDialog.show();

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
        EditText dateText=bottomSheetDialog.findViewById(R.id.date);

        nameText.setText(name);
        CNICText.setText(CNIC);
        dateText.setText(date);

        Button checkInLocation=bottomSheetDialog.findViewById(R.id.checkin_location);
        Button checkOutLocation=bottomSheetDialog.findViewById(R.id.checkout_location);
        Button close=bottomSheetDialog.findViewById(R.id.close);
        Button updateEntry=bottomSheetDialog.findViewById(R.id.save_changes);
        Button deleteEntry=bottomSheetDialog.findViewById(R.id.deleteEntry);

dateText.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(userActivities.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dateText.setText(String.format("%02d",dayOfMonth) + "-" + String.format("%02d",monthOfYear+1) + "-" + String.format("%04d",year));

                    }
                }, Integer.parseInt(dateText.getText().toString().substring(6)),
                Integer.parseInt(dateText.getText().toString().substring(3,5))-1,
                Integer.parseInt(dateText.getText().toString().substring(0,2)));

        datePickerDialog.show();
    }
});

        checkInLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open Map for check in
                if (checkinLatitude!=0.0 && checkinlogitude!=0.0)
                {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", checkinLatitude, checkinlogitude, nameText.getText().toString());
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
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", checkoutLatitude, checkoutlongitude, nameText.getText().toString());
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
        updateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEntry(CNIC,date,dateText.getText().toString().trim());
                Toast.makeText(userActivities.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(userActivities.this)
//set icon
                        .setIcon(android.R.drawable.ic_delete)

//set title
                        .setTitle("Delete Entry")
//set message
                        .setMessage("Entry Will be Deleted Permanently")
//set positive button
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                //TODO Delete User
                                deleteEntry(CNIC,date);
                                bottomSheetDialog.dismiss();
                                Toast.makeText(userActivities.this,"Entry Deleted",Toast.LENGTH_LONG).show();
                            }
                        })
//set negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked

                            }
                        })
                        .show();

                deleteEntry(CNIC,date);
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
    public void updateEntry(String CNIC,String date,String newDate)
    {
        Query query=databaseReference.orderByChild("cnic").equalTo(CNIC);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                userEntry userEntry=dsp.getValue(userEntry.class);
                if (CNIC.equals(userEntry.getCnic()) && date.equals(userEntry.getDate()))
                    dsp.getRef().child("date").setValue(newDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void deleteEntry(String CNIC,String date)
    {
        Query query=databaseReference.orderByChild("cnic").equalTo(CNIC);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    userEntry userEntry=dsp.getValue(com.example.hrissystem.userEntry.class);
                    if (CNIC.equals(userEntry.getCnic()) && date.equals(userEntry.getDate()))
                        dsp.getRef().removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    // Handle errors
                                    Toast.makeText(userActivities.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Value deleted successfully
                                    System.out.println("User deleted successfully.");
                                }
                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getCurrentDate() {


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new java.util.Date());
        return currentDate;
    }
    private Integer getDay(String date)
    {
        return Integer.parseInt(date.substring(0,2));
    }
    private Integer getMonth(String date)
    {
      return   Integer.parseInt(date.substring(3,5))-1;
    } private Integer getYear(String date)
    {
       return Integer.parseInt(date.substring(6));
    }


}