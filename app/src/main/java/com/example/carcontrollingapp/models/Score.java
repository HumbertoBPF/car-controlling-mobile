package com.example.carcontrollingapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Score {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long score;
    private String date;
    private String user;
    @SerializedName("game")
    private Long gameId;

    public Score(Long score, String date, String user, Long gameId) {
        this.score = score;
        this.date = date;
        this.user = user;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
