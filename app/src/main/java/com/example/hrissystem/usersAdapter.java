package com.example.hrissystem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class usersAdapter extends ArrayAdapter {
    List<String> name,fname,CNIC,contact,Designation,district;
    TextView nameText,fNameText,CNICText;
    Activity context;
    public usersAdapter( Activity context,  List<String> name, List<String> fname, List<String> CNIC, List<String> contact, List<String> designation, List<String> district) {
        super(context,0,name);
        this.context=context;
        this.name = name;
        this.fname = fname;
        this.CNIC = CNIC;
        this.contact = contact;
        Designation = designation;
        this.district = district;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null)
        {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.users_list_view,parent,false);

        }
        nameText=convertView.findViewById(R.id.Name);
        fNameText=convertView.findViewById(R.id.FName);
        CNICText=convertView.findViewById(R.id.CNIC);

        nameText.setText(name.get(position));
        fNameText.setText(fname.get(position));
        CNICText.setText(CNIC.get(position));

        return convertView;
    }
}
