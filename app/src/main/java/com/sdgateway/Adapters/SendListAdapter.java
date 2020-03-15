package com.sdgateway.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdgateway.Modules.Models.Message.FetchMessageResponse;
import com.sdgateway.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Callback;

public class SendListAdapter extends RecyclerView.Adapter<SendListAdapter.SendListHolder> {

    private Context mContext;
    private List<FetchMessageResponse> sendList;

    public SendListAdapter(Context context, List<FetchMessageResponse> list) {
        sendList = list;
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public SendListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_layout, parent, false);
        return new SendListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SendListHolder holder, int position) {
        FetchMessageResponse status = sendList.get(position);

        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa MM/dd/yy");

        holder.message.setText(String.format("New message for : %s => %s", status.getPhoneNumber(), sdf.format(cal.getTime())));
    }

    @Override
    public int getItemCount() {
        return sendList.size();
    }

    public class SendListHolder extends RecyclerView.ViewHolder {
        TextView message;

        public SendListHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.info);
        }
    }
}
