package com.example.wallpapersapplication.main.downloadFavorite;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.wallpapersapplication.R;
import com.example.wallpapersapplication.Utils;
import com.example.wallpapersapplication.main.Image;

import java.util.ArrayList;
import java.util.List;

class DownloadFavoritesAdapter extends RecyclerView.Adapter<DownloadFavoritesAdapter.DownloadFavoritesViewHolder> {

    private List<Image> favoriteImages;
    private List<Pair<Image, Bitmap>> downloadedImages;
    private boolean isFavorite;

    private OnItemClickListener adapterItemClickListener;

    public DownloadFavoritesAdapter(boolean isFavorite) {
        this.favoriteImages = new ArrayList<>();
        this.downloadedImages = new ArrayList<>();
        this.isFavorite = isFavorite;
    }

    /**
     * set favorite favoriteImages
     *
     * @param images which are favorite
     */
    public void setFavoriteList(List<Image> images) {
        this.favoriteImages.clear();
        this.favoriteImages.addAll(images);
        notifyDataSetChanged();
    }

    /**
     * set favorite favoriteImages
     *
     * @param images which are favorite
     */
    public void setDownloadedList(List<Pair<Image, Bitmap>> images) {
        downloadedImages.clear();
        downloadedImages.addAll(images);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.adapterItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public DownloadFavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DownloadFavoritesViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_download_favorite_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadFavoritesAdapter.DownloadFavoritesViewHolder downloadFavoritesViewHolder, int i) {
        if (isFavorite) {
            downloadFavoritesViewHolder.bindFavorite(favoriteImages.get(i), i);
        } else {
            downloadFavoritesViewHolder.bindDownloaded(downloadedImages.get(i), i);
        }
    }

    @Override
    public int getItemCount() {
        if (isFavorite) {
            return favoriteImages.size();
        } else {
            return downloadedImages.size();
        }
    }

    class DownloadFavoritesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView setAsTextView;
        ImageView download;
        ImageView remove;
        View rootView;

        DownloadFavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.image_item);
            setAsTextView = itemView.findViewById(R.id.set_as);
            download = itemView.findViewById(R.id.download_icon);
            remove = itemView.findViewById(R.id.remove);
            rootView = itemView;
        }

        private void bindDownloaded(final Pair<Image, Bitmap> image, int position) {
            imageview.setImageBitmap(image.second);
            download.setVisibility(View.GONE);
            remove.setImageDrawable(ContextCompat.getDrawable(remove.getContext(), R.drawable.delete_icon));
            remove.setBackground(ContextCompat.getDrawable(
                    remove.getContext(), R.drawable.rounded_corner_red_background));
            setListeners(image.first, position);
        }

        private void bindFavorite(final Image image, int position) {
            setAsTextView.setVisibility(View.GONE);
            remove.setColorFilter(ContextCompat.getColor(remove.getContext(),
                    R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            remove.setImageDrawable(ContextCompat.getDrawable(remove.getContext(), R.drawable.close_icon));
            remove.setBackground(ContextCompat.getDrawable(
                    remove.getContext(), R.drawable.rounded_corner_white_background));
            if (image.isDownloaded()) {
                download.setAlpha(0.3f);
            } else {
                download.setAlpha(1f);
            }

            //add image
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
            setListeners(image, position);
        }

        private void setListeners(Image image, int position) {
            remove.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    if (isFavorite) {
                        favoriteImages.remove(position);
                    } else {
                        downloadedImages.remove(position);
                    }
                    notifyItemRemoved(position);
                    adapterItemClickListener.onRemoveItemClickListener(image);
                }
            });

            imageview.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onPreviewItemClickListener(image.getImageId(), rootView);
                }
            });

            setAsTextView.setOnClickListener(v -> {
                if (adapterItemClickListener != null) {
                    String completeUrl = Utils.getCompleteImageUrl(image.getImageUrl());

                    if (!TextUtils.isEmpty(completeUrl)) {
                        RequestOptions options = new RequestOptions()
                                .centerCrop();

                        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageview.getContext());
                        circularProgressDrawable.setStrokeWidth(5f);
                        circularProgressDrawable.setCenterRadius(30f);
                        circularProgressDrawable.start();

                        Glide.with(imageview.getContext())
                                .asBitmap()
                                .load(completeUrl)
                                .placeholder(circularProgressDrawable)
                                .apply(options)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap bitmapImage, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                        imageview.setImageBitmap(bitmapImage);
                                        adapterItemClickListener.onSetAsItemClickListener(bitmapImage);
                                        return false;
                                    }
                                }).submit();
                    }
                }
            });

            download.setOnClickListener(v -> {
                download.setAlpha(0.3f);
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onDownloadItemClickListener(image,
                            ((BitmapDrawable) imageview.getDrawable()).getBitmap());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDownloadItemClickListener(Image image, Bitmap bitmap);

        void onSetAsItemClickListener(Bitmap bitmap);

        void onRemoveItemClickListener(Image image);

        void onPreviewItemClickListener(int imageId, View sharedView);
    }
}
