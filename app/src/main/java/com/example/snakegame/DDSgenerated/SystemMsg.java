package com.example.snakegame.SnakeGame;

public class SystemMsg{
    public String msg_type = "";// @ID(0)
    public String content = "";// @ID(1)
    public int timestamp = 0;// @ID(2)

    public SystemMsg(){

    }

    public SystemMsg(SystemMsg other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        SystemMsg typedSrc = (SystemMsg)src;
        this.msg_type =  typedSrc.msg_type;
        this.content =  typedSrc.content;
        this.timestamp =  typedSrc.timestamp;
        return this;
    }
}