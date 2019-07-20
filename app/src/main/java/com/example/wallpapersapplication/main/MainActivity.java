package com.example.wallpapersapplication.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.base.BaseActivity;
import com.example.wallpapersapplication.main.categories.CategoriesFragment;
import com.example.wallpapersapplication.main.downloadFavorite.DownloadFavoritesFragment;
import com.example.wallpapersapplication.main.imageList.ImageListFragment;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends BaseActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 17;

    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindModelData();
        saveImageUrls();
        openFragment(CategoriesFragment.newInstance());
    }

    private void bindModelData() {
        model = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    private void saveImageUrls() {
        ArrayList<String> categoryTitles = new ArrayList<>(Arrays.asList(
                getResources().getStringArray(R.array.category_url_start)));
        model.getImageUrls(categoryTitles).observe(this, imageUrlResponseResource -> {
            switch (imageUrlResponseResource.status) {
                case SUCCESS:
                    if (imageUrlResponseResource.data != null) {
                        model.getAllImages().observe(this, images -> {
                            if (images.isEmpty()) {
                                model.insertImages(imageUrlResponseResource.data);
                            }
                        });
                    }
                    break;
                case ERROR:
                    if (imageUrlResponseResource.message != null) {
                        showErrorDialog(imageUrlResponseResource.message);
                    } else {
                        showErrorDialog();
                    }
                    break;
            }
        });
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
