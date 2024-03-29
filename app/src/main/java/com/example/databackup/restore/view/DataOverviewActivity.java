package com.example.databackup.restore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.databackup.R;
import com.example.databackup.databinding.ActivityDataOverviewBinding;

public class DataOverviewActivity extends AppCompatActivity {
    public static final String EXTRA_DATA_ID = "data-id";

    private ActivityDataOverviewBinding binding;
    private long dataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.data_overview_activity_title);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_DATA_ID)) {
            dataId = receivedIntent.getLongExtra(EXTRA_DATA_ID, 0);
        }

        binding.callLogsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(DataOverviewActivity.this, CallLogsActivity.class);
            intent.putExtra(EXTRA_DATA_ID, dataId);
            startActivity(intent);
        });

        binding.smsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(DataOverviewActivity.this, SmsActivity.class);
            intent.putExtra(EXTRA_DATA_ID, dataId);
            startActivity(intent);
        });

        binding.contactsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(DataOverviewActivity.this, ContactsActivity.class);
            intent.putExtra(EXTRA_DATA_ID, dataId);
            startActivity(intent);
        });
    }
}