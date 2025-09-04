package com.example.snakegame.SnakeGame;

public class GameState{
    public int player_id = 0;// @ID(0)
    public com.zrdds.infrastructure.LongSeq snake_x = new com.zrdds.infrastructure.LongSeq();// @ID(1)
    public com.zrdds.infrastructure.LongSeq snake_y = new com.zrdds.infrastructure.LongSeq();// @ID(2)
    public int length = 0;// @ID(3)
    public int score = 0;// @ID(4)

    public GameState(){

        this.snake_x.maximum(255);
        this.snake_y.maximum(255);
    }

    public GameState(GameState other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        GameState typedSrc = (GameState)src;
        this.player_id =  typedSrc.player_id;
        this.snake_x.copy(typedSrc.snake_x);
        this.snake_y.copy(typedSrc.snake_y);
        this.length =  typedSrc.length;
        this.score =  typedSrc.score;
        return this;
    }
}