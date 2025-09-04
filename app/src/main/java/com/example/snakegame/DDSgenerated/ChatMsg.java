package com.example.snakegame.SnakeGame;

public class ChatMsg{
    public int player_id = 0;// @ID(0)
    public String content = "";// @ID(1)
    public int timestamp = 0;// @ID(2)

    public ChatMsg(){

    }

    public ChatMsg(ChatMsg other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        ChatMsg typedSrc = (ChatMsg)src;
        this.player_id =  typedSrc.player_id;
        this.content =  typedSrc.content;
        this.timestamp =  typedSrc.timestamp;
        return this;
    }
}