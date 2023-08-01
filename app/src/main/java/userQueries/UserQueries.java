package userQueries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrissystem.Global;
import com.example.hrissystem.R;
import com.example.hrissystem.userEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserQueries extends AppCompatActivity {
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_queries);
        recyclerView=findViewById(R.id.recycler_view);
        newMessage=findViewById(R.id.newMessage);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference(getResources().getString(R.string.userQueries));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadDate();
        newMessage.setOnClickListener(View->{
            Intent new_msg=new Intent(UserQueries.this,New_Messgae_Activity.class);
            startActivity(new_msg);
        });
    }

    public void loadDate() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserQuery> userQueries=new ArrayList<>();
                for (DataSnapshot dsp : snapshot.getChildren()) {

                    UserQuery newQuery = dsp.getValue(UserQuery.class);
                        userQueries.add(newQuery);
                }
                chatAdapter=new ChatAdapter(userQueries);
                recyclerView.setAdapter(chatAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}