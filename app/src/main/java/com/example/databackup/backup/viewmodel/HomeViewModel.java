package com.example.databackup.backup.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.backup.repository.RecordsRepository;

import io.reactivex.Observable;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private AuthRepository mAuthRepository;
    private RecordsRepository mRecordsRepository;
    private Observable<RecordsRepository.BackUpStatus> backUpStatusObservable;

    public void init(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
        mRecordsRepository = new RecordsRepository();

        backUpStatusObservable = mRecordsRepository.getBackUpStatusObservable();
    }

    public void signOut(Context context) {
        mAuthRepository.signOut(context);
    }

    public Observable<RecordsRepository.BackUpStatus> getBackUpStatusObservable() {
        return backUpStatusObservable;
    }

    public void backUpData(Context context) {
        mRecordsRepository.backUpData(context);
    }
}
