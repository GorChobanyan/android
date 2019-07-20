package com.example.wallpapersapplication.main;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Image.TABLE_NAME)
public class Image {
    public static final String TABLE_NAME = "images_table";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "category_title")
    private String categoryTitle;

    @ColumnInfo(name = "favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "downloaded")
    private boolean isDownloaded;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public Image(String categoryTitle, String imageUrl, boolean isFavorite, boolean isDownloaded) {
        this.categoryTitle = categoryTitle;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
        this.isDownloaded = isDownloaded;
    }

    public Image(int imageId, String categoryTitle, String imageUrl, boolean isFavorite, boolean isDownloaded) {
        id = imageId;
        this.categoryTitle = categoryTitle;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
        this.isDownloaded = isDownloaded;
    }

    public int getImageId() {
        return id;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }
}
