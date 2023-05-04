package com.example.hrissystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class userLogin extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
Button login;
EditText username,password;
TextView forgotPassword,viewpass;
Intent mainActivity;
Intent adminScreen;
String password1,name,cnic;
Boolean isAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("users");


        login=findViewById(R.id.login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        forgotPassword=findViewById(R.id.forgotPassword);
        viewpass=findViewById(R.id.viewpass);

        mainActivity=new Intent(this,MainActivity.class);
        adminScreen=new Intent(this,AdminScreen.class);


        viewpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getInputType()==InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else
                    password.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(userLogin.this, "Username must be filled in", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(userLogin.this, "Password must be filled in", Toast.LENGTH_SHORT).show();
                }
                else {
//                    if (username.getText().toString().trim().equals("Admin HR")  && password.getText().toString().trim().equals("2021-1721/23"))
//                    {
//                        Toast.makeText(userLogin.this, "Admin Login", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                    else {

                        Query query=databaseReference.orderByChild("cnic").equalTo(username.getText().toString().trim());
                        password1="";
                        cnic="";
                        name="";
                        isAdmin=false;

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dsp : snapshot.getChildren()) {
                                    AllUsers user= dsp.getValue(AllUsers.class);
                                    password1 = user.getPassword();
                                    if (password1.equals(password.getText().toString().trim()))
                                    {
                                        name=user.getName();
                                        isAdmin=user.isAdmin();
                                        cnic=username.getText().toString().trim();
                                        break;
                                    }
                                }
                                    
                                    if (password1.equals(password.getText().toString().trim()))
                                    {
                                        if (isAdmin)
                                        {

                                            Toast.makeText(userLogin.this, "Admin Login", Toast.LENGTH_SHORT).show();
                                            startActivity(adminScreen);

                                        }
                                        else
                                        {
                                            mainActivity.putExtra("username", name);
                                            mainActivity.putExtra("cnic", cnic);
                                            startActivity(mainActivity);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(userLogin.this, "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                                    }
                                
                                
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                       

                }
            }
        });

    }
}