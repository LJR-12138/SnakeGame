package com.example.snakegame.data.model;

public class Food {
    // 食物类型枚举
    public enum FoodType {
        APPLE,      // 普通苹果，+1长度
        GOOD_FOOD,  // 星星，+2长度
        BAD_FOOD    // 骷髅头，-1长度
    }
    
    private Point position;
    private FoodType type;
    private int value;
    private int lengthChange; // 对蛇身长度的影响
    
    public Food() {
        this.type = FoodType.APPLE;
        this.value = 10;
        this.lengthChange = 1;
    }
    
    public Food(FoodType type) {
        this.type = type;
        setupFoodProperties();
    }
    
    private void setupFoodProperties() {
        switch (type) {
            case APPLE:
                this.value = 10;
                this.lengthChange = 1;
                break;
            case GOOD_FOOD:
                this.value = 20;
                this.lengthChange = 2;
                break;
            case BAD_FOOD:
                this.value = -10;
                this.lengthChange = -1;
                break;
        }
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point position) {
        this.position = position;
    }
    
    public FoodType getType() {
        return type;
    }
    
    public void setType(FoodType type) {
        this.type = type;
        setupFoodProperties();
    }
    
    // 为了兼容现有代码，保留String类型的setter
    public void setType(String typeString) {
        switch (typeString.toLowerCase()) {
            case "good":
                setType(FoodType.GOOD_FOOD);
                break;
            case "bad":
                setType(FoodType.BAD_FOOD);
                break;
            case "normal":
            case "apple":
            default:
                setType(FoodType.APPLE);
                break;
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public int getLengthChange() {
        return lengthChange;
    }
    
    public void setLengthChange(int lengthChange) {
        this.lengthChange = lengthChange;
    }
    
    // 获取食物类型的字符串表示（用于渲染）
    public String getTypeString() {
        switch (type) {
            case APPLE:
                return "apple";
            case GOOD_FOOD:
                return "good";
            case BAD_FOOD:
                return "bad";
            default:
                return "apple";
        }
    }
}