package com.example.databackup.backup.repository;

import android.content.Context;

import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class RecordsRepository {
    private static RecordsRepository instance;

    private RecordsRepository() {}

    public static RecordsRepository getInstance()
    {
        if (instance == null) {
            instance = new RecordsRepository();
        }
        return instance;
    }

    public void backUpData() {

    }
}
