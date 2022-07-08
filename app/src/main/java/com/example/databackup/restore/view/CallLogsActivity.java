package com.example.databackup.restore.view;

import static com.example.databackup.restore.view.DataOverviewActivity.EXTRA_DATA_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.databackup.BaseActivity;
import com.example.databackup.R;
import com.example.databackup.backup.model.BackUpData;
import com.example.databackup.backup.model.CallLogModel;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.databinding.ActivityCallLogsBinding;
import com.example.databackup.databinding.ActivityContactsBinding;
import com.example.databackup.restore.view.adapter.CallLogsAdapter;
import com.example.databackup.restore.view.adapter.ContactsAdapter;
import com.example.databackup.util.System;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CallLogsActivity extends BaseActivity {

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
        setTitle(R.string.call_log_activity_title);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_DATA_ID)) {
            dataId = receivedIntent.getLongExtra(EXTRA_DATA_ID, 0);
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        dataRef = dataRef.child("data/" + mCurrentUser.getEmail() + "/" + dataId + ".json");

        mAdapter = new CallLogsAdapter(this, new ArrayList<CallLogModel>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        binding.rcvCallLogs.setAdapter(mAdapter);
        binding.rcvCallLogs.setLayoutManager(layoutManager);
        binding.rcvCallLogs.addItemDecoration(dividerItemDecoration);

        fetchCallLogsData();
    }

    private void fetchCallLogsData() {
        if (System.hasNetwork(this)) {
            showLoadingDialog();

            final long ONE_MEGABYTE = 5 * 1024 * 1024;
            dataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    hidePopup();

                    String a = new String(bytes, StandardCharsets.UTF_8);
                    BackUpData b = BackUpData.fromJson(a);
                    mAdapter.getCurrentValues().clear();
                    mAdapter.getCurrentValues().addAll(b.getCallLogs());
                    mAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hidePopup();
                    // Handle any errors
                    showInformationPopup(getString(R.string.operation_popup_title_fail), getString(R.string.operation_popup_msg_fail));
                }
            });
        }
        else {
            showInformationPopup(getString(R.string.operation_popup_title_fail), getString(R.string.operation_popup_msg_fail));
        }
    }
}