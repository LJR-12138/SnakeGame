package com.example.snakegame.SnakeGame;

public class LeaderboardEntry{
    public int player_id = 0;// @ID(0)
    public String nickname = "";// @ID(1)
    public int score = 0;// @ID(2)

    public LeaderboardEntry(){

    }

    public LeaderboardEntry(LeaderboardEntry other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        LeaderboardEntry typedSrc = (LeaderboardEntry)src;
        this.player_id =  typedSrc.player_id;
        this.nickname =  typedSrc.nickname;
        this.score =  typedSrc.score;
        return this;
    }
}