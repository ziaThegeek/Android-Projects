package com.example.hrissystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminScreen extends AppCompatActivity {
CardView users,userActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        users=findViewById(R.id.users);
        userActivity=findViewById(R.id.user_activity);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent users=new Intent(AdminScreen.this , Users.class);
                startActivity(users);

            }
        });
        userActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent users=new Intent(AdminScreen.this , userActivities.class);
                startActivity(users);
            }
        });


    }
}