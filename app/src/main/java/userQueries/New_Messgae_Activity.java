package userQueries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrissystem.AllUsers;
import com.example.hrissystem.Global;
import com.example.hrissystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class New_Messgae_Activity extends AppCompatActivity {
AutoCompleteTextView users;
EditText msg_input;
Button send_button;
FirebaseDatabase firebaseDatabase;
DatabaseReference userRefrence,queriesRefrence;
ArrayList<String> names,CNICs;
ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_msg_item);
        users=findViewById(R.id.username);
        msg_input=findViewById(R.id.message_input);
        send_button=findViewById(R.id.send_button);
        firebaseDatabase=FirebaseDatabase.getInstance();
        queriesRefrence=firebaseDatabase.getReference(getResources().getString(R.string.userQueries));
        userRefrence=firebaseDatabase.getReference("users");
        names=new ArrayList<>();
        CNICs=new ArrayList();

        loadData();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,names);
        users.setAdapter(adapter);
        //adapter for username

        send_button.setOnClickListener(view -> {
            if (msg_input.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please type message", Toast.LENGTH_SHORT).show();
            } else if (users.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please mention someone to send msg", Toast.LENGTH_SHORT).show();
            }
            else
            {
                UserQuery newQuery=new UserQuery(CNICs.get(names.indexOf(users.getText().toString().trim())),users.getText().toString(), Global.getCurrentDate(),msg_input.getText().toString());
                queriesRefrence.push().setValue(newQuery);
                finish();
            }
        });



    }

    private void loadData() {
        userRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    AllUsers user=dsp.getValue(AllUsers.class);
                    names.add(user.getName());
                    CNICs.add(user.getCnic());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

