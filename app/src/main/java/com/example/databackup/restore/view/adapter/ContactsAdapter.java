package com.example.databackup.restore.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databackup.R;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.backup.view.adapter.RecordsAdapter;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contact> contacts;
    private LayoutInflater mInflater;
    private Context context;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_contact, parent, false);
        return new ContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.tvContactName.setText(context.getString(R.string.contact_item_txt_name_with_argument, contact.getName()));
        holder.tvContactNumber.setText(context.getString(R.string.contact_item_txt_number_with_argument, contact.getPhoneNumber()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<Contact> getCurrentContacts() {
        return contacts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName;
        TextView tvContactNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvContactNumber = itemView.findViewById(R.id.tv_contact_number);
        }
    }
}
