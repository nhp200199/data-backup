package com.example.databackup.backup.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.auth.view.LoginActivity;
import com.example.databackup.backup.viewmodel.HomeViewModel;
import com.example.databackup.databinding.ActivityHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_USER_EMAIL = "user_email";
    private String userEmail = "...";
    private ActivityHomeBinding binding;
    private HomeViewModel mHomeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra(EXTRA_USER_EMAIL)) {
            userEmail = receivedIntent.getStringExtra(EXTRA_USER_EMAIL);
        }

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.init(this);

        binding.tvWelcomeUserEmail.setText(String.format(getString(R.string.home_activity_txt_welcome_user_with_argument), userEmail));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.opt_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        mHomeViewModel.signOut(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}