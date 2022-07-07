package com.example.databackup.restore.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databackup.R;
import com.example.databackup.backup.model.CallLogModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.ViewHolder>{

    private List<CallLogModel> callLogs;
    private LayoutInflater mInflater;
    private Context context;

    public CallLogsAdapter(Context context, List<CallLogModel> logModels) {
        this.mInflater = LayoutInflater.from(context);
        this.callLogs = logModels;
        this.context = context;
    }

    @NonNull
    @Override
    public CallLogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_call_log, parent, false);
        return new CallLogsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogsAdapter.ViewHolder holder, int position) {
        CallLogModel callLog = callLogs.get(position);
        holder.tvCallLogNumber.setText(context.getString(R.string.call_log_item_txt_number_with_argument, callLog.getNumber()));
        holder.tvCallLogDuration.setText(context.getString(R.string.call_log_item_txt_duration_with_argument, String.valueOf(callLog.getDuration())));

        try {
            Date date = new Date(callLog.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = sdf.format(date);
            holder.tvCallLogDate.setText(context.getString(R.string.call_log_item_txt_date_with_argument, formattedDate));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error when format date time");
            holder.tvCallLogDate.setText(context.getString(R.string.call_log_item_txt_date_with_argument, "---"));
        }

        String type;
        switch (callLog.getCallType()) {
            case 1:
                type = "Nhận";
                break;
            case 2:
                type = "Gọi";
                break;
            case 3:
                type = "Cuộc gọi nhỡ";
                break;
            default: type = "Không xác định";
        }
        holder.tvCallLogType.setText(context.getString(R.string.call_log_item_txt_type_with_argument, type));
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public List<CallLogModel> getCurrentValues() {
        return callLogs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCallLogType;
        TextView tvCallLogDate;
        TextView tvCallLogNumber;
        TextView tvCallLogDuration;

        ViewHolder(View itemView) {
            super(itemView);
            tvCallLogType = itemView.findViewById(R.id.tv_call_log_type);
            tvCallLogDate = itemView.findViewById(R.id.tv_call_log_date);
            tvCallLogNumber = itemView.findViewById(R.id.tv_call_log_number);
            tvCallLogDuration = itemView.findViewById(R.id.tv_call_log_duration);
        }
    }
}