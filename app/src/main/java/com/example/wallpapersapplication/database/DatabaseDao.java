package com.example.wallpapersapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import com.example.wallpapersapplication.main.categories.Category;
import com.example.wallpapersapplication.main.Image;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DatabaseDao {
    @Query("SELECT * FROM " + Image.TABLE_NAME + " WHERE category_title = :categoryTitle")
    LiveData<List<Image>> getImagesByCategory(String categoryTitle);

    @Query("SELECT * FROM " + Image.TABLE_NAME)
    LiveData<List<Image>> getAllImages();

    @Query("SELECT * FROM " + Image.TABLE_NAME)
    List<Image> getImages();

    @Insert
    void insertImages(Image... images);

    @Query("SELECT * FROM " + Image.TABLE_NAME + " WHERE _id = :imageId")
    LiveData<Image> getSingleImage(int imageId);

    @Query("SELECT * FROM " + Image.TABLE_NAME + " WHERE favorite = 1")
    LiveData<List<Image>> getFavoriteImages();

    @Update
    void updateImage(Image image);

    @Update
    void insertImages(ArrayList<Image> second);
}
