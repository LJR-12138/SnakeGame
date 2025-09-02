package com.example.snakegame.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    
    // 添加缺失的常量
    public static final int APPLE = 1;
    public static final int GOOD_FOOD = 2;
    public static final int BAD_FOOD = 3;
    
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private GameWorld gameWorld;
    
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
    }
    
    public void updateGameWorld(GameWorld gameWorld) {
    this.gameWorld = gameWorld;
    calculateGridLayout();
    
    // 简化判断逻辑，确保能够正常重绘
    if (shouldRedraw(gameWorld)) {
        draw();
        this.lastGameWorld = gameWorld;
    }
    }

    private boolean shouldRedraw(GameWorld newGameWorld) {
    // 第一次绘制
    if (lastGameWorld == null) return true;
    
    // 游戏状态改变
    if (newGameWorld.isGameRunning() != lastGameWorld.isGameRunning()) return true;
    
    // 视野改变
    if (newGameWorld.getViewOffsetX() != lastGameWorld.getViewOffsetX() ||
        newGameWorld.getViewOffsetY() != lastGameWorld.getViewOffsetY()) return true;
    
    // 蛇的身体点数量改变（吃食物或移动）
    if (newGameWorld.getMySnake() != null && lastGameWorld.getMySnake() != null) {
        if (newGameWorld.getMySnake().getBodyPoints().size() != 
            lastGameWorld.getMySnake().getBodyPoints().size()) return true;
    }
    
    // 食物数量改变
    if (newGameWorld.getFoods() != null && lastGameWorld.getFoods() != null) {
        if (newGameWorld.getFoods().size() != lastGameWorld.getFoods().size()) return true;
    }
    
    // 默认重绘，确保不会卡住
    return true;
}

    // 检查是否有重要变化
    // 修改hasSignificantChange方法
private boolean hasSignificantChange(GameWorld newGameWorld) {
    if (lastGameWorld == null) return true;
    
    // 检查视野是否改变
    if (lastGameWorld.getViewOffsetX() != newGameWorld.getViewOffsetX() ||
        lastGameWorld.getViewOffsetY() != newGameWorld.getViewOffsetY()) {
        return true;
    }
    
    // 检查蛇的位置是否改变
    if (newGameWorld.getMySnake() != null && lastGameWorld.getMySnake() != null) {
        Point newHead = getSnakeHead(newGameWorld.getMySnake());  // 现在这个方法存在了
        Point oldHead = getSnakeHead(lastGameWorld.getMySnake());
        if (newHead != null && oldHead != null && 
            (!newHead.equals(oldHead))) {
            return true;
        }
    }
    
    return false;
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
                
                // 根据食物类型设置颜色
                switch (food.getType()) {
                    case "normal":
                        paint.setColor(Color.RED);
                        break;
                    case "good":
                        paint.setColor(Color.GREEN);
                        break;
                    case "bad":
                        paint.setColor(Color.YELLOW);
                        break;
                    default:
                        paint.setColor(Color.RED);
                        break;
                }
                
                // 使用视野坐标绘制圆形食物
                float centerX = offsetX + viewPos.getX() * cellSize + cellSize / 2f;
                float centerY = offsetY + viewPos.getY() * cellSize + cellSize / 2f;
                float radius = cellSize / 3f;
                canvas.drawCircle(centerX, centerY, radius, paint);
            }
        }
    }
    
    private void drawSnake(Canvas canvas, Snake snake, boolean isMySnake) {
        if (snake == null || snake.getBodyPoints() == null) return;
        
        List<Point> bodyPoints = snake.getBodyPoints();
        
        for (int i = 0; i < bodyPoints.size(); i++) {
            Point worldPoint = bodyPoints.get(i);
            
            // 只绘制在视野内的蛇身体部分
            if (gameWorld.isInViewport(worldPoint)) {
                Point viewPoint = gameWorld.worldToViewport(worldPoint);
                
                if (i == 0) {
                    // 绘制蛇头
                    paint.setColor(Color.parseColor(snake.getColor()));
                    canvas.drawRect(
                        offsetX + viewPoint.getX() * cellSize + 2,
                        offsetY + viewPoint.getY() * cellSize + 2,
                        offsetX + (viewPoint.getX() + 1) * cellSize - 2,
                        offsetY + (viewPoint.getY() + 1) * cellSize - 2,
                        paint
                    );
                } else {
                    // 绘制蛇身
                    paint.setColor(Color.parseColor(snake.getColor()));
                    if (isMySnake) {
                        paint.setAlpha(180);
                    } else {
                        paint.setAlpha(150);
                    }
                    
                    canvas.drawRect(
                        offsetX + viewPoint.getX() * cellSize + 4,
                        offsetY + viewPoint.getY() * cellSize + 4,
                        offsetX + (viewPoint.getX() + 1) * cellSize - 4,
                        offsetY + (viewPoint.getY() + 1) * cellSize - 4,
                        paint
                    );
                    paint.setAlpha(255);
                }
            }
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
        // Surface销毁时的处理
    }


    // 在GameSurfaceView类中添加getSnakeHead方法
private Point getSnakeHead(Snake snake) {
    if (snake != null && snake.getBodyPoints() != null && !snake.getBodyPoints().isEmpty()) {
        return snake.getBodyPoints().get(0);
    }
    return null;
}
}