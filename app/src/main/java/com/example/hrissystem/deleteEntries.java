package com.example.hrissystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class deleteEntries extends AsyncTask<List<String>,Void,String> {
    Activity context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> dateRange;
    private ProgressDialog mProgressDialog;

    public deleteEntries(Activity context)
    {
        this.context=context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("userEntry");

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Delete Progress");
        mProgressDialog.setMessage("Deleting Records...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    @Override
    protected String doInBackground(List<String>... params) {
        dateRange=params[0];
        for (int i=0;i<dateRange.size();i++) {
            Query query = databaseReference.orderByChild("date").equalTo(dateRange.get(i));

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        userEntry usrEntry = dsp.getValue(userEntry.class);
                        if (dateRange.contains(usrEntry.getDate()))
                            dsp.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error != null) {
                                        // Handle errors
                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Value deleted successfully

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
        return "Deleted Entries Successfully";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
