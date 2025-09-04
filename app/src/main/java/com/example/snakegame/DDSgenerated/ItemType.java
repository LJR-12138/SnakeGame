package com.example.snakegame.SnakeGame;

public class ItemType {
    public static final ItemType APPLE =  new ItemType(0);

    public static final ItemType GOOD_FOOD =  new ItemType(1);

    public static final ItemType BAD_FOOD =  new ItemType(2);

    private int _ordinal;

    private ItemType(int ordinal){
        this._ordinal = ordinal;
    }

    public ItemType(ItemType other){
        this._ordinal = other.ordinal();
    }

    public Object copy(ItemType other){
        this._ordinal = other.ordinal();
        return this;
    }

    public ItemType(){
        _ordinal = 0;
    }

    public final int ordinal(){
        return this._ordinal;
    }

    public final void ordinal(int ordinal){
         this._ordinal = ordinal;
    }

    public static ItemType valueOf(int ordinal){
         switch (ordinal){
            case 0: return ItemType.APPLE;
            case 1: return ItemType.GOOD_FOOD;
            case 2: return ItemType.BAD_FOOD;
        }
        return null; 
    }

}