package com.example.snakegame.SnakeGame;

public class Item{
    public int item_id = 0;// @ID(0)
    public ItemType item_type = new ItemType();// @ID(1)
    public int x = 0;// @ID(2)
    public int y = 0;// @ID(3)
    public String image_id = "";// @ID(4)

    public Item(){

    }

    public Item(Item other){
        this();
        copy(other);
    }

    public Object copy(Object src) {
        Item typedSrc = (Item)src;
        this.item_id =  typedSrc.item_id;
        this.item_type.copy(typedSrc.item_type);
        this.x =  typedSrc.x;
        this.y =  typedSrc.y;
        this.image_id =  typedSrc.image_id;
        return this;
    }
}