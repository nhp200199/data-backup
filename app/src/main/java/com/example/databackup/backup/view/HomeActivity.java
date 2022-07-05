package com.example.databackup.backup.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.databackup.BaseActivity;
import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.auth.view.LoginActivity;
import com.example.databackup.backup.repository.RecordsRepository;
import com.example.databackup.backup.viewmodel.HomeViewModel;
import com.example.databackup.databinding.ActivityHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends BaseActivity {
    public static final String EXTRA_USER_EMAIL = "user_email";
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 444;
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
        binding.btnBackUp.setOnClickListener(v -> {
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS,     ACTION_REQUEST_PERMISSIONS);
            }
            else {
                new RecordsRepository().backUpData(HomeActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
        }
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                new RecordsRepository().backUpData(HomeActivity.this);
            } else {
                Toast.makeText(this, "Bạn cần cho quyền để thực hiện tác vụ", Toast.LENGTH_SHORT).show();
            }
        }
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