package com.example.databackup.backup.viewmodel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.ViewModel;

import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.backup.repository.RecordsRepository;
import com.example.databackup.util.System;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class HomeViewModel extends ViewModel {
    public enum OperationStatus {
        INITIAL,
        IN_PROGRESS,
        SUCCESS,
        FAIL,
    }

    private static final String TAG = HomeViewModel.class.getSimpleName();
    private AuthRepository mAuthRepository;
    private RecordsRepository mRecordsRepository;
    private BehaviorSubject<OperationStatus> backUpStatusSubject = BehaviorSubject.createDefault(OperationStatus.INITIAL);
    private BehaviorSubject<OperationStatus> fetchRecordsStatusSubject = BehaviorSubject.createDefault(OperationStatus.INITIAL);
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

    public Observable<OperationStatus> getBackUpStatusObservable() {
        return backUpStatusSubject.hide();
    }

    public void backUpData(Context context) {
        backUpStatusSubject.onNext(OperationStatus.IN_PROGRESS);
        if (System.hasNetwork(context)) {
            Disposable disposable = mRecordsRepository.backUpData(context).subscribe(backUpData -> {
                backUpStatusSubject.onNext(OperationStatus.SUCCESS);
                mRecordsRepository.putRecord(backUpData.getBackUpDate());
            }, e -> backUpStatusSubject.onNext(OperationStatus.FAIL));
            compositeDisposable.add(disposable);
        }
        else {
            backUpStatusSubject.onNext(OperationStatus.FAIL);
        }
    }

    public void fetchRecords(Context context, String email) {
        fetchRecordsStatusSubject.onNext(OperationStatus.IN_PROGRESS);
        if (System.hasNetwork(context)) {
            Disposable fetchRecordDisposable = mRecordsRepository.fetchRecords(context, email).subscribe(records -> {
                fetchRecordsStatusSubject.onNext(OperationStatus.SUCCESS);
            }, e ->{
                fetchRecordsStatusSubject.onNext(OperationStatus.FAIL);
            });
            compositeDisposable.add(fetchRecordDisposable);
        }
        else {
            fetchRecordsStatusSubject.onNext(OperationStatus.FAIL);
        }
    }

    public Observable<List<Long>> getRecordsObservable() {
        return recordsObservable;
    }

    public Observable<OperationStatus> getFetchRecordsObservable() {
        return fetchRecordsStatusSubject.hide();
    }

    public void tearDown() {
        compositeDisposable.dispose();
    }

    public void resetLoadingStates() {
        fetchRecordsStatusSubject.onNext(OperationStatus.INITIAL);
        backUpStatusSubject.onNext(OperationStatus.INITIAL);
    }
}
