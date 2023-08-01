package userQueries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrissystem.Global;
import com.example.hrissystem.R;
import com.example.hrissystem.userEntry;
import com.example.hrissystem.Users;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.RecyclerViewChatHolder> {
    List<UserQuery> userQueries;

    public ChatAdapter(List<UserQuery> userQueries) {
        this.userQueries = userQueries;
    }

    @NonNull
    @Override
    public RecyclerViewChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new RecyclerViewChatHolder(chatView);
    }

    @Override
    public int getItemCount() {
        return userQueries.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewChatHolder holder, int position) {
        holder.user_name.setText(userQueries.get(position).user_name);
        holder.message_date.setText(userQueries.get(position).message_date);
        holder.message_content.setText(userQueries.get(position).message_content);

    }

    class RecyclerViewChatHolder extends RecyclerView.ViewHolder {
        TextView  user_name, message_date, message_content;

        public RecyclerViewChatHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.username);
            message_date = itemView.findViewById(R.id.msg_date);
            message_content = itemView.findViewById(R.id.msg_content);

        }
    }
}
