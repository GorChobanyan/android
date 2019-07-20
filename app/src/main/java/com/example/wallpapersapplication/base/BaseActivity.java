package com.example.wallpapersapplication.base;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;

public class BaseActivity extends AppCompatActivity {

    public static final String TIMEOUT = "timeout";

    public void showErrorDialog() {
        String errorMessage = getString(R.string.default_error_message);

        showErrorDialog(errorMessage);
    }

    public void showErrorDialog(String errorMsg) {
        String errorMessage;
        if (Utils.isNetworkAvailable(this)) {
            errorMessage = errorMsg.equals(TIMEOUT)
                    ? getString(R.string.timeout_error_message)
                    : errorMsg;
        } else {
            errorMessage = getString(R.string.no_connection_error_message);
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.default_error_message)
                .setMessage(errorMessage)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .create().show();
    }

}
