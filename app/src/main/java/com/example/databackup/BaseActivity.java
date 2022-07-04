package com.example.databackup;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.databackup.databinding.DialogLoadingBinding;
import com.example.databackup.databinding.DialogOperationErrorBinding;

public class BaseActivity extends AppCompatActivity {
    protected Dialog dialog;

    public void showInformationPopup(String title, String message) {
        if (dialog != null) {
            hidePopup();
        }
        dialog = new Dialog(this);
        DialogOperationErrorBinding binding = DialogOperationErrorBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.ivDialogIcon.setImageResource(R.drawable.ic_warning);
        binding.tvTitle.setText(title);
        binding.tvMsg.setText(message);
        binding.btnOK.setOnClickListener(v -> {
            hidePopup();
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public void showLoadingDialog() {
        if (dialog != null) {
            hidePopup();
        }
        dialog = new Dialog(this);
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        dialog.setCancelable(false);
        dialog.show();
    }

    public void hidePopup() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
