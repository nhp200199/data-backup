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
import com.example.databackup.backup.model.SmsModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.ViewHolder>{

    private List<SmsModel> smsList;
    private LayoutInflater mInflater;

    public SmsAdapter(Context context, List<SmsModel> smsModelList) {
        this.mInflater = LayoutInflater.from(context);
        this.smsList = smsModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sms, parent, false);
        return new SmsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsModel smsModel = smsList.get(position);
        holder.tvSmsBody.setText(smsModel.getBody());

        try {
            Date date = new Date(smsModel.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = sdf.format(date);
            holder.tvSmsDate.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error when format date time");
            holder.tvSmsDate.setText("---");
        }

        String type;
        switch (smsModel.getType()) {
            case 1:
                type = "Nhận";
                break;
            case 2:
                type = "Gửi";
                break;
            default: type = "Không xác định";
        }
        holder.tvSmsType.setText(type);
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public List<SmsModel> getCurrentValues() {
        return smsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSmsType;
        TextView tvSmsDate;
        TextView tvSmsBody;

        ViewHolder(View itemView) {
            super(itemView);
            tvSmsType = itemView.findViewById(R.id.tv_sms_type);
            tvSmsDate = itemView.findViewById(R.id.tv_sms_date);
            tvSmsBody = itemView.findViewById(R.id.tv_sms_body);
        }
    }
}
