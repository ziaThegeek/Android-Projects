package com.example.hrissystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;



import userQueries.UserQueries;

public class AdminScreen extends AppCompatActivity {
CardView users,userActivity,leaveApplications,user_queries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        users=findViewById(R.id.users);
        userActivity=findViewById(R.id.user_activity);
        leaveApplications=findViewById(R.id.leaves);
        user_queries=findViewById(R.id.user_queries);
        users.setOnClickListener(view -> {
            Intent users=new Intent(AdminScreen.this , Users.class);
            startActivity(users);

        });
        userActivity.setOnClickListener(view -> {
            Intent users=new Intent(AdminScreen.this , userActivities.class);
            startActivity(users);
        });
        leaveApplications.setOnClickListener(view -> {
            Intent leaves=new Intent(AdminScreen.this , leaveApplications.leave_applications.class);
            startActivity(leaves);
        });
        user_queries.setOnClickListener(view -> {
            Intent queries=new Intent(AdminScreen.this , UserQueries.class);
            startActivity(queries);
        });

    }


}