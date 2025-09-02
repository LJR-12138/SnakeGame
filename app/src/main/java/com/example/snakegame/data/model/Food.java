package com.example.snakegame.data.model;

public class Food {
    private Point position;
    private String type;
    private int value;
    
    public Food() {
        this.type = "normal";
        this.value = 10;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point position) {
        this.position = position;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}