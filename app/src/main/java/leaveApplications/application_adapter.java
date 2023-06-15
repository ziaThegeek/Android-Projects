package leaveApplications;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hrissystem.R;

import java.util.List;

public class application_adapter extends ArrayAdapter {
    List<String> name,fromDate,toDate,leave_type;
    List<Boolean> approved;
    TextView nameText,fromDateText,toDateText,leaveTypeText,approvedText;
    Activity context;

    public application_adapter(Activity context,List<String> name,List<String> type,List<String> fromDate,List<String> toDate,List<Boolean> approved)
    {
        super(context, R.layout.leave_applications_layout,name);
        this.context=context;
        this.name=name;
        this.leave_type=type;
        this.fromDate=fromDate;
        this.toDate=toDate;
        this.approved=approved;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View application_view, @NonNull ViewGroup parent) {
   if (application_view==null)
   {
       application_view= LayoutInflater.from(getContext()).inflate(R.layout.leave_applications_layout,parent,false);

   }
   nameText=application_view.findViewById(R.id.username);
   fromDateText=application_view.findViewById(R.id.fromDate);
   toDateText=application_view.findViewById(R.id.toDate);
   leaveTypeText=application_view.findViewById(R.id.leaveType);
   approvedText=application_view.findViewById(R.id.approval_status);

   nameText.setText(name.get(position));
   fromDateText.setText(fromDate.get(position));
   toDateText.setText(toDate.get(position));
   leaveTypeText.setText(leave_type.get(position));
   approvedText.setText(approved.get(position)==true?"Approved":"Not Approved");
   return application_view;
    }
}
