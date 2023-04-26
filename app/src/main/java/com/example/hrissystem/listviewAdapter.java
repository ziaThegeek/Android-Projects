package com.example.hrissystem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Array;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class listviewAdapter extends ArrayAdapter {
    List<String> checkinTime,checkoutTime,name;

    List<String> currentDate;
    Activity context;
    TextView checkinTimeText,checkoutTimeText,currentDateText,username;

    public listviewAdapter(Activity context,List<String> checkinTime, List<String> checkoutTime, List<String> currentDate,List<String> name) {
        super(context,0,checkinTime);
        this.context=context;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
        this.currentDate = currentDate;
        this.name=name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitemcatlog, parent, false);
        }

        checkinTimeText=convertView.findViewById(R.id.checkinTime);
        checkoutTimeText=convertView.findViewById(R.id.checkoutTime);
        currentDateText=convertView.findViewById(R.id.currentdate);
        username=convertView.findViewById(R.id.username);

        checkinTimeText.setText(checkinTime.get(position));
        checkoutTimeText.setText(checkoutTime.get(position));
        currentDateText.setText(currentDate.get(position));
        username.setText(name.get(position));

        return convertView;
    }

}
