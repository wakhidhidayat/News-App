package com.wahidhidayat.newsapp.models;

public class Category {
    private String categoryItem;

    public Category(String categoryItem) {
        this.categoryItem = categoryItem;
    }

    public String getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(String categoryItem) {
        this.categoryItem = categoryItem;
    }
}
