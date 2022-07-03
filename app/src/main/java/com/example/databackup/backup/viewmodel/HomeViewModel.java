package com.example.databackup.backup.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.databackup.auth.repository.AuthRepository;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private AuthRepository mAuthRepository;

    public void init(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
    }

    public void signOut(Context context) {
        mAuthRepository.signOut(context);
    }
}
