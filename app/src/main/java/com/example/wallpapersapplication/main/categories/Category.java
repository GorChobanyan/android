package com.example.wallpapersapplication.main.categories;

public class Category {

    private String title;

    private int resId;

    public Category(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return resId;
    }
}
