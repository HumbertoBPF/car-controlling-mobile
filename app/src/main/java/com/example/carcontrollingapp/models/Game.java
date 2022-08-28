package com.example.carcontrollingapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity
public class Game {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @SerializedName("game_tag")
    private String gameTag;
    @SerializedName("game_name")
    private String gameName;

    public Game(String gameTag, String gameName) {
        this.gameTag = gameTag;
        this.gameName = gameName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameTag() {
        return gameTag;
    }

    public void setGameTag(String gameTag) {
        this.gameTag = gameTag;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return this.gameName;
    }

    @Override
    public boolean equals(Object o) {
        Game game = (Game) o;
        return Objects.equals(id, game.id) && Objects.equals(gameTag, game.gameTag) && Objects.equals(gameName, game.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameTag, gameName);
    }
}
