package com.example.snakegame.SnakeGame;

public class GameSetting{
    public int speed = 0;// @ID(0)
    public int grid_size = 0;// @ID(1)

    public GameSetting(){

    }

    public GameSetting(GameSetting other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        GameSetting typedSrc = (GameSetting)src;
        this.speed =  typedSrc.speed;
        this.grid_size =  typedSrc.grid_size;
        return this;
    }
}