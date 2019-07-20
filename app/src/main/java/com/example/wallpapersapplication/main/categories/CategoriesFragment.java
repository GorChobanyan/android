package com.example.wallpapersapplication.main.categories;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.base.BaseActivity;
import com.example.wallpapersapplication.main.downloadFavorite.DownloadFavoritesFragment;
import com.example.wallpapersapplication.main.imageList.ImageListFragment;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.wallpapersapplication.main.MainActivity.PERMISSIONS_REQUEST_CODE;

public class CategoriesFragment extends Fragment {

    private ImageView downloads;
    private ImageView favorites;

    private RecyclerView categoriesList;
    private CategoriesAdapter adapter;

    private CategoriesViewModel model;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
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
        downloads = view.findViewById(R.id.download_icon);
        favorites = view.findViewById(R.id.favorites_icon);
        categoriesList = view.findViewById(R.id.category_title_list);
    }

    private void setListeners() {
        adapter.setOnCategoriesItemClickListener((categoryTitle, index) -> {
            openFragment(ImageListFragment.newInstance(categoryTitle, index));
        });

        favorites.setOnClickListener(v -> {
            openFragment(DownloadFavoritesFragment.newInstance(true));
        });

        downloads.setOnClickListener(v -> {
            if (checkPermissions()) {
                openFragment(DownloadFavoritesFragment.newInstance(false));
            }
        });
    }

    private void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private void bindModelData() {
        model = ViewModelProviders.of(this).get(CategoriesViewModel.class);
    }

    private void initUI() {
        adapter = new CategoriesAdapter();
        setCategories();
        categoriesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesList.setAdapter(adapter);
    }

    private void setCategories() {
        String[] categoryTitles = getResources().getStringArray(R.array.category_titles);
        ArrayList<Category> categories = new ArrayList<>(categoryTitles.length);
        categories.add(new Category(categoryTitles[0], R.drawable.background_space));
        categories.add(new Category(categoryTitles[1], R.drawable.background_sport));
        categories.add(new Category(categoryTitles[2], R.drawable.background_nature));
        categories.add(new Category(categoryTitles[3], R.drawable.background_music));
        categories.add(new Category(categoryTitles[4], R.drawable.background_macro));
        categories.add(new Category(categoryTitles[5], R.drawable.background_love));
        categories.add(new Category(categoryTitles[6], R.drawable.background_food));
        categories.add(new Category(categoryTitles[7], R.drawable.background_flowers));
        categories.add(new Category(categoryTitles[8], R.drawable.background_city));
        categories.add(new Category(categoryTitles[9], R.drawable.background_cars));
        categories.add(new Category(categoryTitles[10], R.drawable.background_birds));
        categories.add(new Category(categoryTitles[11], R.drawable.background_bike));
        categories.add(new Category(categoryTitles[12], R.drawable.background_baby));
        categories.add(new Category(categoryTitles[13], R.drawable.background_art));
        categories.add(new Category(categoryTitles[14], R.drawable.background_anime));
        categories.add(new Category(categoryTitles[15], R.drawable.background_animals));
        categories.add(new Category(categoryTitles[16], R.drawable.background_3d));
        categories.add(new Category(categoryTitles[17], R.drawable.background_abstract));
        categories.add(new Category(categoryTitles[18], R.drawable.background_vector));
        categories.add(new Category(categoryTitles[19], R.drawable.background_celebrities));
        adapter.setCategoriesList(categories);
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
}
