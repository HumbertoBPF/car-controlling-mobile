package com.example.carcontrollingapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ad {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private String description;
    private String picture;

    public Ad(String title, String description, String picture) {
        this.title = title;
        this.description = description;
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
