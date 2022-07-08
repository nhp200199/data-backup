package com.example.databackup.restore.view;

import static com.example.databackup.restore.view.DataOverviewActivity.EXTRA_DATA_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.example.databackup.BaseActivity;
import com.example.databackup.R;
import com.example.databackup.backup.model.BackUpData;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.backup.model.SmsModel;
import com.example.databackup.databinding.ActivitySmsBinding;
import com.example.databackup.restore.view.adapter.ContactsAdapter;
import com.example.databackup.restore.view.adapter.SmsAdapter;
import com.example.databackup.util.System;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SmsActivity extends BaseActivity {

    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    FirebaseUser mCurrentUser;
    StorageReference dataRef = storageRef.getReference();
    private SmsAdapter mAdapter;
    private ActivitySmsBinding binding;
    private long dataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.sms_activity_title);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_DATA_ID)) {
            dataId = receivedIntent.getLongExtra(EXTRA_DATA_ID, 0);
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        dataRef = dataRef.child("data/" + mCurrentUser.getEmail() + "/" + dataId + ".json");

        mAdapter = new SmsAdapter(this, new ArrayList<SmsModel>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        binding.rcvSms.setAdapter(mAdapter);
        binding.rcvSms.setLayoutManager(layoutManager);
        binding.rcvSms.addItemDecoration(dividerItemDecoration);

        fetchSmsData();
    }

    private void fetchSmsData() {
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
                    mAdapter.getCurrentValues().addAll(b.getSmsList());
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