package com.example.snakegame.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.snakegame.data.model.GameWorld;
import com.example.snakegame.data.model.Snake;
import com.example.snakegame.data.model.Food;
import com.example.snakegame.data.model.Point;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private GameWorld gameWorld;
    
    // 移除图片资源，使用代码绘制
    
    // 游戏区域配置
    private int gridCols; // 列数
    private int gridRows; // 行数
    private int cellSize; // 每个格子的大小（方形）
    private int offsetX, offsetY; // 居中偏移

    // 在GameSurfaceView中添加缓存机制
    private GameWorld lastGameWorld;
    private boolean needsRedraw = true;
    
    
    public GameSurfaceView(Context context) {
        super(context);
        init();
    }
    
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public GameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        
        // 不再加载图片，使用代码绘制
    }
    
    // 移除图片加载方法，使用代码绘制
    
    public void updateGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        calculateGridLayout();
        
        // 直接绘制，不使用动画
        draw();
        
        this.lastGameWorld = gameWorld;
    }
    
    private void calculateGridLayout() {
        if (gameWorld == null || getWidth() == 0 || getHeight() == 0) return;
        
        // 计算最优的格子大小和行列数
        int baseGridSize = gameWorld.getGridSize(); // 基准大小，比如20
        
        // 根据屏幕比例计算行列数
        float screenRatio = (float) getWidth() / getHeight();
        
        if (screenRatio > 1.0f) {
            // 横屏或接近方形 - 列数更多
            gridCols = (int) (baseGridSize * screenRatio);
            gridRows = baseGridSize;
        } else {
            // 竖屏 - 行数更多
            gridCols = baseGridSize;
            gridRows = (int) (baseGridSize / screenRatio);
        }
        
        // 确保最小值
        gridCols = Math.max(gridCols, 15);
        gridRows = Math.max(gridRows, 15);
        
        // 计算每个格子的大小（保持方形）
        int cellSizeByWidth = getWidth() / gridCols;
        int cellSizeByHeight = getHeight() / gridRows;
        cellSize = Math.min(cellSizeByWidth, cellSizeByHeight);
        
        // 计算实际游戏区域大小和居中偏移
        int gameAreaWidth = gridCols * cellSize;
        int gameAreaHeight = gridRows * cellSize;
        offsetX = (getWidth() - gameAreaWidth) / 2;
        offsetY = (getHeight() - gameAreaHeight) / 2;
    }
    
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    drawGame(canvas);
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
    
    private void drawGame(Canvas canvas) {
        // 清空画布 - 使用深灰色背景
        canvas.drawColor(Color.parseColor("#2A2A2A"));
        
        if (gameWorld == null || cellSize == 0) return;
        
        // 隐藏网格线
        // drawGrid(canvas);
        
        // 绘制地图边界
        drawMapBoundary(canvas);
        
        // 绘制食物
        drawFood(canvas);
        
        // 绘制玩家的蛇
        drawSnake(canvas, gameWorld.getMySnake(), true);
        
        // 绘制其他玩家的蛇
        if (gameWorld.getOtherSnakes() != null) {
            for (Snake snake : gameWorld.getOtherSnakes()) {
                if (snake.isAlive()) {
                    drawSnake(canvas, snake, false);
                }
            }
        }
    }
    
    private void drawGrid(Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);
        
        // 绘制垂直线
        for (int i = 0; i <= gridCols; i++) {
            int x = offsetX + i * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + gridRows * cellSize, paint);
        }
        
        // 绘制水平线
        for (int i = 0; i <= gridRows; i++) {
            int y = offsetY + i * cellSize;
            canvas.drawLine(offsetX, y, offsetX + gridCols * cellSize, y, paint);
        }
    }
    
    private void drawMapBoundary(Canvas canvas) {
        if (gameWorld == null) return;
        
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        
        // 计算世界地图边界在当前视野中的位置
        int worldCols = gameWorld.getWorldMapCols();
        int worldRows = gameWorld.getWorldMapRows();
        int viewOffsetX = gameWorld.getViewOffsetX();
        int viewOffsetY = gameWorld.getViewOffsetY();
        
        // 左边界
        if (viewOffsetX <= 0) {
            int x = offsetX - viewOffsetX * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + gridRows * cellSize, paint);
        }
        
        // 右边界
        if (viewOffsetX + gridCols >= worldCols) {
            int x = offsetX + (worldCols - viewOffsetX) * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + gridRows * cellSize, paint);
        }
        
        // 上边界
        if (viewOffsetY <= 0) {
            int y = offsetY - viewOffsetY * cellSize;
            canvas.drawLine(offsetX, y, offsetX + gridCols * cellSize, y, paint);
        }
        
        // 下边界
        if (viewOffsetY + gridRows >= worldRows) {
            int y = offsetY + (worldRows - viewOffsetY) * cellSize;
            canvas.drawLine(offsetX, y, offsetX + gridCols * cellSize, y, paint);
        }
        
        // 恢复画笔样式
        paint.setStyle(Paint.Style.FILL);
    }
    
    private void drawFood(Canvas canvas) {
        if (gameWorld.getFoods() == null) return;
        
        for (Food food : gameWorld.getFoods()) {
            Point worldPos = food.getPosition();
            if (worldPos != null && gameWorld.isInViewport(worldPos)) {
                // 转换为视野坐标
                Point viewPos = gameWorld.worldToViewport(worldPos);
                
                float centerX = offsetX + viewPos.getX() * cellSize + cellSize / 2f;
                float centerY = offsetY + viewPos.getY() * cellSize + cellSize / 2f;
                
                // 根据食物类型绘制不同的形状
                switch (food.getType()) {
                    case APPLE:
                        // 红色圆形苹果
                        paint.setColor(Color.parseColor("#FF4444"));
                        float appleRadius = cellSize / 3f;
                        canvas.drawCircle(centerX, centerY, appleRadius, paint);
                        
                        // 绘制苹果的叶子（绿色小矩形）
                        paint.setColor(Color.parseColor("#4CAF50"));
                        float leafSize = cellSize / 8f;
                        canvas.drawRect(centerX - leafSize/2, centerY - appleRadius - leafSize, 
                                       centerX + leafSize/2, centerY - appleRadius, paint);
                        break;
                        
                    case GOOD_FOOD:
                        // 金色五角星
                        paint.setColor(Color.parseColor("#FFD700"));
                        drawStar(canvas, centerX, centerY, cellSize / 3f, paint);
                        break;
                        
                    case BAD_FOOD:
                        // 紫色骷髅头
                        paint.setColor(Color.parseColor("#9C27B0"));
                        float skullRadius = cellSize / 3f;
                        canvas.drawCircle(centerX, centerY, skullRadius, paint);
                        
                        // 绘制眼睛
                        paint.setColor(Color.BLACK);
                        float eyeRadius = cellSize / 12f;
                        canvas.drawCircle(centerX - skullRadius/2, centerY - skullRadius/3, eyeRadius, paint);
                        canvas.drawCircle(centerX + skullRadius/2, centerY - skullRadius/3, eyeRadius, paint);
                        
                        // 绘制嘴巴
                        canvas.drawRect(centerX - skullRadius/3, centerY + skullRadius/4, 
                                       centerX + skullRadius/3, centerY + skullRadius/2, paint);
                        break;
                }
            }
        }
    }
    
    // 绘制五角星的辅助方法
    private void drawStar(Canvas canvas, float centerX, float centerY, float radius, Paint paint) {
        paint.setAntiAlias(true);
        
        // 简化的星星绘制：绘制一个实心五角星
        Path starPath = new Path();
        
        // 计算五个外部点和五个内部点
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5.0;
            float r = (i % 2 == 0) ? radius : radius * 0.5f;
            float x = centerX + (float)(r * Math.cos(angle - Math.PI / 2));
            float y = centerY + (float)(r * Math.sin(angle - Math.PI / 2));
            
            if (i == 0) {
                starPath.moveTo(x, y);
            } else {
                starPath.lineTo(x, y);
            }
        }
        starPath.close();
        
        canvas.drawPath(starPath, paint);
    }
    
    private void drawSnake(Canvas canvas, Snake snake, boolean isMySnake) {
        if (snake == null || snake.getBodyPoints() == null) return;
        
        List<Point> bodyPoints = snake.getBodyPoints();
        
        for (int i = 0; i < bodyPoints.size(); i++) {
            Point worldPoint = bodyPoints.get(i);
            
            // 只绘制在视野内的部分
            if (gameWorld.isInViewport(worldPoint)) {
                drawSnakeSegment(canvas, worldPoint, snake.getColor(), i == 0, isMySnake, worldPoint.getX(), worldPoint.getY());
            }
        }
    }
    
    // 专门为动画蛇头设计的绘制方法，直接使用浮点坐标
    private void drawSnakeSegmentWithFloatCoords(Canvas canvas, float worldX, float worldY, String color, boolean isHead, boolean isMySnake) {
        // 转换为视野坐标（浮点数）
        float viewX = worldX - gameWorld.getViewOffsetX();
        float viewY = worldY - gameWorld.getViewOffsetY();
        
        // 检查是否在视野范围内
        if (viewX < -1 || viewX > gridCols || viewY < -1 || viewY > gridRows) {
            return; // 超出视野范围，不绘制
        }
        
        // 计算精确的像素位置
        float pixelX = offsetX + viewX * cellSize;
        float pixelY = offsetY + viewY * cellSize;
        
        if (isHead) {
            // 绘制蛇头 - 使用抗锯齿
            paint.setColor(Color.parseColor(color));
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            canvas.drawRoundRect(
                pixelX + 1,
                pixelY + 1,
                pixelX + cellSize - 1,
                pixelY + cellSize - 1,
                cellSize * 0.2f, cellSize * 0.2f,
                paint
            );
        } else {
            // 绘制蛇身
            paint.setColor(Color.parseColor(color));
            if (isMySnake) {
                paint.setAlpha(180);
            } else {
                paint.setAlpha(150);
            }
            paint.setAntiAlias(true);
            
            canvas.drawRoundRect(
                pixelX + 3,
                pixelY + 3,
                pixelX + cellSize - 3,
                pixelY + cellSize - 3,
                cellSize * 0.15f, cellSize * 0.15f,
                paint
            );
            paint.setAlpha(255);
        }
    }

    private void drawSnakeSegment(Canvas canvas, Point worldPoint, String color, boolean isHead, boolean isMySnake, float actualX, float actualY) {
        Point viewPoint = gameWorld.worldToViewport(worldPoint);
        
        // 计算精确的像素位置，减少浮点误差
        float baseX = offsetX + viewPoint.getX() * cellSize;
        float baseY = offsetY + viewPoint.getY() * cellSize;
        
        // 应用亚像素偏移
        float offsetDiffX = (actualX - worldPoint.getX()) * cellSize;
        float offsetDiffY = (actualY - worldPoint.getY()) * cellSize;
        
        float pixelX = baseX + offsetDiffX;
        float pixelY = baseY + offsetDiffY;
        
        if (isHead) {
            // 绘制蛇头 - 使用抗锯齿
            paint.setColor(Color.parseColor(color));
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            canvas.drawRoundRect(
                pixelX + 1,
                pixelY + 1,
                pixelX + cellSize - 1,
                pixelY + cellSize - 1,
                cellSize * 0.2f, cellSize * 0.2f,
                paint
            );
        } else {
            // 绘制蛇身
            paint.setColor(Color.parseColor(color));
            if (isMySnake) {
                paint.setAlpha(180);
            } else {
                paint.setAlpha(150);
            }
            paint.setAntiAlias(true);
            
            canvas.drawRoundRect(
                pixelX + 3,
                pixelY + 3,
                pixelX + cellSize - 3,
                pixelY + cellSize - 3,
                cellSize * 0.15f, cellSize * 0.15f,
                paint
            );
            paint.setAlpha(255);
        }
    }
    
    private boolean isPointInBounds(Point point) {
        return point.getX() >= 0 && point.getX() < gridCols && 
               point.getY() >= 0 && point.getY() < gridRows;
    }
    
    // 获取当前网格大小供游戏逻辑使用
    public int getGridCols() {
        return gridCols;
    }
    
    public int getGridRows() {
        return gridRows;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 只处理点击开始游戏，不处理滑动控制
        if (gameWorld == null || !gameWorld.isGameRunning()) {
            return false; // 返回false让MainActivity处理点击开始游戏
        }
        
        // 游戏运行中不处理任何触摸事件，只使用方向键控制
        return false;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Surface创建时的处理
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Surface改变时的处理，重新计算布局
        calculateGridLayout();
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface销毁时的处理，无需清理图片资源
    }
    
    private void cleanupResources() {
        // 移除图片资源清理，改为代码绘制
    }
}