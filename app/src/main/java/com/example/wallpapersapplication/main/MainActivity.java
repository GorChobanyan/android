package com.example.wallpapersapplication.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.base.BaseActivity;
import com.example.wallpapersapplication.main.categories.CategoriesFragment;
import com.example.wallpapersapplication.main.downloadFavorite.DownloadFavoritesFragment;
import com.example.wallpapersapplication.main.imageList.ImageListFragment;


public class MainActivity extends BaseActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment(CategoriesFragment.newInstance());
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean isChecked = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isChecked = false;
                    break;
                }
            }
            if (isChecked) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
                if (fragment instanceof ImageListFragment) {
                    ((ImageListFragment) fragment).permissionGranted();
                } else if (fragment instanceof DownloadFavoritesFragment) {
                    ((DownloadFavoritesFragment) fragment).downloadImage();
                } else if (fragment instanceof CategoriesFragment) {
                    openFragment(DownloadFavoritesFragment.newInstance(false));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment instanceof ImageListFragment) {
            ((ImageListFragment) fragment).removeFromBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
