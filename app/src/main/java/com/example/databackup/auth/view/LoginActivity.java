package com.example.databackup.auth.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.databackup.backup.view.HomeActivity;
import com.example.databackup.auth.viewmodel.LoginViewModel;
import com.example.databackup.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int SIGN_IN_REQUEST_CODE = 123;
    private ActivityMainBinding binding;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mLoginViewModel.init(this);

        if (mLoginViewModel.isUserSignedIn()) {
            navigateToHome(mLoginViewModel.getFirebaseUser());
        }

        mLoginViewModel.getAuthenticatedUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                navigateToHome(firebaseUser);
            }
        });

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            Intent googleSignInIntent = mLoginViewModel.getGoogleSignInIntent();
            if (googleSignInIntent != null) {
                startActivityForResult(googleSignInIntent, SIGN_IN_REQUEST_CODE);
            }
        });
    }

    private void navigateToHome(FirebaseUser currentUser) {
        Intent homeActivityIntent = new Intent(this, HomeActivity.class);
        homeActivityIntent.putExtra(HomeActivity.EXTRA_USER_EMAIL, currentUser.getEmail());
        startActivity(homeActivityIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mLoginViewModel.firebaseAuthWithGoogle(this, account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
}