package com.example.snakegame.SnakeGame;

public class PlayerAuth{
    public int player_id = 0;// @ID(0)
    public String nickname = "";// @ID(1)
    public String password = "";// @ID(2)
    public String auth_type = "";// @ID(3)

    public PlayerAuth(){

    }

    public PlayerAuth(PlayerAuth other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        PlayerAuth typedSrc = (PlayerAuth)src;
        this.player_id =  typedSrc.player_id;
        this.nickname =  typedSrc.nickname;
        this.password =  typedSrc.password;
        this.auth_type =  typedSrc.auth_type;
        return this;
    }
}