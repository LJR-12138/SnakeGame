package com.example.snakegame.SnakeGame;

public class PlayerInfo{
    public int player_id = 0;// @ID(0)
    public String nickname = "";// @ID(1)
    public String color = "";// @ID(2)

    public PlayerInfo(){

    }

    public PlayerInfo(PlayerInfo other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        PlayerInfo typedSrc = (PlayerInfo)src;
        this.player_id =  typedSrc.player_id;
        this.nickname =  typedSrc.nickname;
        this.color =  typedSrc.color;
        return this;
    }
}