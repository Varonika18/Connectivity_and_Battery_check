package com.example.napplication;

public class ImageData {
    private String imageUrl;
    private String time;
    private String date;

    public ImageData() {
        // Default constructor required for Firebase
    }

    public ImageData(String imageUrl, String time, String date) {
        this.imageUrl = imageUrl;
        this.time = time;
        this.date = date;
    }

    // Getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
