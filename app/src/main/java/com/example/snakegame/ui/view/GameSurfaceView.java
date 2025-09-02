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
    
    // 快捷语句功能相关
    private boolean showQuickMessages = false; // 是否显示快捷语句面板
    private String[] quickMessages = {
        "菜！就多练", 
        "抱歉", 
        "大哥别杀我", 
        "哈基米哟~南北绿豆", 
        "是兄弟就来干我！",
        "不收徒"
    }; // 预设的快捷语句
    private ChatBubble activeChatBubble; // 当前显示的聊天气泡
    private long chatBubbleStartTime; // 聊天气泡开始显示的时间
    private static final long CHAT_BUBBLE_DURATION = 2500; // 聊天气泡显示2.5秒
    
    
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
        
        // 绘制快捷语句面板（在游戏内容之上）
        drawQuickMessagePanel(canvas);
        
        // 绘制聊天气泡
        drawChatBubble(canvas);
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
                drawSnakeSegment(canvas, worldPoint, snake.getColor(), i == 0, isMySnake, worldPoint.getX(), worldPoint.getY(), snake);
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

    private void drawSnakeSegment(Canvas canvas, Point worldPoint, String color, boolean isHead, boolean isMySnake, float actualX, float actualY, Snake snake) {
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
            
            // 在蛇头上方绘制玩家昵称
            drawPlayerNickname(canvas, snake, pixelX, pixelY);
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();
            
            // 检查是否点击了快捷语句按钮 - 圆形按钮
            float buttonCenterX = getWidth() - 70; // 更新按钮位置
            float buttonCenterY = getHeight() / 2f;
            float buttonRadius = 65; // 更新按钮半径
            
            // 计算点击位置到按钮中心的距离
            float distance = (float) Math.sqrt(
                Math.pow(touchX - buttonCenterX, 2) + Math.pow(touchY - buttonCenterY, 2)
            );
            
            if (distance <= buttonRadius) {
                // 切换快捷语句面板显示状态
                showQuickMessages = !showQuickMessages;
                return true;
            }
            
            // 如果快捷语句面板显示，检查是否点击了面板中的选项
            if (showQuickMessages) {
                if (handleQuickMessageSelection(touchX, touchY)) {
                    return true;
                }
            }
        }
        
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
    
    /**
     * 根据蛇的移动方向绘制玩家昵称
     */
    private void drawPlayerNickname(Canvas canvas, Snake snake, float pixelX, float pixelY) {
        if (snake == null || snake.getNickname() == null || snake.getNickname().isEmpty()) {
            return;
        }
        
        String nickname = snake.getNickname();
        String direction = snake.getDirection();
        
        // 设置文字画笔 - 调整文字大小，保持可读性
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(cellSize * 0.5f); // 稍微减小文字大小避免过大
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); // 使用粗体字
        textPaint.setShadowLayer(3, 1, 1, Color.BLACK); // 阴影效果
        
        // 根据蛇的移动方向计算昵称显示位置，昵称显示在蛇头前进方向上
        float textX = pixelX + cellSize / 2f; // 水平居中（默认）
        float textY = pixelY - cellSize * 0.8f; // 默认在上方
        
        // 根据移动方向调整昵称位置 - 昵称显示在蛇头前进的方向上，保持一致的距离
        float offset = cellSize * 0.4f; // 统一的偏移距离
        if (direction != null) {
            switch (direction) {
                case "UP":
                    // 向上移动时，昵称显示在蛇头上方
                    textX = pixelX + cellSize / 2f;
                    textY = pixelY - offset; // 蛇头上方，统一距离
                    break;
                case "DOWN":
                    // 向下移动时，昵称显示在蛇头下方
                    textX = pixelX + cellSize / 2f;
                    textY = pixelY + cellSize + offset; // 蛇头下方，统一距离
                    break;
                case "LEFT":
                    // 向左移动时，昵称显示在蛇头左侧
                    textX = pixelX - offset; // 蛇头左侧，统一距离
                    textY = pixelY + cellSize * 0.7f; // 垂直稍微偏下
                    break;
                case "RIGHT":
                    // 向右移动时，昵称显示在蛇头右侧
                    textX = pixelX + cellSize + offset; // 蛇头右侧，统一距离
                    textY = pixelY + cellSize * 0.7f; // 垂直稍微偏下
                    break;
                default:
                    // 默认在上方
                    textX = pixelX + cellSize / 2f;
                    textY = pixelY - offset;
                    break;
            }
        }
        
        // 绘制背景框
        Paint backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setAlpha(140); // 适中的背景不透明度
        
        // 测量文字宽度和高度
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textWidth = textPaint.measureText(nickname);
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        
        // 绘制背景矩形
        float padding = cellSize * 0.08f; // 适中的内边距
        canvas.drawRoundRect(
            textX - textWidth / 2f - padding,
            textY - textHeight + fontMetrics.bottom - padding,
            textX + textWidth / 2f + padding,
            textY + fontMetrics.bottom + padding,
            padding, padding,
            backgroundPaint
        );
        
        // 绘制昵称文字
        canvas.drawText(nickname, textX, textY, textPaint);
    }
    
    /**
     * 绘制快捷语句栏
     */
    private void drawQuickMessagePanel(Canvas canvas) {
        int panelWidth = getWidth() / 5; // 增大面板宽度
        int panelHeight = getHeight() / 2; // 面板高度为屏幕高度的1/2
        int panelX = getWidth() - panelWidth - 150; // 向左移动，留出触发按钮的空间
        int panelY = getHeight() / 4; // 垂直居中
        
        // 绘制快捷语句按钮（右侧边栏）- 更大的圆形按钮
        Paint buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        
        // 渐变背景
        float buttonCenterX = getWidth() - 70; // 稍微向左移动一点
        float buttonCenterY = getHeight() / 2f;
        float buttonRadius = 65; // 增大按钮半径到65px
        
        android.graphics.RadialGradient buttonGradient = new android.graphics.RadialGradient(
            buttonCenterX, buttonCenterY, buttonRadius,
            Color.parseColor("#4FC3F7"), // 浅蓝色
            Color.parseColor("#29B6F6"), // 深蓝色
            android.graphics.Shader.TileMode.CLAMP
        );
        buttonPaint.setShader(buttonGradient);
        
        // 绘制圆形按钮
        canvas.drawCircle(buttonCenterX, buttonCenterY, buttonRadius, buttonPaint);
        
        // 按钮边框
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.parseColor("#1976D2"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        canvas.drawCircle(buttonCenterX, buttonCenterY, buttonRadius, borderPaint);
        
        // 绘制聊天图标（两个对话气泡）
        drawChatIcon(canvas, buttonCenterX, buttonCenterY);
        
        // 如果显示快捷语句面板
        if (showQuickMessages) {
            drawQuickMessagesList(canvas, panelX, panelY, panelWidth, panelHeight);
            
            // 绘制连接线，表示面板和按钮的关联
            drawConnectionLine(canvas, panelX + panelWidth, panelY + panelHeight / 2f, buttonCenterX, buttonCenterY);
        }
    }
    
    /**
     * 绘制聊天图标
     */
    private void drawChatIcon(Canvas canvas, float centerX, float centerY) {
        Paint iconPaint = new Paint();
        iconPaint.setAntiAlias(true);
        iconPaint.setColor(Color.WHITE);
        
        // 绘制主对话气泡（蓝色，类似您的图标）
        Paint bubble1Paint = new Paint();
        bubble1Paint.setAntiAlias(true);
        bubble1Paint.setColor(Color.parseColor("#00BCD4")); // 青色，更接近您的图标
        
        float bubble1X = centerX + 5;
        float bubble1Y = centerY - 5;
        float bubble1Width = 28;
        float bubble1Height = 20;
        
        canvas.drawRoundRect(
            bubble1X - bubble1Width/2, bubble1Y - bubble1Height/2,
            bubble1X + bubble1Width/2, bubble1Y + bubble1Height/2,
            8, 8, bubble1Paint
        );
        
        // 主气泡的尖角
        Path bubble1Tail = new Path();
        bubble1Tail.moveTo(bubble1X - bubble1Width/2 + 6, bubble1Y + bubble1Height/2);
        bubble1Tail.lineTo(bubble1X - bubble1Width/2 - 4, bubble1Y + bubble1Height/2 + 8);
        bubble1Tail.lineTo(bubble1X - bubble1Width/2 + 10, bubble1Y + bubble1Height/2);
        bubble1Tail.close();
        canvas.drawPath(bubble1Tail, bubble1Paint);
        
        // 绘制副对话气泡（橙色，类似您的图标）
        Paint bubble2Paint = new Paint();
        bubble2Paint.setAntiAlias(true);
        bubble2Paint.setColor(Color.parseColor("#FF9800")); // 橙色
        
        float bubble2X = centerX - 5;
        float bubble2Y = centerY + 5;
        float bubble2Width = 24;
        float bubble2Height = 16;
        
        canvas.drawRoundRect(
            bubble2X - bubble2Width/2, bubble2Y - bubble2Height/2,
            bubble2X + bubble2Width/2, bubble2Y + bubble2Height/2,
            6, 6, bubble2Paint
        );
        
        // 副气泡的尖角
        Path bubble2Tail = new Path();
        bubble2Tail.moveTo(bubble2X + bubble2Width/2 - 6, bubble2Y + bubble2Height/2);
        bubble2Tail.lineTo(bubble2X + bubble2Width/2 + 4, bubble2Y + bubble2Height/2 + 6);
        bubble2Tail.lineTo(bubble2X + bubble2Width/2 - 10, bubble2Y + bubble2Height/2);
        bubble2Tail.close();
        canvas.drawPath(bubble2Tail, bubble2Paint);
        
        // 绘制气泡内的省略号点点（白色）
        Paint dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.WHITE);
        
        // 主气泡内的三个点
        float dotRadius = 2.5f;
        canvas.drawCircle(bubble1X - 8, bubble1Y, dotRadius, dotPaint);
        canvas.drawCircle(bubble1X, bubble1Y, dotRadius, dotPaint);
        canvas.drawCircle(bubble1X + 8, bubble1Y, dotRadius, dotPaint);
        
        // 副气泡内的两个点
        canvas.drawCircle(bubble2X - 5, bubble2Y, dotRadius, dotPaint);
        canvas.drawCircle(bubble2X + 5, bubble2Y, dotRadius, dotPaint);
    }
    
    /**
     * 绘制面板和按钮之间的连接线
     */
    private void drawConnectionLine(Canvas canvas, float panelEndX, float panelCenterY, float buttonCenterX, float buttonCenterY) {
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#4FC3F7"));
        linePaint.setStrokeWidth(6);
        linePaint.setAlpha(150);
        
        // 绘制连接线
        canvas.drawLine(panelEndX, panelCenterY, buttonCenterX - 65, buttonCenterY, linePaint);
        
        // 绘制小箭头
        Paint arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setColor(Color.parseColor("#29B6F6"));
        
        float arrowX = buttonCenterX - 75;
        float arrowY = buttonCenterY;
        
        Path arrowPath = new Path();
        arrowPath.moveTo(arrowX, arrowY);
        arrowPath.lineTo(arrowX - 12, arrowY - 8);
        arrowPath.lineTo(arrowX - 12, arrowY + 8);
        arrowPath.close();
        
        canvas.drawPath(arrowPath, arrowPaint);
    }
    
    /**
     * 绘制快捷语句列表
     */
    private void drawQuickMessagesList(Canvas canvas, int panelX, int panelY, int panelWidth, int panelHeight) {
        // 绘制背景面板阴影
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(80);
        float shadowOffset = 8;
        canvas.drawRoundRect(
            panelX + shadowOffset, panelY + shadowOffset, 
            panelX + panelWidth + shadowOffset, panelY + panelHeight + shadowOffset, 
            20, 20, shadowPaint
        );
        
        // 绘制背景面板 - 渐变背景
        Paint panelPaint = new Paint();
        panelPaint.setAntiAlias(true);
        android.graphics.LinearGradient panelGradient = new android.graphics.LinearGradient(
            panelX, panelY,
            panelX, panelY + panelHeight,
            Color.parseColor("#37474F"), // 深灰色
            Color.parseColor("#263238"), // 更深的灰色
            android.graphics.Shader.TileMode.CLAMP
        );
        panelPaint.setShader(panelGradient);
        canvas.drawRoundRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 20, 20, panelPaint);
        
        // 面板边框
        Paint panelBorderPaint = new Paint();
        panelBorderPaint.setAntiAlias(true);
        panelBorderPaint.setColor(Color.parseColor("#1976D2"));
        panelBorderPaint.setStyle(Paint.Style.STROKE);
        panelBorderPaint.setStrokeWidth(3);
        canvas.drawRoundRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 20, 20, panelBorderPaint);
        
        // 绘制快捷语句选项
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32); // 增大字体
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setShadowLayer(2, 1, 1, Color.BLACK);
        
        Paint itemBgPaint = new Paint();
        itemBgPaint.setAntiAlias(true);
        
        int itemHeight = panelHeight / quickMessages.length;
        for (int i = 0; i < quickMessages.length; i++) {
            float itemY = panelY + i * itemHeight;
            
            // 每个选项的渐变背景
            android.graphics.LinearGradient itemGradient = new android.graphics.LinearGradient(
                panelX + 15, itemY + 8,
                panelX + panelWidth - 15, itemY + itemHeight - 8,
                Color.parseColor("#4FC3F7"), // 浅蓝色
                Color.parseColor("#29B6F6"), // 深蓝色
                android.graphics.Shader.TileMode.CLAMP
            );
            itemBgPaint.setShader(itemGradient);
            
            // 绘制选项背景
            canvas.drawRoundRect(
                panelX + 15, itemY + 8,
                panelX + panelWidth - 15, itemY + itemHeight - 8,
                12, 12, itemBgPaint
            );
            
            // 选项边框
            Paint itemBorderPaint = new Paint();
            itemBorderPaint.setAntiAlias(true);
            itemBorderPaint.setColor(Color.parseColor("#1976D2"));
            itemBorderPaint.setStyle(Paint.Style.STROKE);
            itemBorderPaint.setStrokeWidth(2);
            canvas.drawRoundRect(
                panelX + 15, itemY + 8,
                panelX + panelWidth - 15, itemY + itemHeight - 8,
                12, 12, itemBorderPaint
            );
            
            // 绘制选项文字
            canvas.drawText(
                quickMessages[i],
                panelX + panelWidth / 2f,
                itemY + itemHeight / 2f + 12,
                textPaint
            );
        }
    }
    
    /**
     * 绘制聊天气泡
     */
    private void drawChatBubble(Canvas canvas) {
        if (activeChatBubble == null || gameWorld == null || gameWorld.getMySnake() == null) {
            return;
        }
        
        // 检查气泡是否过期
        long currentTime = System.currentTimeMillis();
        if (currentTime - chatBubbleStartTime > CHAT_BUBBLE_DURATION) {
            activeChatBubble = null;
            return;
        }
        
        // 获取蛇头位置
        Snake mySnake = gameWorld.getMySnake();
        List<Point> bodyPoints = mySnake.getBodyPoints();
        if (bodyPoints == null || bodyPoints.isEmpty()) {
            return;
        }
        
        Point head = bodyPoints.get(0);
        
        // 转换为屏幕坐标
        float screenX = offsetX + (head.getX() - gameWorld.getViewOffsetX()) * cellSize;
        float screenY = offsetY + (head.getY() - gameWorld.getViewOffsetY()) * cellSize;
        
        // 检查是否在视野内
        if (screenX < -cellSize || screenX > getWidth() + cellSize ||
            screenY < -cellSize || screenY > getHeight() + cellSize) {
            return; // 不在视野内，不显示
        }
        
        // 绘制聊天气泡 - 调整位置让其更醒目
        drawChatBubbleAt(canvas, activeChatBubble.message, screenX + cellSize / 2f, screenY - cellSize * 2.0f);
    }
    
    /**
     * 在指定位置绘制聊天气泡
     */
    private void drawChatBubbleAt(Canvas canvas, String message, float x, float y) {
        // 设置文字画笔 - 适中的字体
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(36); // 减小字体到36px
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        
        // 测量文字尺寸
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textWidth = textPaint.measureText(message);
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        
        // 计算气泡尺寸 - 减小内边距
        float padding = 25; // 减小内边距到25px
        float bubbleWidth = Math.max(textWidth + padding * 2, 140); // 减小最小宽度
        float bubbleHeight = textHeight + padding;
        
        // 绘制气泡阴影
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(50);
        
        float shadowOffset = 6; // 减小阴影偏移
        canvas.drawRoundRect(
            x - bubbleWidth / 2f + shadowOffset,
            y - bubbleHeight + shadowOffset,
            x + bubbleWidth / 2f + shadowOffset,
            y + shadowOffset,
            16, 16, // 减小圆角
            shadowPaint
        );
        
        // 绘制气泡背景 - 渐变色效果
        Paint bubblePaint = new Paint();
        bubblePaint.setAntiAlias(true);
        
        // 使用王者荣耀风格的蓝色渐变
        android.graphics.LinearGradient gradient = new android.graphics.LinearGradient(
            x - bubbleWidth / 2f, y - bubbleHeight,
            x + bubbleWidth / 2f, y,
            Color.parseColor("#4FC3F7"), // 浅蓝色
            Color.parseColor("#29B6F6"), // 深蓝色
            android.graphics.Shader.TileMode.CLAMP
        );
        bubblePaint.setShader(gradient);
        
        // 绘制圆角矩形气泡
        canvas.drawRoundRect(
            x - bubbleWidth / 2f,
            y - bubbleHeight,
            x + bubbleWidth / 2f,
            y,
            16, 16, // 减小圆角
            bubblePaint
        );
        
        // 绘制气泡边框 - 稍细的边框
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.parseColor("#1976D2")); // 深蓝色边框
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3); // 减小边框宽度
        
        canvas.drawRoundRect(
            x - bubbleWidth / 2f,
            y - bubbleHeight,
            x + bubbleWidth / 2f,
            y,
            16, 16,
            borderPaint
        );
        
        // 绘制气泡尖角 - 稍小的尖角
        Path trianglePath = new Path();
        trianglePath.moveTo(x - 15, y);
        trianglePath.lineTo(x, y + 18); // 稍小的尖角
        trianglePath.lineTo(x + 15, y);
        trianglePath.close();
        
        // 尖角阴影
        Path shadowTrianglePath = new Path();
        shadowTrianglePath.moveTo(x - 15 + shadowOffset, y + shadowOffset);
        shadowTrianglePath.lineTo(x + shadowOffset, y + 18 + shadowOffset);
        shadowTrianglePath.lineTo(x + 15 + shadowOffset, y + shadowOffset);
        shadowTrianglePath.close();
        canvas.drawPath(shadowTrianglePath, shadowPaint);
        
        // 绘制尖角
        canvas.drawPath(trianglePath, bubblePaint);
        canvas.drawPath(trianglePath, borderPaint);
        
        // 绘制文字 - 白色文字更醒目
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(2, 1, 1, Color.parseColor("#1976D2")); // 文字阴影
        canvas.drawText(message, x, y - bubbleHeight / 2f + textHeight / 2f - 6, textPaint);
    }
    
    /**
     * 聊天气泡数据类
     */
    private static class ChatBubble {
        String message;
        long timestamp;
        
        ChatBubble(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 处理快捷语句选项点击
     */
    private boolean handleQuickMessageSelection(float touchX, float touchY) {
        int panelWidth = getWidth() / 5; // 更新面板宽度
        int panelHeight = getHeight() / 2;
        int panelX = getWidth() - panelWidth - 150; // 更新面板位置，向左移动
        int panelY = getHeight() / 4;
        
        // 检查是否在面板范围内
        if (touchX >= panelX && touchX <= panelX + panelWidth &&
            touchY >= panelY && touchY <= panelY + panelHeight) {
            
            // 计算点击的是第几个选项
            int itemHeight = panelHeight / quickMessages.length;
            int selectedIndex = (int) ((touchY - panelY) / itemHeight);
            
            if (selectedIndex >= 0 && selectedIndex < quickMessages.length) {
                // 显示选中的快捷语句
                String selectedMessage = quickMessages[selectedIndex];
                showChatBubble(selectedMessage);
                
                // 隐藏快捷语句面板
                showQuickMessages = false;
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 显示聊天气泡
     */
    private void showChatBubble(String message) {
        activeChatBubble = new ChatBubble(message);
        chatBubbleStartTime = System.currentTimeMillis();
    }
}