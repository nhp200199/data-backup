package com.example.databackup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.databackup.databinding.ActivityHomeBinding;
import com.example.databackup.databinding.ActivityMainBinding;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_USER_EMAIL = "user_email";
    private String userEmail = "...";
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_USER_EMAIL)) {
            userEmail = receivedIntent.getStringExtra(EXTRA_USER_EMAIL);
        }

        binding.tvWelcomeUserEmail.setText(String.format(getString(R.string.home_activity_txt_welcome_user_with_argument), userEmail));
    }
}