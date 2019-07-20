package com.example.wallpapersapplication.main.imageList;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpapersapplication.R;

import java.util.ArrayList;
import java.util.List;

class CategoryNameAdapter extends RecyclerView.Adapter<CategoryNameAdapter.CategoryTitleViewHolder> {

    private List<String> categoryTitleList;
    private String selectedTitle;
    private int selectedPosition;

    private OnCategoryNameAdapterItemClickListener adapterItemClickListener;

    public CategoryNameAdapter(String selectedTitle) {
        this.categoryTitleList = new ArrayList<>();
        this.selectedTitle = selectedTitle;
    }

    public void setCategoryTitles(List<String> categoryTitleList) {
        this.categoryTitleList.clear();
        this.categoryTitleList.addAll(categoryTitleList);
        notifyDataSetChanged();
    }

    public void setOnImageItemClickListener(OnCategoryNameAdapterItemClickListener adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @NonNull
    @Override
    public CategoryNameAdapter.CategoryTitleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoryTitleViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_category_title, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryNameAdapter.CategoryTitleViewHolder categoryTitleViewHolder, int i) {
        categoryTitleViewHolder.bindTo(categoryTitleList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return categoryTitleList.size();
    }

    class CategoryTitleViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;

        CategoryTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_item_title);
        }

        private void bindTo(final String titleString, int position) {
            if (titleString.equals(selectedTitle)) {
                selectedPosition = position;
                categoryTitle.setBackground(categoryTitle.getContext().getDrawable(R.drawable.category_title_selected_back));
                categoryTitle.setTextColor(ContextCompat.getColor(categoryTitle.getContext(), R.color.white));
                categoryTitle.setTypeface(null, Typeface.BOLD);
            } else {
                categoryTitle.setBackground(categoryTitle.getContext().getDrawable(R.drawable.category_title_unselected_back));
                categoryTitle.setTextColor(ContextCompat.getColor(categoryTitle.getContext(), R.color.category_title_color));
                categoryTitle.setTypeface(null, Typeface.NORMAL);
            }
            categoryTitle.setText(titleString);
            categoryTitle.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    if (selectedPosition != position) {
                        categoryTitle.setBackground(categoryTitle.getContext().getDrawable(R.drawable.category_title_selected_back));
                        categoryTitle.setTextColor(ContextCompat.getColor(categoryTitle.getContext(), R.color.white));
                        categoryTitle.setTypeface(null, Typeface.BOLD);
                        selectedTitle = categoryTitle.getText().toString();
                        notifyItemChanged(selectedPosition);
                        selectedPosition = position;
                    }
                    adapterItemClickListener.onNameCategoryItemClick(titleString);
                }
            });
        }
    }

    public interface OnCategoryNameAdapterItemClickListener {
        void onNameCategoryItemClick(String categoryTitle);
    }
}
