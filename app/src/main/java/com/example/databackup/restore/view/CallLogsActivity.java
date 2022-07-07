package com.example.databackup.restore.view;

import static com.example.databackup.restore.view.DataOverviewActivity.EXTRA_DATA_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.databackup.backup.model.BackUpData;
import com.example.databackup.backup.model.CallLogModel;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.databinding.ActivityCallLogsBinding;
import com.example.databackup.databinding.ActivityContactsBinding;
import com.example.databackup.restore.view.adapter.CallLogsAdapter;
import com.example.databackup.restore.view.adapter.ContactsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CallLogsActivity extends AppCompatActivity {

    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    FirebaseUser mCurrentUser;
    StorageReference dataRef = storageRef.getReference();
    private CallLogsAdapter mAdapter;
    private ActivityCallLogsBinding binding;
    private long dataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_DATA_ID)) {
            dataId = receivedIntent.getLongExtra(EXTRA_DATA_ID, 0);
            Toast.makeText(this, "Data id: " + dataId, Toast.LENGTH_SHORT).show();
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        dataRef = dataRef.child("data/" + mCurrentUser.getEmail() + "/" + dataId + ".json");
        Toast.makeText(this, dataRef.getPath(), Toast.LENGTH_SHORT).show();

        mAdapter = new CallLogsAdapter(this, new ArrayList<CallLogModel>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        binding.rcvCallLogs.setAdapter(mAdapter);
        binding.rcvCallLogs.setLayoutManager(layoutManager);
        binding.rcvCallLogs.addItemDecoration(dividerItemDecoration);

        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        dataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String a = new String(bytes, StandardCharsets.UTF_8);
                BackUpData b = BackUpData.fromJson(a);
                mAdapter.getCurrentValues().clear();
                mAdapter.getCurrentValues().addAll(b.getCallLogs());
                mAdapter.notifyDataSetChanged();
                Toast.makeText(CallLogsActivity.this, a , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}