package com.example.databackup.backup.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.backup.repository.RecordsRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class HomeViewModel extends ViewModel {
    public enum BackUpStatus {
        INITIAL,
        IN_PROGRESS,
        SUCCESS,
        FAIL,
    }

    private static final String TAG = HomeViewModel.class.getSimpleName();
    private AuthRepository mAuthRepository;
    private RecordsRepository mRecordsRepository;
    private BehaviorSubject<BackUpStatus> backUpStatusSubject = BehaviorSubject.createDefault(BackUpStatus.INITIAL);
    private Observable<List<Long>> recordsObservable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void init(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
        mRecordsRepository = new RecordsRepository();
        recordsObservable = mRecordsRepository.getRecordsObservable();
    }

    public void signOut(Context context) {
        mAuthRepository.signOut(context);
    }

    public Observable<BackUpStatus> getBackUpStatusObservable() {
        return backUpStatusSubject.hide();
    }

    public void backUpData(Context context) {
        backUpStatusSubject.onNext(BackUpStatus.IN_PROGRESS);
        Disposable disposable = mRecordsRepository.backUpData(context).subscribe(backUpData -> {
            backUpStatusSubject.onNext(BackUpStatus.SUCCESS);
            mRecordsRepository.putRecord(backUpData.getBackUpDate());
        }, e -> backUpStatusSubject.onNext(BackUpStatus.FAIL));
        compositeDisposable.add(disposable);
    }

    public void fetchRecords(String email) {
        mRecordsRepository.fetchAll(email);
    }

    public Observable<List<Long>> getRecordsObservable() {
        return recordsObservable;
    }

    public void tearDown() {
        compositeDisposable.dispose();
    }
}
