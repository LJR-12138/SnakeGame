package com.example.snakegame.SnakeGame;

public class GetFood{
    public int player_id = 0;// @ID(0)
    public int item_id = 0;// @ID(1)
    public ItemType item_type = new ItemType();// @ID(2)
    public int x = 0;// @ID(3)
    public int y = 0;// @ID(4)

    public GetFood(){

    }

    public GetFood(GetFood other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        GetFood typedSrc = (GetFood)src;
        this.player_id =  typedSrc.player_id;
        this.item_id =  typedSrc.item_id;
        this.item_type.copy(typedSrc.item_type);
        this.x =  typedSrc.x;
        this.y =  typedSrc.y;
        return this;
    }
}