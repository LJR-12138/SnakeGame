package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.StringSeq;

public class InRoom{
    public String room_id = "";// @ID(0)
    public int player_id = 0;// @ID(1)
    public String player_nickname = "";// @ID(2)
    public com.zrdds.infrastructure.LongSeq player_ids = new com.zrdds.infrastructure.LongSeq();// @ID(3)
    public StringSeq player_nicknames = new StringSeq();// @ID(4)
    public String room_state = "";// @ID(5)

    public InRoom(){

        this.player_ids.maximum(255);
        this.player_nicknames.maximum(255);
    }

    public InRoom(InRoom other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        InRoom typedSrc = (InRoom)src;
        this.room_id =  typedSrc.room_id;
        this.player_id =  typedSrc.player_id;
        this.player_nickname =  typedSrc.player_nickname;
        this.player_ids.copy(typedSrc.player_ids);
        this.player_nicknames.copy(typedSrc.player_nicknames);
        this.room_state =  typedSrc.room_state;
        return this;
    }
}