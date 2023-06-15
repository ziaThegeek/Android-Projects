package com.example.hrissystem;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Users extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> name, fName, CNIC, contact, designation, district,password;
    List<Boolean> adminUser;
    ListView listView_users;
    usersAdapter usersAdapter;
    private static final int REQUEST_CODE = 10;
    private static final int REQUST_PERMISSION_CODE = 11,REQUST_PERMISSION_CODE_EXCEL=12;
    boolean isDuplicate;
    List<userEntry> userEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("users");

        listView_users = findViewById(R.id.listview_users);

        name = new ArrayList<>();
        fName = new ArrayList<>();
        CNIC = new ArrayList<>();
        contact = new ArrayList<>();
        designation = new ArrayList<>();
        district = new ArrayList<>();
        password=new ArrayList<>();
        adminUser=new ArrayList<>();
        userEntries=new ArrayList<>();


        usersAdapter = new usersAdapter(this, name, fName, CNIC, contact, designation, district);

        loadUsers();

        listView_users.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showEditUserDialog(name.get(i),fName.get(i),CNIC.get(i),contact.get(i),designation.get(i),district.get(i),password.get(i),adminUser.get(i));
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_users:
                open_file_activity();
                return true;
            case R.id.add_user:
                showBottomSheetDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void importUsers(String filename) throws IOException, BiffException {

        Workbook wb;
        WorkbookSettings ws;
        InputStream is;
        Sheet sheet;
        ws = new WorkbookSettings();
        ws.setGCDisabled(true);
        is = new FileInputStream(filename);
        wb = Workbook.getWorkbook(is);
        sheet = wb.getSheet(0);
        int rows = sheet.getRows();
        int successfullRows=0,duplicateRows=0;
        //   Toast.makeText(context,""+sheet.getColumns(),Toast.LENGTH_LONG).show();
        if (sheet.getColumns()<8) {
            Toast.makeText(this, "Insufficient Data for Import ,Try Again", Toast.LENGTH_SHORT).show();

        }
        else {

            for (int i = 3; i < sheet.getRows(); i++) {
                Cell[] row = sheet.getRow(i);

                if (CNIC.contains(row[3].getContents())) {
                    //TODO Skip user
                    duplicateRows++;
                }
                else {
                    AllUsers users = new AllUsers(
                            row[1].getContents(),
                            row[2].getContents(),
                            row[3].getContents(),
                            row[4].getContents(),
                            row[5].getContents(),
                            row[6].getContents(),
                            row[7].getContents());

                    databaseReference.push().setValue(users);
                    successfullRows++;

                }


            }

                Toast.makeText(this, successfullRows + " Rows Imported Successfully " + duplicateRows + " Duplicate Rows Found", Toast.LENGTH_SHORT).show();


        }
    }



    public void open_file_activity() {
        if (ContextCompat.checkSelfPermission(Users.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent browse_file = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            browse_file.setType("application/vnd.ms-excel");
            startActivityForResult(browse_file, REQUEST_CODE);
        } else
            requestPermission();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            open_file_activity();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUST_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filepath = uri.getPath();
//                    filepath = filepath.substring(filepath.indexOf(":") + 1);
//                    String fileName = getFileName(uri);
                    String TEMP_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
//                    File file = new File(uri.toString());
                    File copyFile = new File(TEMP_DIR_PATH + "/" + get_file_path(uri));
//                    Toast.makeText(this, copyFile.toString(), Toast.LENGTH_SHORT).show();

                    try {
                        importUsers(copyFile.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (BiffException e) {
                        throw new RuntimeException(e);
                    }
                    super.onActivityResult(requestCode, resultCode, data);
                }
        }
    }


    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;

        String path = uri.getPath();
        if (path.contains("/"))
            path = path.substring(path.lastIndexOf(":") + 1);
        if (path.contains("/"))
            path = path.substring(path.lastIndexOf("/") + 1);
        return path;
    }

    public String get_file_path(Uri uri) {
        String path = uri.getPath();
        if (path.contains("/"))
            path = path.substring(path.lastIndexOf(":") + 1);
        return path;
    }

    public void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.clear();
                fName.clear();
                CNIC.clear();
                contact.clear();
                designation.clear();
                district.clear();
                password.clear();
                adminUser.clear();
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    AllUsers user = dsp.getValue(AllUsers.class);
                    name.add(user.name);
                    fName.add(user.fname);
                    CNIC.add(user.cnic);
                    contact.add(user.contact);
                    designation.add(user.designation);
                    district.add(user.district);
                    password.add(user.password);
                    adminUser.add(user.isAdmin);
                }
                listView_users.setAdapter(usersAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.add_user_dialog);
        bottomSheetDialog.show();
        TextView nameText=bottomSheetDialog.findViewById(R.id.name);
        TextView fnameText=bottomSheetDialog.findViewById(R.id.fname);
        TextView CNICText=bottomSheetDialog.findViewById(R.id.CNIC);
        TextView contactText=bottomSheetDialog.findViewById(R.id.contact);
        TextView designationText=bottomSheetDialog.findViewById(R.id.designation);
        TextView districtText=bottomSheetDialog.findViewById(R.id.district);
        TextView passwordText=bottomSheetDialog.findViewById(R.id.password);



        Button add_usr=bottomSheetDialog.findViewById(R.id.add_user);
        add_usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameText.getText().toString().isEmpty())
                {
                    Toast.makeText(Users.this, "Name cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else if(fnameText.getText().toString().isEmpty())
                {
                    Toast.makeText(Users.this, "Father Name cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else if(CNICText.getText().toString().isEmpty())
                {
                    Toast.makeText(Users.this, "CNIC cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else if(passwordText.getText().toString().isEmpty())
                {
                    Toast.makeText(Users.this, "Password cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AllUsers newUser=new AllUsers(
                            nameText.getText().toString().trim(),
                            fnameText.getText().toString().trim(),
                            CNICText.getText().toString().trim(),
                            contactText.getText().toString().trim(),
                            designationText.getText().toString().trim(),
                            districtText.getText().toString().trim(),
                            passwordText.getText().toString().trim()
                    );
                    if (CNIC.contains(CNICText.getText().toString().trim()))
                        Toast.makeText(Users.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                    else {
                        databaseReference.push().setValue(newUser);
                        Toast.makeText(Users.this, "User " + nameText.getText() + " Created SuccessFully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();

                    }
                }
            }
        });

    }
    private void showEditUserDialog(
            String name,
            String fName,
            String CNIC,
            String contact,
            String designation,
            String district,
            String password,
            boolean adminUser)
    {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.edit_user_dialog);

        EditText nameText=bottomSheetDialog.findViewById(R.id.name);
        EditText fNameText=bottomSheetDialog.findViewById(R.id.fname);
        EditText CNICText=bottomSheetDialog.findViewById(R.id.CNIC);
        EditText contactText=bottomSheetDialog.findViewById(R.id.contact);
        EditText designationText=bottomSheetDialog.findViewById(R.id.designation);
        EditText districtText=bottomSheetDialog.findViewById(R.id.district);
        EditText passwordText=bottomSheetDialog.findViewById(R.id.password);
        Button   exportActivity=bottomSheetDialog.findViewById(R.id.view_history);
        CheckBox isAdmin=bottomSheetDialog.findViewById(R.id.isAdmin);

        //----------------------------------------------//
        nameText.setText(name);
        fNameText.setText(fName);
        CNICText.setText(CNIC);
        contactText.setText(contact);
        designationText.setText(designation);
        districtText.setText(district);
        passwordText.setText(password);
        isAdmin.setChecked(adminUser);

        //----------------------------------------------//











        bottomSheetDialog.show();
        Button saveChanges=bottomSheetDialog.findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO save changes
                updateUserDetails(CNIC,
                        nameText.getText().toString().trim(),
                        fNameText.getText().toString().trim(),
                        designationText.getText().toString().trim(),
                        contactText.getText().toString().trim(),
                        districtText.getText().toString().trim(),
                        passwordText.getText().toString().trim(),
                        isAdmin.isChecked()
                );
                bottomSheetDialog.dismiss();
            }
        });

        Button delUser=bottomSheetDialog.findViewById(R.id.delete_user);

        delUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Users.this)
