package com.example.wallpapersapplication.base;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;

public class BaseActivity extends AppCompatActivity {

    public static final String LOADING_TAG = "loading";
    public static final String TIMEOUT = "timeout";

    private LoadingDialogFragment loadingDialog;
    private int showCount;
    private int dismissCount;

    public void showLoadingDialog() {
        if (getSupportFragmentManager().findFragmentByTag(TIMEOUT) == null) {
            if (loadingDialog == null
                    || loadingDialog.getDialog() == null
                    || !loadingDialog.getDialog().isShowing()
                    || loadingDialog.isRemoving()) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                loadingDialog = LoadingDialogFragment.newInstance();
                FragmentManager manager = getSupportFragmentManager();
                loadingDialog.show(manager, LOADING_TAG);
            }
        }
        Log.e("show loading dialog", ++showCount + "");
    }

    public void dismissLoadingDialog() {
        if (loadingDialog.getDialog() != null
                && loadingDialog.getDialog().isShowing()
                && !loadingDialog.isRemoving()) {
            // dialog is shown
            loadingDialog.dismiss();
        }
        Log.e("hide loading dialog", ++dismissCount + "");
    }

    public void showErrorDialog() {
        String errorMessage = getString(R.string.default_error_message);

        showErrorDialog(errorMessage);
    }

    public void showErrorDialog(String errorMsg) {
        dismissLoadingDialog();
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
