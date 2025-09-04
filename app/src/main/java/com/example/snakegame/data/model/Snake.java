// filepath: d:\SnakeGame\app\src\main\java\com\example\snakegame\data\model\Snake.java
package com.example.snakegame.data.model;

import java.util.List;

public class Snake {
    private String playerId;
    private String nickname;
    private String color;
    private int score;
    private boolean alive;
    private List<Point> bodyPoints;
    private String direction;
    private boolean growing; // 添加这个字段
    private int growthPending = 0; // 待增长的节数
        // 在Snake类中添加这个字段和方法
    private boolean justAte = false;
    private boolean foodDropped = false; // 标记是否已经掉落过食物
    
    public boolean isJustAte() {
        return justAte;
    }
    
    public void setJustAte(boolean justAte) {
        this.justAte = justAte;
    }
    
    public Snake() {
        this.alive = true;
        this.score = 0;
        this.growing = false;
    }
    
    // Getters and Setters
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public List<Point> getBodyPoints() {
        return bodyPoints;
    }
    
    public void setBodyPoints(List<Point> bodyPoints) {
        this.bodyPoints = bodyPoints;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public boolean isGrowing() {
        return growing || growthPending > 0;
    }
    
    public void setGrowing(boolean growing) {
        this.growing = growing;
    }
    
    public int getGrowthPending() {
        return growthPending;
    }
    
    public void decreaseGrowthPending() {
        if (growthPending > 0) {
            growthPending--;
        }
    }
    
    // 获取蛇头位置
    public Point getHead() {
        if (bodyPoints != null && !bodyPoints.isEmpty()) {
            return bodyPoints.get(0);
        }
        return null;
    }
    
    // 蛇生长（吃到食物时调用）
    public void grow() {
        this.growthPending++;
    }
    
    // 蛇身体减少（吃到坏食物时调用）
    public void shrink() {
        if (bodyPoints != null && bodyPoints.size() > 1) {
            // 移除尾部节点，但至少保留蛇头
            bodyPoints.remove(bodyPoints.size() - 1);
        }
    }
    
    // 蛇身体增长指定数量
    public void growByAmount(int amount) {
        this.growthPending += amount;
    }
    
    // 食物掉落相关方法
    public boolean isFoodDropped() {
        return foodDropped;
    }
    
    public void setFoodDropped(boolean foodDropped) {
        this.foodDropped = foodDropped;
    }
}