//set icon
                        .setIcon(android.R.drawable.ic_delete)

//set title
                        .setTitle("Delete User")
//set message
                        .setMessage("User will be Permanently deleted")
//set positive button
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                //TODO Delete User
                                delUser(CNIC);
                                bottomSheetDialog.dismiss();
                                Toast.makeText(Users.this,"User Deleted",Toast.LENGTH_LONG).show();
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


            }
        });
       exportActivity.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent userHistory=new Intent(Users.this,viewHistory.class);
               userHistory.putExtra("username",name);
               userHistory.putExtra("cnic",CNIC);
               startActivity(userHistory);
           }
       });


    }



    private void updateUserDetails(String CNIC,String name,String fname,String designation,String contact,String district,String password,boolean isAdmin) {
        Query query=databaseReference.orderByChild("cnic").equalTo(CNIC);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    AllUsers user= dsp.getValue(AllUsers.class);

                    if (CNIC.equals(user.getCnic().trim()))
                    {

                       dsp.getRef().child("name").setValue(name);
                       dsp.getRef().child("fname").setValue(fname);
                       dsp.getRef().child("designation").setValue(designation);
                       dsp.getRef().child("contact").setValue(contact);
                       dsp.getRef().child("district").setValue(district);
                       dsp.getRef().child("password").setValue(password);
                       dsp.getRef().child("admin").setValue(isAdmin);
                        Toast.makeText(Users.this, "user "+CNIC +" Updated Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Users.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void delUser(String CNIC)
    {
        Query query=databaseReference.orderByChild("cnic").equalTo(CNIC);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    AllUsers user = dsp.getValue(AllUsers.class);

                    if (CNIC.equals(user.getCnic().trim())) {
                        dsp.getRef().removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    // Handle errors
                                    Toast.makeText(Users.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Value deleted successfully
                                    System.out.println("User deleted successfully.");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

