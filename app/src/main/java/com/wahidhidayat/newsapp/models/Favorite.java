package com.wahidhidayat.newsapp.models;

public class Favorite {
    private String id, url, title, source, date, image, description;

    public Favorite(String id, String url, String title, String source, String date, String image, String description) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.source = source;
        this.date = date;
        this.image = image;
        this.description = description;
    }

    public Favorite() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
