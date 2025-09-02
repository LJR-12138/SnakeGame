package com.example.snakegame.presentation.presenter;

import android.os.Handler;
import android.os.Looper;
import com.example.snakegame.data.model.GameWorld;
import com.example.snakegame.data.model.Snake;
import com.example.snakegame.data.model.Player;
import com.example.snakegame.data.model.Point;
import com.example.snakegame.data.model.Food;
import com.example.snakegame.presentation.contract.GameContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePresenter implements GameContract.Presenter {
    
    private GameContract.View view;
    private GameWorld gameWorld;
    private Handler mainHandler;
    private Runnable gameUpdateRunnable;
    private boolean isGameActive;
    
    // 玩家信息
    private String playerId;
    private String playerNickname;
    private String playerColor;
    
    public GamePresenter() {
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.isGameActive = false;
    }
    
    @Override
    public void attachView(GameContract.View view) {
        this.view = view;
    }
    
    @Override
    public void detachView() {
        this.view = null;
        stopGameLoop();
    }
    
    @Override
    public void initializeGame(long playerId, String nickname, String color) {
        this.playerId = String.valueOf(playerId);
        this.playerNickname = nickname;
        this.playerColor = color;
        
        if (view != null) {
            view.showLoading();
        }
        
        // 生成模拟数据
        generateMockData();
        
        if (view != null) {
            view.hideLoading();
            view.onGameWorldUpdated(gameWorld);
        }
    }
    
// 修改generateMockData方法
private void generateMockData() {
    gameWorld = new GameWorld();
    gameWorld.setGridSize(20);
    gameWorld.setGameSpeed(200);
    
    // 设置大世界地图（100x100）
    gameWorld.setWorldMapCols(100);
    gameWorld.setWorldMapRows(100);
    
    // 创建玩家的蛇（在世界中心附近）
    Snake mySnake = new Snake();
    mySnake.setPlayerId(playerId);
    mySnake.setNickname(playerNickname);
    mySnake.setColor(playerColor);
    mySnake.setScore(0);
    mySnake.setAlive(true);
    
    // 在世界地图中心附近初始化蛇
    List<Point> bodyPoints = new ArrayList<>();
    bodyPoints.add(new Point(50, 50));  // 世界坐标
    bodyPoints.add(new Point(50, 51));
    bodyPoints.add(new Point(50, 52));
    mySnake.setBodyPoints(bodyPoints);
    mySnake.setDirection("UP");
    
    gameWorld.setMySnake(mySnake);
    
    // 更新视野以蛇头为中心
    gameWorld.updateViewToCenter(bodyPoints.get(0));
    
    // 创建Bot蛇
    createBotSnakes();
    
    updateLeaderboard();
    
    // 使用新的初始食物生成方法
    generateInitialFood();
}


        // 添加createBotSnakes方法到GamePresenter类中
    private void createBotSnakes() {
        List<Snake> otherSnakes = new ArrayList<>();
        Random random = new Random();
        
        // 创建多个Bot蛇
        String[] botNames = {"Bot Alpha", "Bot Beta", "Bot Gamma", "Snake AI"};
        String[] botColors = {"#FF00FF", "#00FFFF", "#FFFF00", "#FF8000"};
        
        for (int i = 0; i < botNames.length; i++) {
            Snake botSnake = new Snake();
            botSnake.setPlayerId("bot" + (i + 1));
            botSnake.setNickname(botNames[i]);
            botSnake.setColor(botColors[i]);
            botSnake.setScore(random.nextInt(15) + 1); // 1-15的随机分数
            botSnake.setAlive(true);
            
            // 在世界地图的随机位置生成Bot蛇
            List<Point> botBodyPoints = new ArrayList<>();
            Point startPos;
            int attempts = 0;
            
            do {
                startPos = new Point(
                    random.nextInt(gameWorld.getWorldMapCols() - 10) + 5,
                    random.nextInt(gameWorld.getWorldMapRows() - 10) + 5
                );
                attempts++;
            } while (isPositionOccupied(startPos) && attempts < 20);
            
            if (attempts < 20) {
                botBodyPoints.add(startPos);
                botBodyPoints.add(new Point(startPos.getX(), startPos.getY() + 1));
                botBodyPoints.add(new Point(startPos.getX(), startPos.getY() + 2));
                
                botSnake.setBodyPoints(botBodyPoints);
                
                // 随机方向
                String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
                botSnake.setDirection(directions[random.nextInt(directions.length)]);
                
                otherSnakes.add(botSnake);
            }
        }
        
        gameWorld.setOtherSnakes(otherSnakes);
    }

    // 修改初始食物生成
    private void generateInitialFood() {
    List<Food> foods = new ArrayList<>();
    Random random = new Random();
    
    // 在整个世界地图上生成初始食物
    for (int i = 0; i < 30; i++) {  // 生成30个初始食物
        Food food = new Food();
        Point position;
        int attempts = 0;
        
        do {
            position = new Point(
                random.nextInt(gameWorld.getWorldMapCols()),
                random.nextInt(gameWorld.getWorldMapRows())
            );
            attempts++;
        } while (isPositionOccupied(position) && attempts < 50);
        
        if (attempts < 50) {
            food.setPosition(position);
            
            // 随机生成不同类型的食物
            int type = random.nextInt(10);
            if (type == 0) {
                food.setType("good");
                food.setValue(20);
            } else if (type == 1) {
                food.setType("bad");
                food.setValue(-5);
            } else {
                food.setType("normal");
                food.setValue(10);
            }
            
            foods.add(food);
        }
    }
    
    gameWorld.setFoods(foods);
}
    
    private void updateLeaderboard() {
        List<Player> leaderboard = new ArrayList<>();
        
        // 添加自己
        if (gameWorld.getMySnake() != null) {
            Player myPlayer = new Player();
            myPlayer.setPlayerId(gameWorld.getMySnake().getPlayerId());
            myPlayer.setNickname(gameWorld.getMySnake().getNickname());
            myPlayer.setScore(gameWorld.getMySnake().getScore());
            leaderboard.add(myPlayer);
        }
        
        // 添加其他活着的蛇
        if (gameWorld.getOtherSnakes() != null) {
            for (Snake snake : gameWorld.getOtherSnakes()) {
                if (snake.isAlive()) {
                    Player player = new Player();
                    player.setPlayerId(snake.getPlayerId());
                    player.setNickname(snake.getNickname());
                    player.setScore(snake.getScore());
                    leaderboard.add(player);
                }
            }
        }
        
        // 按分数排序
        leaderboard.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        gameWorld.setLeaderboard(leaderboard);
    }
    
    // 修改食物生成使用世界坐标
// 修改食物生成方法 - 只在需要时添加食物，不删除现有食物
private void generateFood() {
    if (gameWorld.getFoods() == null) {
        gameWorld.setFoods(new ArrayList<>());
    }
    
    List<Food> foods = gameWorld.getFoods();
    Random random = new Random();
    
    // 确保世界地图上有足够的食物（比如总共20个）
    int targetFoodCount = 20;
    int currentFoodCount = foods.size();
    
    if (currentFoodCount < targetFoodCount) {
        int foodToAdd = targetFoodCount - currentFoodCount;
        
        for (int i = 0; i < foodToAdd; i++) {
            Food food = new Food();
            Point position;
            int attempts = 0;
            
            do {
                position = new Point(
                    random.nextInt(gameWorld.getWorldMapCols()),
                    random.nextInt(gameWorld.getWorldMapRows())
                );
                attempts++;
            } while (isPositionOccupied(position) && attempts < 50);
            
            if (attempts < 50) {  // 只有找到合适位置才添加
                food.setPosition(position);
                food.setType("normal");
                food.setValue(10);
                foods.add(food);
            }
        }
    }
}

// 新增：检查视野内食物数量的方法
private void ensureFoodInViewport() {
    if (gameWorld.getFoods() == null) {
        generateFood();
        return;
    }
    
    // 计算视野内的食物数量
    long foodInView = gameWorld.getFoods().stream()
        .filter(food -> gameWorld.isInViewport(food.getPosition()))
        .count();
    
    // 如果视野内食物太少，在视野附近生成新食物
    if (foodInView < 3) {
        addFoodNearViewport();
    }
}

// 新增：在视野附近添加食物
    private void addFoodNearViewport() {
        Random random = new Random();
        List<Food> foods = gameWorld.getFoods();
        
        // 在视野扩展区域内生成食物（视野周围的更大区域）
        int expandedLeft = Math.max(0, gameWorld.getViewOffsetX() - 10);
        int expandedTop = Math.max(0, gameWorld.getViewOffsetY() - 10);
        int expandedRight = Math.min(gameWorld.getWorldMapCols(), 
                                    gameWorld.getViewOffsetX() + gameWorld.getGridCols() + 10);
        int expandedBottom = Math.min(gameWorld.getWorldMapRows(), 
                                    gameWorld.getViewOffsetY() + gameWorld.getGridRows() + 10);
        
        // 尝试添加2-3个食物
        for (int i = 0; i < 3; i++) {
            Food food = new Food();
            Point position;
            int attempts = 0;
            
            do {
                position = new Point(
                    expandedLeft + random.nextInt(expandedRight - expandedLeft),
                    expandedTop + random.nextInt(expandedBottom - expandedTop)
                );
                attempts++;
            } while (isPositionOccupied(position) && attempts < 20);
            
            if (attempts < 20) {
                food.setPosition(position);
                food.setType("normal");
                food.setValue(10);
                foods.add(food);
            }
        }
    }
    
    private boolean isPositionOccupied(Point position) {
        // 检查是否与自己的蛇重叠
        if (gameWorld.getMySnake() != null) {
            for (Point bodyPoint : gameWorld.getMySnake().getBodyPoints()) {
                if (bodyPoint.getX() == position.getX() && bodyPoint.getY() == position.getY()) {
                    return true;
                }
            }
        }
        
        // 检查是否与其他蛇重叠
        if (gameWorld.getOtherSnakes() != null) {
            for (Snake snake : gameWorld.getOtherSnakes()) {
                for (Point bodyPoint : snake.getBodyPoints()) {
                    if (bodyPoint.getX() == position.getX() && bodyPoint.getY() == position.getY()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void handlePlayerMove(String direction) {
        if (!isGameActive || gameWorld.getMySnake() == null) return;
        
        // 防止反向移动
        String currentDirection = gameWorld.getMySnake().getDirection();
        if (isOppositeDirection(currentDirection, direction)) {
            return;
        }
        
        // 更新自己蛇的方向
        gameWorld.getMySnake().setDirection(direction);
    }
    
    private boolean isOppositeDirection(String current, String newDirection) {
        return (current.equals("UP") && newDirection.equals("DOWN")) ||
               (current.equals("DOWN") && newDirection.equals("UP")) ||
               (current.equals("LEFT") && newDirection.equals("RIGHT")) ||
               (current.equals("RIGHT") && newDirection.equals("LEFT"));
    }
    
    @Override
    public void sendChatMessage(String message) {
        // TODO: 阶段3时实现网络发送
        if (view != null) {
            view.showChatMessage("我", message);
        }
    }
    
    @Override
    public void startGame() {
        isGameActive = true;
        gameWorld.setGameRunning(true);
        startGameLoop();
        
        if (view != null) {
            view.onGameStarted();
        }
    }
    
    @Override
    public void pauseGame() {
        isGameActive = false;
        stopGameLoop();
    }
    
    @Override
    public void endGame() {
        isGameActive = false;
        gameWorld.setGameRunning(false);
        stopGameLoop();
        
        if (view != null) {
            view.onGameEnded();
        }
    }
    
    private void startGameLoop() {
        gameUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isGameActive) {
                    updateGame();
                    if (view != null) {
                        view.onGameWorldUpdated(gameWorld);
                    }
                    mainHandler.postDelayed(this, gameWorld.getGameSpeed());
                }
            }
        };
        mainHandler.post(gameUpdateRunnable);
    }
    
    private void stopGameLoop() {
        if (gameUpdateRunnable != null) {
            mainHandler.removeCallbacks(gameUpdateRunnable);
        }
    }
    
    // 修改updateGame方法
    private void updateGame() {
        // 更新玩家的蛇
        if (gameWorld.getMySnake() != null && gameWorld.getMySnake().isAlive()) {
            moveSnake(gameWorld.getMySnake());
            
            // 更新视野以蛇头为中心
            Point head = getSnakeHead(gameWorld.getMySnake());
            if (head != null) {
                gameWorld.updateViewToCenter(head);
            }
            
            checkBoundaryCollision();
            checkOtherSnakeCollision(); // 检查与其他蛇的碰撞
            // 移除自碰撞检测 - checkSelfCollision();
            checkFoodCollision();
        }
        
        updateBotSnakes();
        updateLeaderboard();
        
        // 确保有足够的食物，但不要频繁重新生成
        ensureFoodInViewport();
        
        if (view != null) {
            view.onGameWorldUpdated(gameWorld);
        }
    }

    
    private void moveSnake(Snake snake) {
        List<Point> bodyPoints = snake.getBodyPoints();
        if (bodyPoints.isEmpty()) return;
        
        Point head = bodyPoints.get(0);
        Point newHead = new Point(head.getX(), head.getY());
        
        // 根据方向移动头部
        switch (snake.getDirection()) {
            case "UP":
                newHead.setY(newHead.getY() - 1);
                break;
            case "DOWN":
                newHead.setY(newHead.getY() + 1);
                break;
            case "LEFT":
                newHead.setX(newHead.getX() - 1);
                break;
            case "RIGHT":
                newHead.setX(newHead.getX() + 1);
                break;
        }
        
        // 添加新头部
        bodyPoints.add(0, newHead);
        
        // 如果没有吃到食物，移除尾部
        if (!snake.isGrowing()) {
            bodyPoints.remove(bodyPoints.size() - 1);
        } else {
            snake.setGrowing(false);
        }
    }
    
    private void updateBotSnakes() {
        if (gameWorld.getOtherSnakes() != null) {
            Random random = new Random();
            for (Snake botSnake : gameWorld.getOtherSnakes()) {
                if (botSnake.isAlive()) {
                    // 简单的随机移动AI
                    if (random.nextInt(10) == 0) { // 10%概率改变方向
                        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
                        String newDirection = directions[random.nextInt(4)];
                        if (!isOppositeDirection(botSnake.getDirection(), newDirection)) {
                            botSnake.setDirection(newDirection);
                        }
                    }
                    
                    moveSnake(botSnake);
                    
                    // 检查Bot的边界碰撞
                    Point head = getSnakeHead(botSnake);
                    if (head != null) {
                        if (head.getX() < 0 || head.getX() >= gameWorld.getGridSize() || 
                            head.getY() < 0 || head.getY() >= gameWorld.getGridSize()) {
                            botSnake.setAlive(false);
                        }
                    }
                }
            }
        }
    }
    
    private Point getSnakeHead(Snake snake) {
    if (snake != null && snake.getBodyPoints() != null && !snake.getBodyPoints().isEmpty()) {
        return snake.getBodyPoints().get(0);
    }
    return null;
    }
    
    // 在 GamePresenter.java 中添加方法
public void updateGridSize(int gridCols, int gridRows) {
    if (gameWorld != null) {
        gameWorld.setGridCols(gridCols);
        gameWorld.setGridRows(gridRows);
    }
}

// 修改边界检测
// 修改边界检测使用世界坐标
private void checkBoundaryCollision() {
    Point head = getSnakeHead(gameWorld.getMySnake());
    if (head != null) {
        int x = head.getX();
        int y = head.getY();
        
        // 使用世界地图的边界
        int worldCols = gameWorld.getWorldMapCols();
        int worldRows = gameWorld.getWorldMapRows();
        
        if (x < 0 || x >= worldCols || 
            y < 0 || y >= worldRows) {
            gameWorld.getMySnake().setAlive(false);
            endGame();
        }
    }
    }
    
    private void checkOtherSnakeCollision() {
        Point myHead = getSnakeHead(gameWorld.getMySnake());
        if (myHead == null) return;
        
        // 检查与其他蛇的碰撞
        if (gameWorld.getOtherSnakes() != null) {
            for (Snake otherSnake : gameWorld.getOtherSnakes()) {
                if (otherSnake.isAlive() && otherSnake.getBodyPoints() != null) {
                    for (Point bodyPoint : otherSnake.getBodyPoints()) {
                        if (myHead.getX() == bodyPoint.getX() && myHead.getY() == bodyPoint.getY()) {
                            gameWorld.getMySnake().setAlive(false);
                            endGame();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    // 保留原来的checkSelfCollision方法但不使用（注释掉调用）
    private void checkSelfCollision() {
        Point head = getSnakeHead(gameWorld.getMySnake());
        if (head != null) {
            List<Point> body = gameWorld.getMySnake().getBodyPoints();
            for (int i = 1; i < body.size(); i++) { // 跳过头部
                Point bodyPoint = body.get(i);
                if (head.getX() == bodyPoint.getX() && head.getY() == bodyPoint.getY()) {
                    gameWorld.getMySnake().setAlive(false);
                    endGame();
                    break;
                }
            }
        }
    }    private void checkFoodCollision() {
    Point head = getSnakeHead(gameWorld.getMySnake());
    if (head == null || gameWorld.getFoods() == null) return;
    
    List<Food> foods = gameWorld.getFoods();
    Food eatenFood = null;
    
    // 查找被吃掉的食物
    for (Food food : foods) {
        if (head.equals(food.getPosition())) {
            eatenFood = food;
            break;
        }
    }
    
    // 如果找到被吃掉的食物，处理它
    if (eatenFood != null) {
        // 移除被吃掉的食物
        foods.remove(eatenFood);
        
        // 增加分数
        Snake mySnake = gameWorld.getMySnake();
        mySnake.setScore(mySnake.getScore() + eatenFood.getValue());
        
        // 增长蛇身（添加一个新的身体节点）
        List<Point> bodyPoints = mySnake.getBodyPoints();
        if (!bodyPoints.isEmpty()) {
            Point tail = bodyPoints.get(bodyPoints.size() - 1);
            bodyPoints.add(new Point(tail.getX(), tail.getY()));
        }
        
        // 在远处随机位置生成一个新食物来替代被吃掉的食物
        addRandomFood();
    }
    }

    // 添加单个随机食物
private void addRandomFood() {
    Random random = new Random();
    Food food = new Food();
    Point position;
    int attempts = 0;
    
    do {
        position = new Point(
            random.nextInt(gameWorld.getWorldMapCols()),
            random.nextInt(gameWorld.getWorldMapRows())
        );
        attempts++;
    } while (isPositionOccupied(position) && attempts < 50);
    
    if (attempts < 50) {
        food.setPosition(position);
        food.setType("normal");
        food.setValue(10);
        gameWorld.getFoods().add(food);
    }
}
}