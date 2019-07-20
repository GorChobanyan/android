package com.example.wallpapersapplication.main.downloadFavorite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;
import com.example.wallpapersapplication.base.BaseActivity;
import com.example.wallpapersapplication.fullScreenImage.FullScreenImageActivity;
import com.example.wallpapersapplication.main.Image;

import java.util.List;

import static com.example.wallpapersapplication.main.MainActivity.PERMISSIONS_REQUEST_CODE;

public class DownloadFavoritesFragment extends Fragment {

    private static final String OPEN_FAVORITES = "open_favorites_key";

    private TextView title;
    private ImageView back;
    private TextView noImageText;
    private RecyclerView imagesList;
    private DownloadFavoritesAdapter adapter;

    private DownloadFavoritesViewModel viewModel;
    private boolean isFavorite;
    private Image image;
    private Bitmap bitmap;

    public static DownloadFavoritesFragment newInstance(Boolean isFavorites) {
        Bundle args = new Bundle();
        args.putBoolean(OPEN_FAVORITES, isFavorites);
        DownloadFavoritesFragment fragment = new DownloadFavoritesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        bindModelData();
        initUI();
        setListeners();
    }

    private void initViews(View view) {
        back = view.findViewById(R.id.download_favorite_back);
        imagesList = view.findViewById(R.id.images_recycler_view);
        title = view.findViewById(R.id.download_favorite_title);
        noImageText = view.findViewById(R.id.no_image_message);
    }

    private void bindModelData() {
        viewModel = ViewModelProviders.of(this).get(DownloadFavoritesViewModel.class);
    }

    private void initUI() {
        if (getArguments() != null) {
            isFavorite = getArguments().getBoolean(OPEN_FAVORITES);
        }
        if (isFavorite) {
            title.setText(R.string.favorites_title);
        } else {
            title.setText(R.string.downloads_title);
        }

        adapter = new DownloadFavoritesAdapter(isFavorite);
        setImageList();
        imagesList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        imagesList.setAdapter(adapter);
    }

    private void setImageList() {
        if (isFavorite) {
            viewModel.getFavoriteImages().observe(this, images -> {
                if (!images.isEmpty()) {
                    adapter.setFavoriteList(images);
                } else {
                    showEmptyMessage();
                }
            });
        } else {
            viewModel.getAllImages().observe(this, images -> {
                List<Pair<Image, Bitmap>> imageList = viewModel.getDownloadedImages(getContext().getPackageName(), images);
                if (!imageList.isEmpty()) {
                    showAdapter();
                    adapter.setDownloadedList(viewModel.getDownloadedImages(getContext().getPackageName(), images));
                } else {
                    showEmptyMessage();
                }
            });
        }
    }

    private void showAdapter() {
        imagesList.setVisibility(View.VISIBLE);
        noImageText.setVisibility(View.GONE);
    }

    private void showEmptyMessage() {
        imagesList.setVisibility(View.GONE);
        noImageText.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        back.setOnClickListener(v -> {
            returnPrevPage();
        });

        adapter.setOnItemClickListener(new DownloadFavoritesAdapter.OnItemClickListener() {
            @Override
            public void onDownloadItemClickListener(Image image, Bitmap bitmap) {
                if (checkPermissions()) {
                    DownloadFavoritesFragment.this.image = image;
                    DownloadFavoritesFragment.this.bitmap = bitmap;
                    downloadImage();
                }
            }

            @Override
            public void onSetAsItemClickListener(Bitmap bitmap) {
                Utils.setWallpaper(bitmap, getContext());
                returnPrevPage();
            }

            @Override
            public void onRemoveItemClickListener(Image image) {
                if (isFavorite) {
                    image.setFavorite(false);
                    viewModel.updateImage(image);
                } else {
                    image.setDownloaded(false);
                    viewModel.updateImage(image);
                    viewModel.removeDownloadedImage(getContext(), image);
                }
            }

            @Override
            public void onPreviewItemClickListener(int imageId, View sharedView) {
                Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.IMAGE_ID, imageId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        makeSceneTransitionAnimation(getActivity(), sharedView, sharedView.getTransitionName());
                startActivity(intent);
            }
        });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    public void downloadImage() {
        if (!image.isDownloaded()) {
            image.setDownloaded(!image.isDownloaded());
            viewModel.updateImage(image);
            viewModel.saveImage(getContext(), image, bitmap);
        }
    }

    private void returnPrevPage() {
        getFragmentManager().popBackStack();
    }
}
