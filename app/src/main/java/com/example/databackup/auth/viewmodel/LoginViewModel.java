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
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private MutableLiveData<FirebaseUser> mAuthenticatedUser = new MutableLiveData<FirebaseUser>();

    public void init(Context context) {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public boolean isUserSignedIn() {
        if (mAuth != null) {
            return mAuth.getCurrentUser() != null;
        }
        else {
            Log.e(TAG, "Operation fail. Authentication was called on null object");
            return false;
        }
    }

    public Intent getGoogleSignInIntent() {
        if (mGoogleSignInClient == null) {
            Log.e(TAG, "Operation fail. Get Google's intent was called on null object");
            return null;
        }
        else {
            return mGoogleSignInClient.getSignInIntent();
        }
    }

    public void signOut(Context context) {
        if (context instanceof Activity)
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        else {
            Log.e(TAG, "Operation fail. You must pass the activity as the context");
        }
    }

    public FirebaseUser getFirebaseUser() {
        if (mAuth == null) {
            Log.e(TAG, "Operation fail. Authentication was called on null object");
            return null;
        }
        else {
            return mAuth.getCurrentUser();
        }
    }

    public void firebaseAuthWithGoogle(Context context, GoogleSignInAccount act) {
        if (context instanceof Activity) {
            AuthCredential credential = GoogleAuthProvider.getCredential(act.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener((Activity) context, authResult -> {
                        Log.i(TAG, "Successfully sign user in");
                        Log.i(TAG, "User's sign in email: " + authResult.getUser().getEmail());
                        mAuthenticatedUser.setValue(authResult.getUser());
                    })
                    .addOnFailureListener((Activity) context, e -> {
                        Log.e(TAG, "Failed to authenticate with google. Error is: " + e);
                    });
        }
        else {
            Log.e(TAG, "Operation fail. You must pass the activity as the context");
        }
    }

    public LiveData<FirebaseUser> getAuthenticatedUser() {
        return mAuthenticatedUser;
    }
}
