package com.example.wallpapersapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

import com.example.wallpapersapplication.main.Image;
import com.example.wallpapersapplication.network.RestClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {

    private static final String PICTURE = "picture_";
    private static final String EXTERNAL_STORAGE_TAG = "ExternalStorage";
    private static final String JPG_FORMAT = ".jpg";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setWallpaper(Bitmap imageBitmap, Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap,width,height, true);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        wallpaperManager.setWallpaperOffsetSteps(1, 1);
        wallpaperManager.suggestDesiredDimensions(width, height);

        try {
//            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDownloadedImage(Context context, Image image) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getPackageName());
        if (directory.exists()) {
            File file = new File(directory,
                    PICTURE + image.getImageId() + JPG_FORMAT);
            if (file.delete()) {
                Log.i(EXTERNAL_STORAGE_TAG, file.getName() + " has been removed successfully.");
            }

            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, new String[]{"image/jpeg"}, (path, uri) -> {
                        Log.i(EXTERNAL_STORAGE_TAG, "Scanned " + path + ":");
                        Log.i(EXTERNAL_STORAGE_TAG, "-> uri=" + uri);
                    });
        }
    }

    public static void saveImage(Context context, Image image, Bitmap bitmap) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getPackageName());
        if (!directory.mkdirs()) {
            Log.e(EXTERNAL_STORAGE_TAG, "Directory not created");
        }

        File file = new File(directory, PICTURE + image.getImageId() + JPG_FORMAT);
        if (file.exists()) {
            file.delete();
        }

        try {
            if (file.createNewFile()) {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Log.i(EXTERNAL_STORAGE_TAG, file.getName() + " has been added successfully.");

                // Tell the media scanner about the new file
                // so that it appears in the gallery.
                MediaScannerConnection.scanFile(context,
                        new String[]{file.toString()}, new String[]{"image/jpeg"}, (path, uri) -> {
                            Log.i(EXTERNAL_STORAGE_TAG, "Scanned " + path + ":");
                            Log.i(EXTERNAL_STORAGE_TAG, "-> uri=" + uri);
                        });
            }
        } catch (Exception e) {
            Log.e(EXTERNAL_STORAGE_TAG, "Error writing " + file, e);
        }
    }

    public static Pair<ArrayList<Pair<Image, Bitmap>>, ArrayList<Image>> getImages(String albumName, List<Image> images) {
        HashMap<Integer, Bitmap> downloadedImageIds = getDownloadedImageIds(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName));

        ArrayList<Pair<Image, Bitmap>> downloadedImages = new ArrayList<>(downloadedImageIds.size());
        ArrayList<Image> updateImages = new ArrayList<>();

        for (Image image : images) {
            Bitmap bitmap = downloadedImageIds.get(image.getImageId());
            if (bitmap != null) {
                downloadedImages.add(Pair.create(image, bitmap));
                if (!image.isDownloaded()) {
                    image.setDownloaded(true);
                    updateImages.add(image);
                }
            } else {
                if (image.isDownloaded()) {
                    image.setDownloaded(false);
                    updateImages.add(image);
                }
            }
        }

        // if there are some images in the external album don't add
        // them to the database, but show to the user. The functionality of delete
        // will work.

//        if (!downloadedImageIds.isEmpty()) {
//            Iterator it = downloadedImageIds.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry pair = (Map.Entry) it.next();
//                downloadedImages.add(new Image((Integer) pair.getKey(), null, convertBitmapToByteArray(
//                        (Bitmap) pair.getValue()), false, true));
//                it.remove(); // avoids a ConcurrentModificationException
//            }
//        }

        return new Pair<>(downloadedImages, updateImages);
    }

    /**
     * Extract saved image ids
     *
     * @param root album directory
     * @return set of saved image ids
     */
    @SuppressLint("UseSparseArrays")
    private static HashMap<Integer, Bitmap> getDownloadedImageIds(File root) {
        HashMap<Integer, Bitmap> imageList = new HashMap<>();
        File[] files = root.listFiles();
        if (files != null) {
            String name;
            int start;
            int end;
            for (File file : files) {
                name = file.getName();
                start = name.indexOf(PICTURE) + PICTURE.length();
                end = name.indexOf(JPG_FORMAT);
                if (start != -1 && end != -1 && start < name.length()) {
                    try {
                        imageList.put(Integer.valueOf(name.substring(start, end)),
                                BitmapFactory.decodeFile(file.getAbsolutePath()));
                    } catch (NumberFormatException e) {
                        Log.e(EXTERNAL_STORAGE_TAG, "Error while getting image Id");
                    }
                }
            }
        }
        return imageList;
    }

    public static String getCompleteImageUrl(String imageUrl) {
        return RestClient.BASE_URL + imageUrl;
    }
}
