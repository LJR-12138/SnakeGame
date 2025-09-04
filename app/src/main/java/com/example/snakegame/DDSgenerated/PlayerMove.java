package com.example.snakegame.SnakeGame;

public class PlayerMove{
    public int player_id = 0;// @ID(0)
    public String direction = "";// @ID(1)
    public int timestamp = 0;// @ID(2)

    public PlayerMove(){

    }

    public PlayerMove(PlayerMove other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        PlayerMove typedSrc = (PlayerMove)src;
        this.player_id =  typedSrc.player_id;
        this.direction =  typedSrc.direction;
        this.timestamp =  typedSrc.timestamp;
        return this;
    }
}