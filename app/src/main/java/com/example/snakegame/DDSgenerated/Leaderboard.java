package com.example.snakegame.SnakeGame;

public class Leaderboard{
    public LeaderboardEntrySeq entries = new LeaderboardEntrySeq();// @ID(0)

    public Leaderboard(){

        this.entries.maximum(255);
    }

    public Leaderboard(Leaderboard other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        Leaderboard typedSrc = (Leaderboard)src;
        this.entries.copy(typedSrc.entries);
        return this;
    }
}