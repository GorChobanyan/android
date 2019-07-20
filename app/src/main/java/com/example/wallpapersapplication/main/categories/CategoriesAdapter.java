package com.example.wallpapersapplication.main.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpapersapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
    private List<Category> categories;

    private OnCategoriesAdapterItemClickListener adapterItemClickListener;

    public CategoriesAdapter() {
        this.categories = new ArrayList<>();
    }

    public void setCategoriesList(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    public void setOnCategoriesItemClickListener(OnCategoriesAdapterItemClickListener adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoriesViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_category, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesViewHolder categoriesViewHolder, int i) {
        categoriesViewHolder.bindTo(categories.get(i), i);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryTitle;
        View rootView;

        CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_item_image);
            categoryTitle = itemView.findViewById(R.id.category_item_title);
            rootView = itemView;
        }

        private void bindTo(final Category category, int position) {
            categoryTitle.setText(category.getTitle());

            categoryImage.setImageDrawable(ContextCompat.getDrawable(categoryImage.getContext(), category.getImage()));

            rootView.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
//                    startZoomInAnimation(rootView, 0.8f, 0.8f);
                    adapterItemClickListener.onCategoryItemClicked(category.getTitle(), position);
                }
            });
        }
    }

    public void startZoomInAnimation(View v, float startScale, float endScale) {
//        ScaleAnimation zoom = new ScaleAnimation(1f, 0.8f, 1f, 0.8f);
//        zoom.setFillAfter(true);
//        zoom.setDuration(1000);
//        v.startAnimation(zoom);

//        PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f);
//        PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f);
//        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v, scalex, scaley);
//        anim.setRepeatMode(ValueAnimator.);
//        anim.setDuration(1500);
//        anim.start();

//        Animation anim = new ScaleAnimation(
//                1.0f, 0.9f, // Start and end values for the X axis scaling
//                1.0f, 0.9f, // Start and end values for the Y axis scaling
//                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
//                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
//        anim.setFillAfter(true);
//        anim.setDuration(5000);
//        v.startAnimation(anim);
    }

    public interface OnCategoriesAdapterItemClickListener {
        void onCategoryItemClicked(String categoryTitle, int index);
    }
}
