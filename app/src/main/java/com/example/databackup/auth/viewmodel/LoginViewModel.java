package com.example.databackup.auth.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.auth.view.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getSimpleName();
    private AuthRepository mAuthRepository;
    private LiveData<AuthRepository.LoginStatus> loginStatusLiveData;

    public void init(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
        loginStatusLiveData = mAuthRepository.getLoginStatusLiveData();
    }

    public boolean isUserSignedIn() {
        return mAuthRepository.isUserSignedIn();
    }

    public Intent getGoogleSignInIntent() {
        return mAuthRepository.getGoogleSignInIntent();
    }

    public FirebaseUser getFirebaseUser() {
        return mAuthRepository.getFirebaseUser();
    }

    public void firebaseAuthWithGoogle(Context context, GoogleSignInAccount act) {
        mAuthRepository.firebaseAuthWithGoogle(context, act);
    }

    public LiveData<FirebaseUser> getAuthenticatedUser() {
        return mAuthRepository.getAuthenticatedUser();
    }

    public LiveData<AuthRepository.LoginStatus> getLoginStatusLiveData() {
        return loginStatusLiveData;
    }
}
