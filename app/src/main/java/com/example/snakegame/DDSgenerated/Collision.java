package com.example.snakegame.SnakeGame;

public class Collision{
    public com.zrdds.infrastructure.LongSeq player_ids = new com.zrdds.infrastructure.LongSeq();// @ID(0)
    public com.zrdds.infrastructure.BooleanSeq collisions = new com.zrdds.infrastructure.BooleanSeq();// @ID(1)

    public Collision(){

        this.player_ids.maximum(255);
        this.collisions.maximum(255);
    }

    public Collision(Collision other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        Collision typedSrc = (Collision)src;
        this.player_ids.copy(typedSrc.player_ids);
        this.collisions.copy(typedSrc.collisions);
        return this;
    }
}