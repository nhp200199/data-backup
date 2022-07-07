package com.example.databackup.backup.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databackup.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private List<Long> mData = new ArrayList<Long>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecordsAdapter(Context context, List<Long> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            long timestamps = mData.get(position);
            Date date = new Date(timestamps);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = sdf.format(date);
            holder.tvRecordName.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error when format date time");
            holder.tvRecordName.setText("---");
        }
    }

    public List<Long> getCurrentValues() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvRecordName;

        ViewHolder(View itemView) {
            super(itemView);
            tvRecordName = itemView.findViewById(R.id.tv_record_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public long getItem(int id) {
        return mData.get(id);
    }
}


