package com.example.wallpapersapplication.main.imageList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;
import com.example.wallpapersapplication.main.Image;

import java.util.ArrayList;
import java.util.List;

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> images;

    private OnImagesAdapterItemClickListener adapterItemClickListener;

    public ImageAdapter() {
        this.images = new ArrayList<>();
    }

    public void setImageList(List<Image> images) {
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void setOnImageItemClickListener(OnImagesAdapterItemClickListener adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapter.ImageViewHolder fullImageViewHolder, int i) {
        fullImageViewHolder.bindTo(images.get(i));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView preview;
        ImageView favorite;
        ImageView download;
        View parentView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.image_item);
            preview = itemView.findViewById(R.id.preview);
            favorite = itemView.findViewById(R.id.favorites_icon);
            download = itemView.findViewById(R.id.download_icon);
            parentView = itemView;
        }

        private void bindTo(final Image image) {

            String completeUrl = Utils.getCompleteImageUrl(image.getImageUrl());
            if (!TextUtils.isEmpty(completeUrl)) {
                RequestOptions options = new RequestOptions()
                        .centerCrop();

                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageview.getContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.start();

                Glide.with(imageview.getContext())
                        .load(completeUrl)
                        .placeholder(circularProgressDrawable)
                        .apply(options)
//                        .override(350, 350)
                        .into(imageview);
            }

            if (image.isDownloaded()) {
                download.setAlpha(0.3f);
            } else {
                download.setAlpha(1.0f);
            }

            if (image.isFavorite()) {
                favorite.setImageDrawable(ContextCompat.getDrawable(favorite.getContext(), R.drawable.favorite_icon));
            } else {
                favorite.setImageDrawable(ContextCompat.getDrawable(favorite.getContext(), R.drawable.no_favorite_icon));
            }

            preview.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onPreviewItemClickListener(image.getImageId());
                }
            });

            favorite.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onFavoriteItemClickListener(image);
                }
            });

            download.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onDownloadItemClickListener(image,
                            ((BitmapDrawable)imageview.getDrawable()).getBitmap());
                }
            });
        }
    }

    public interface OnImagesAdapterItemClickListener {
        void onDownloadItemClickListener(Image image, Bitmap bitmap);

        void onFavoriteItemClickListener(Image image);

        void onPreviewItemClickListener(int imageId);
    }
}
