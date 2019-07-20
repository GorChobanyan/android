package com.example.wallpapersapplication.fullScreenImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;
import com.example.wallpapersapplication.base.BaseActivity;
import com.example.wallpapersapplication.main.Image;

public class FullScreenImageActivity extends BaseActivity {

    public static final String IMAGE_ID = "image_id";
    private static final String TAG = FullScreenImageActivity.class.getSimpleName();

    public static final int PERMISSIONS_REQUEST_CODE = 17;

    private FullScreenImageViewModel model;

    private ImageView imageview;
    private ImageView download;
    private ImageView favorite;
    private TextView setImage;
    private ImageView close;
    private View gradient;
    private boolean isShown = true;
    private Bitmap bitmap;
    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_full_screen_image);
        initViews();
        bindModelData();
        getImage();
        setListeners();
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void bindModelData() {
        model = ViewModelProviders.of(this).get(FullScreenImageViewModel.class);
    }

    private void initViews() {
        imageview = findViewById(R.id.image_item);
        setImage = findViewById(R.id.set_button);
        favorite = findViewById(R.id.favorites_icon);
        download = findViewById(R.id.download_icon);
        close = findViewById(R.id.close_full_screen_image);
        gradient = findViewById(R.id.image_gradient);
    }

    private void getImage() {
        if (getIntent().getExtras() != null) {
            int imageId = getIntent().getExtras().getInt(IMAGE_ID);
            model.getSingleImage(imageId).observe(this, image -> {
                this.image = image;

                if (image.isFavorite()) {
                    favorite.setImageDrawable(ContextCompat.getDrawable(
                            favorite.getContext(), R.drawable.favorite_icon));
                } else {
                    favorite.setImageDrawable(ContextCompat.getDrawable(
                            favorite.getContext(), R.drawable.no_favorite_icon));
                }

                if (image.isDownloaded()) {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{new int[]{}},
                            new int[]{
                                    ContextCompat.getColor(this, R.color.white_opacity)
                            });
                    download.setImageTintList(colorStateList);
                }
                String completeUrl = Utils.getCompleteImageUrl(image.getImageUrl());
                if (!TextUtils.isEmpty(completeUrl)) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop();

                    Glide.with(imageview.getContext())
                            .asBitmap()
                            .load(completeUrl)
                            .apply(options)
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap bitmapImage, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                    bitmap = bitmapImage;
                                    return false;
                                }
                            }).into(imageview);

                }
            });
        }
    }

    private void setListeners() {
        download.setOnClickListener(v -> {
            if (checkPermissions()) {
                downloadImage();
            }
        });

        favorite.setOnClickListener(v -> {
            image.setFavorite(!image.isFavorite());
            model.updateImage(image);
        });

        setImage.setOnClickListener(v -> {
            Utils.setWallpaper(bitmap, this);
            finish();
        });

        imageview.setOnClickListener(v -> {
            if (isShown) {
                setGroupViewVisibility(View.GONE);
            } else {
                setGroupViewVisibility(View.VISIBLE);
            }
            isShown = !isShown;
        });

        close.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
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
                downloadImage();
            }
        }
    }

    private void downloadImage() {
        if (!image.isDownloaded()) {
            image.setDownloaded(!image.isDownloaded());
            model.updateImage(image);
            model.saveImage(this, image, bitmap);
        }
    }

    private void setGroupViewVisibility(int visibility) {
        setImage.setVisibility(visibility);
        favorite.setVisibility(visibility);
        download.setVisibility(visibility);
        close.setVisibility(visibility);
        gradient.setVisibility(visibility);
    }
}
