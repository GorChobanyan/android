package com.example.wallpapersapplication.main.imageList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.fullScreenImage.FullScreenImageActivity;
import com.example.wallpapersapplication.main.Image;
import com.example.wallpapersapplication.main.downloadFavorite.DownloadFavoritesFragment;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.wallpapersapplication.main.MainActivity.PERMISSIONS_REQUEST_CODE;

public class ImageListFragment extends Fragment {

    private static final String SELECTED_CATEGORY_TITLE = "selected_category_title";
    private static final String SELECTED_CATEGORY_INDEX = "selected_category_index";

    private ImageListViewModel viewModel;

    private ImageView back;
    private ImageView downloads;
    private ImageView favorites;

    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private String categoryTitle;

    private boolean shouldDownload;

    private RecyclerView categoryRecyclerView;
    private CategoryNameAdapter categoryNameAdapter;
    private ArrayList<String> categoryTitles;

    private Image image;
    private Bitmap bitmap;

    public static ImageListFragment newInstance(String categoryTitle, int index) {
        Bundle args = new Bundle();
        args.putString(SELECTED_CATEGORY_TITLE, categoryTitle);
        args.putInt(SELECTED_CATEGORY_INDEX, index);
        ImageListFragment fragment = new ImageListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        bindModelData();
        getCategoryTitles();
        initUI();
        setListeners();
    }

    private void getCategoryTitles() {
        categoryTitles = new ArrayList<>(Arrays.asList(
                getResources().getStringArray(R.array.category_url_start)));
    }

    private boolean checkPermissions(boolean shouldDownload) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.shouldDownload = shouldDownload;
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    private void initViews(View view) {
        back = view.findViewById(R.id.image_list_back);
        downloads = view.findViewById(R.id.download_icon);
        favorites = view.findViewById(R.id.favorites_icon);
        imageRecyclerView = view.findViewById(R.id.images_list);
        categoryRecyclerView = view.findViewById(R.id.category_title_list);
    }

    private void bindModelData() {
        viewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
    }

    private void initUI() {
        if (getArguments() != null) {
            categoryTitle = getArguments().getString(SELECTED_CATEGORY_TITLE);
            int categoryIndex = getArguments().getInt(SELECTED_CATEGORY_INDEX);
            String serverTitle = getResources().getStringArray(R.array.category_url_start)[categoryIndex];
            imageAdapter = new ImageAdapter();
            viewModel.setCategoryTitle(serverTitle);
            setImageList();
            imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            imageRecyclerView.setAdapter(imageAdapter);
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(imageRecyclerView);

            categoryNameAdapter = new CategoryNameAdapter(categoryTitle);
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            setCategoryTitles();
            categoryRecyclerView.setAdapter(categoryNameAdapter);
        }
    }

    private void setCategoryTitles() {
        categoryNameAdapter.setCategoryTitles(categoryTitles);
        int scrollPosition = categoryTitles.indexOf(this.categoryTitle);
        if (scrollPosition != -1) {
            categoryRecyclerView.getLayoutManager().scrollToPosition(scrollPosition);
        }
    }

    private void setImageList() {
        viewModel.observeImages().observe(this, images -> {
            imageAdapter.setImageList(images);
        });
    }

    private void setListeners() {
        back.setOnClickListener(v -> {
            removeFromBackStack();
        });

        favorites.setOnClickListener(v -> {
            openFragment(DownloadFavoritesFragment.newInstance(true));
        });

        downloads.setOnClickListener(v -> {
            if (checkPermissions(false)) {
                openFragment(DownloadFavoritesFragment.newInstance(false));
            }
        });

        categoryNameAdapter.setOnImageItemClickListener(categoryTitle -> {
            if (!this.categoryTitle.equals(categoryTitle)) {
                this.categoryTitle = categoryTitle;
//                getCategoryTitles();
                viewModel.setCategoryTitle(categoryTitle);
            }
        });

        imageAdapter.setOnImageItemClickListener(new ImageAdapter.OnImagesAdapterItemClickListener() {
            @Override
            public void onDownloadItemClickListener(Image image, Bitmap bitmap) {
                ImageListFragment.this.image = image;
                ImageListFragment.this.bitmap = bitmap;
                if (checkPermissions(true)) {
                    downloadImage();
                }
            }

            @Override
            public void onFavoriteItemClickListener(Image image) {
                image.setFavorite(!image.isFavorite());
                viewModel.updateImage(image);
            }

            @Override
            public void onPreviewItemClickListener(int imageId) {
                Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.IMAGE_ID, imageId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });
    }

    private void downloadImage() {
        if (!image.isDownloaded()) {
            image.setDownloaded(!image.isDownloaded());
            viewModel.updateImage(image);
            viewModel.saveImage(getContext(), image, bitmap);
        }
    }

    private void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, fragment)
                .commit();
    }

    public void removeFromBackStack() {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.remove(this);
        trans.commit();
        getFragmentManager().popBackStack();
    }

    public void permissionGranted() {
        if (shouldDownload) {
            downloadImage();
        } else {
            openFragment(DownloadFavoritesFragment.newInstance(false));
        }
    }
}
