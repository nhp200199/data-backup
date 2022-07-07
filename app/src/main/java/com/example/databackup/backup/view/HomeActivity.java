package com.example.databackup.backup.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.databackup.BaseActivity;
import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.auth.view.LoginActivity;
import com.example.databackup.backup.repository.RecordsRepository;
import com.example.databackup.backup.view.adapter.RecordsAdapter;
import com.example.databackup.backup.viewmodel.HomeViewModel;
import com.example.databackup.databinding.ActivityHomeBinding;
import com.example.databackup.restore.view.DataOverviewActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class HomeActivity extends BaseActivity {
    public static final String EXTRA_USER_EMAIL = "user_email";
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
    };
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int ACTION_REQUEST_PERMISSIONS = 444;
    private String userEmail = "...";
    private ActivityHomeBinding binding;
    private HomeViewModel mHomeViewModel;
    private CompositeDisposable viewSubscription = new CompositeDisposable();
    private RecordsAdapter mRecordsAdapter;

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

        mHomeViewModel.fetchRecords(userEmail);

        setUpView();
        setUpViewListeners();
    }

    private void setUpView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        mRecordsAdapter = new RecordsAdapter(this, new ArrayList<Long>());
        mRecordsAdapter.setClickListener((v, index) -> {
            Intent intent = new Intent(HomeActivity.this, DataOverviewActivity.class);
            intent.putExtra(DataOverviewActivity.EXTRA_DATA_ID, mRecordsAdapter.getItem(index));
            startActivity(intent);
//            Toast.makeText(this, "clicked " + index, Toast.LENGTH_SHORT).show();
        });
        binding.rcvRecords.setAdapter(mRecordsAdapter);
        binding.rcvRecords.setLayoutManager(layoutManager);
        binding.rcvRecords.addItemDecoration(dividerItemDecoration);
    }

    private void setUpViewListeners() {
        binding.tvWelcomeUserEmail.setText(String.format(getString(R.string.home_activity_txt_welcome_user_with_argument), userEmail));
        binding.btnBackUp.setOnClickListener(v -> {
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            }
            else {
                mHomeViewModel.backUpData(HomeActivity.this);
            }
        });

        //TODO: implement swipe to refresh
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeViewSubscriptions();
    }

    private void makeViewSubscriptions() {
        Disposable backUpStatusDisposable = mHomeViewModel.getBackUpStatusObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(status -> {
            switch (status) {
                case IN_PROGRESS:
                    showLoadingDialog();
                    break;
                case FAIL:
                    showInformationPopup("Thất bại", "Có lỗi trong quá trình thực hiện. Vui lòng thử lại");
                    break;
                case SUCCESS:
                    hidePopup();
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }, e -> {
            e.printStackTrace();
            Log.e(TAG, "ERROR WITH BACK UP STATUS. " + e.toString());
        });

        Disposable recordsDisposable = mHomeViewModel.getRecordsObservable().subscribe(longs -> {
            Log.i(TAG, "number of records: " + longs.size());
            if (longs.size() == 0) {
                binding.tvRecordsStatus.setVisibility(View.VISIBLE);
                binding.rcvRecords.setVisibility(View.GONE);

                binding.tvRecordsStatus.setText("Chưa có bản lưu nào");
            }
            else {
                binding.tvRecordsStatus.setVisibility(View.GONE);
                binding.rcvRecords.setVisibility(View.VISIBLE);

                mRecordsAdapter.getCurrentValues().clear();
                mRecordsAdapter.getCurrentValues().addAll(longs);
                mRecordsAdapter.notifyDataSetChanged();
            }
        });

        viewSubscription.add(backUpStatusDisposable);
        viewSubscription.add(recordsDisposable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearViewSubscriptions();
    }

    private void clearViewSubscriptions() {
        viewSubscription.dispose();
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
                mHomeViewModel.backUpData(HomeActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomeViewModel.tearDown();
        binding = null;
    }
}