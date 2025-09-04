package com.example.snakegame.data.model;

import java.util.List;

public class GameWorld {
    private int gridSize;
    private int gridCols;  // 视野列数
    private int gridRows;  // 视野行数
    
    // 新增：大地图尺寸
    private int worldMapCols;  // 整个世界地图的列数
    private int worldMapRows;  // 整个世界地图的行数
    
    // 新增：视野偏移（相对于世界地图的偏移）
    private int viewOffsetX;   // 视野左上角在世界地图中的X坐标
    private int viewOffsetY;   // 视野左上角在世界地图中的Y坐标
    
    private long gameSpeed;
    private boolean gameRunning;
    private Snake mySnake;
    private List<Snake> otherSnakes;
    private List<Food> foods;
    private List<Player> leaderboard;
    
    public GameWorld() {
        this.gameRunning = false;
        this.gridSize = 20;
        this.gridCols = 20;  // 视野默认值
        this.gridRows = 20;  // 视野默认值
        
        // 设置大地图尺寸（比如100x100的大世界）
        this.worldMapCols = 100;
        this.worldMapRows = 100;
        
        // 初始视野偏移
        this.viewOffsetX = 0;
        this.viewOffsetY = 0;
        
        this.gameSpeed = 180;  // 确保默认速度与其他地方一致
    }
    
    // 原有的getters和setters...
    public int getGridSize() {
        return gridSize;
    }
    
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
    
    public int getGridCols() {
        return gridCols;
    }
    
    public void setGridCols(int gridCols) {
        this.gridCols = gridCols;
    }
    
    public int getGridRows() {
        return gridRows;
    }
    
    public void setGridRows(int gridRows) {
        this.gridRows = gridRows;
    }
    
    // 新增：大地图相关的getters和setters
    public int getWorldMapCols() {
        return worldMapCols;
    }
    
    public void setWorldMapCols(int worldMapCols) {
        this.worldMapCols = worldMapCols;
    }
    
    public int getWorldMapRows() {
        return worldMapRows;
    }
    
    public void setWorldMapRows(int worldMapRows) {
        this.worldMapRows = worldMapRows;
    }
    
    public int getViewOffsetX() {
        return viewOffsetX;
    }
    
    public void setViewOffsetX(int viewOffsetX) {
        this.viewOffsetX = viewOffsetX;
    }
    
    public int getViewOffsetY() {
        return viewOffsetY;
    }
    
    public void setViewOffsetY(int viewOffsetY) {
        this.viewOffsetY = viewOffsetY;
    }
    
    // 辅助方法：更新视野位置以蛇头为中心
    public void updateViewToCenter(Point snakeHead) {
        if (snakeHead != null) {
            // 计算视野偏移，使蛇头位于视野中心
            this.viewOffsetX = snakeHead.getX() - gridCols / 2;
            this.viewOffsetY = snakeHead.getY() - gridRows / 2;
            
            // 确保视野不超出世界地图边界
            this.viewOffsetX = Math.max(0, Math.min(this.viewOffsetX, worldMapCols - gridCols));
            this.viewOffsetY = Math.max(0, Math.min(this.viewOffsetY, worldMapRows - gridRows));
        }
    }
    
    // 辅助方法：检查世界坐标是否在当前视野内
    public boolean isInViewport(Point worldPoint) {
        return worldPoint.getX() >= viewOffsetX && 
               worldPoint.getX() < viewOffsetX + gridCols &&
               worldPoint.getY() >= viewOffsetY && 
               worldPoint.getY() < viewOffsetY + gridRows;
    }
    
    // 辅助方法：将世界坐标转换为视野坐标
    public Point worldToViewport(Point worldPoint) {
        return new Point(
            worldPoint.getX() - viewOffsetX,
            worldPoint.getY() - viewOffsetY
        );
    }
    
    // 辅助方法：将视野坐标转换为世界坐标
    public Point viewportToWorld(Point viewPoint) {
        return new Point(
            viewPoint.getX() + viewOffsetX,
            viewPoint.getY() + viewOffsetY
        );
    }
    
    // 其他getters和setters...
    public long getGameSpeed() {
        return gameSpeed;
    }
    
    public void setGameSpeed(long gameSpeed) {
        this.gameSpeed = gameSpeed;
    }
    
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }
    
    public Snake getMySnake() {
        return mySnake;
    }
    
    public void setMySnake(Snake mySnake) {
        this.mySnake = mySnake;
    }
    
    public List<Snake> getOtherSnakes() {
        return otherSnakes;
    }
    
    public void setOtherSnakes(List<Snake> otherSnakes) {
        this.otherSnakes = otherSnakes;
    }
    
    public List<Food> getFoods() {
        return foods;
    }
    
    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
    
    public List<Player> getLeaderboard() {
        return leaderboard;
    }
    
    public void setLeaderboard(List<Player> leaderboard) {
        this.leaderboard = leaderboard;
    }
}