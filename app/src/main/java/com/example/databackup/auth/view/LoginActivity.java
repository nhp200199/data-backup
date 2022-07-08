package com.example.databackup.auth.view;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.databackup.BaseActivity;
import com.example.databackup.R;
import com.example.databackup.backup.view.HomeActivity;
import com.example.databackup.auth.viewmodel.LoginViewModel;
import com.example.databackup.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int SIGN_IN_REQUEST_CODE = 123;
    private ActivityMainBinding binding;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.login_activity_title);

        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mLoginViewModel.init(this);

        if (mLoginViewModel.isUserSignedIn()) {
            navigateToHome(mLoginViewModel.getFirebaseUser());
        }

        setUpObservables();

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            Intent googleSignInIntent = mLoginViewModel.getGoogleSignInIntent();
            if (googleSignInIntent != null) {
                startActivityForResult(googleSignInIntent, SIGN_IN_REQUEST_CODE);
            }
        });
    }

    private void setUpObservables() {
        mLoginViewModel.getAuthenticatedUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                navigateToHome(firebaseUser);
            }
        });

        mLoginViewModel.getLoginStatusLiveData().observe(this, loginStatus -> {
            switch (loginStatus) {
                case IN_PROGRESS:
                    showLoadingDialog();
                    break;
                case SUCCESS:
                    hidePopup();
                    Toast.makeText(this, getString(R.string.toast_operation_success), Toast.LENGTH_SHORT).show();
                    break;
                case FAIL:
                    showInformationPopup(getString(R.string.operation_popup_title_fail), getString(R.string.operation_popup_msg_fail));
                    break;
                default: break;
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
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            mLoginViewModel.login(this, data);
        }
    }
}