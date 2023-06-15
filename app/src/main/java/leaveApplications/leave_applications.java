package leaveApplications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrissystem.R;
import com.example.hrissystem.userActivities;
import com.example.hrissystem.userEntry;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class leave_applications extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> name, cnic, fromDate, toDate, reason, leave_type,datePosted;
    List<Boolean> approved;
    ListView listView;
    application_adapter application_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_applications);


        //---------------------------------------------------------//
        name = new ArrayList<>();
        cnic = new ArrayList<>();
        leave_type = new ArrayList<>();
        fromDate = new ArrayList<>();
        toDate = new ArrayList<>();
        reason = new ArrayList<>();
        approved = new ArrayList<>();
        datePosted=new ArrayList<>();
        //--------------------------------------------------------//


        //--------------------------------------------------------//
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("leaves");
        listView = findViewById(R.id.listview);
        application_adapter = new application_adapter(this, name, leave_type, fromDate, toDate, approved);
        //--------------------------------------------------------//
        loadDate();

        //-------------------------------------Leave Approval----------------------------//start

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                leave_application_dialog(name.get(i),cnic.get(i),datePosted.get(i),approved.get(i));


                return false;

            }
        });
        //-------------------------------------Leave Approval----------------------------//end

    }

    private void leave_application_dialog(String name, String cnic, String datePosted, Boolean approval_status) {

        TextView nameText,cnicText,datePostedText;
        CheckBox approved_status;
        Button save_changes,delete,close;
        final BottomSheetDialog leave_bottom_dialog=new BottomSheetDialog(this);
    leave_bottom_dialog.setContentView(R.layout.leave_application_action_dialog);
    nameText=leave_bottom_dialog.findViewById(R.id.name);
    cnicText=leave_bottom_dialog.findViewById(R.id.CNIC);
    datePostedText=leave_bottom_dialog.findViewById(R.id.date);
    approved_status=leave_bottom_dialog.findViewById(R.id.approved);
    save_changes=leave_bottom_dialog.findViewById(R.id.save_changes);
    delete=leave_bottom_dialog.findViewById(R.id.deleteEntry);
    close=leave_bottom_dialog.findViewById(R.id.close);

    nameText.setText(name);
    cnicText.setText(cnic);
    datePostedText.setText(datePosted);
    approved_status.setChecked(approval_status);
    approved_status.setText(approval_status?"Approved":"Not Approved");
    leave_bottom_dialog.show();
    approved_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b)
        {
            approved_status.setText("Approved");
        }
        else {
            approved_status.setText(" Not Approved");
        }
    }
});
    save_changes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateLeave(cnic,datePosted,approved_status.isChecked());

        }

    }

    );
    delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //----------------------Delete---------------------//start
            Query query=databaseReference.orderByChild("cnic").equalTo(cnic);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        LeaveApplication application=dsp.getValue(LeaveApplication.class);
                        if (cnic.equals(application.getCnic()) && datePosted.equals(application.getDatePosted()))
                            dsp.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error != null) {
                                        // Handle errors
                                        Toast.makeText(leave_applications.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Value deleted successfully
                                        System.out.println("deleted successfully.");
                                    }
                                }
                            });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            leave_bottom_dialog.dismiss();
            //----------------------Delete---------------------//end

        }
    });
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            leave_bottom_dialog.dismiss();
        }
    });
        datePostedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(leave_applications.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                datePostedText.setText(String.format("%02d",dayOfMonth) + "-" + String.format("%02d",monthOfYear+1) + "-" + String.format("%04d",year));

                            }
                        }, Integer.parseInt(datePostedText.getText().toString().substring(6)),
                        Integer.parseInt(datePostedText.getText().toString().substring(3,5))-1,
                        Integer.parseInt(datePostedText.getText().toString().substring(0,2)));

            }
        });

    }

    private void updateLeave(String cnic, String datePosted, boolean checked) {
        //--------------------update leave-------------------//start

        Query query=databaseReference.orderByChild("cnic").equalTo(cnic);
        query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for (DataSnapshot dsp : snapshot.getChildren()) {
                                                LeaveApplication application=dsp.getValue(LeaveApplication.class);
                                                if (cnic.equals(application.getCnic()) &&
                                                        datePosted.equals(application.getDatePosted())) {
                                                    dsp.getRef().child("approved").setValue(checked);
                                                    break;

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    }
        );
        //--------------------update leave-------------------//start
    }


    private void loadDate() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearLists();
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    LeaveApplication application = dsp.getValue(LeaveApplication.class);
                    name.add(application.getName());
                    cnic.add(application.getCnic());
                    leave_type.add(application.getLeaveType());
                    fromDate.add(application.getFromDate());
                    toDate.add(application.getToDate());
                    reason.add(application.getReason());
                    approved.add(application.isApproved());
                    datePosted.add(application.getDatePosted());
                }
                listView.setAdapter(application_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearLists() {
        name.clear();
        cnic.clear();
        leave_type.clear();
        fromDate.clear();
        toDate.clear();
        reason.clear();
        approved.clear();
        datePosted.clear();
    }

}