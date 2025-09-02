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
    
    // 定时积分赛相关
    private boolean isTimedScoreMode = true; // 默认启用定时积分赛
    private long gameStartTime;
    private static final long GAME_DURATION_MS = 5 * 60 * 1000; // 5分钟
    private Runnable timeUpdateRunnable;
    private List<DeadPlayerFood> deadPlayerFoods = new ArrayList<>();
    
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
        this.isTimedScoreMode = true; // 强制启用定时积分赛
        
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
    
    // 新增：初始化定时积分赛模式
    @Override
    public void initializeTimedScoreMode(long playerId, String nickname, String color) {
        this.isTimedScoreMode = true;
        this.gameStartTime = System.currentTimeMillis();
        initializeGame(playerId, nickname, color);
    }
    
    private void generateMockData() {
        gameWorld = new GameWorld();
        gameWorld.setGridSize(20);
        gameWorld.setGameSpeed(100); // 设置为100ms，使移动更流畅
        
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
            
            // 随机生成不同类型的食物，使用新的枚举类型
            int typeRoll = random.nextInt(100);
            if (typeRoll < 10) {
                // 10% 概率生成好食物（星星）
                food.setType(Food.FoodType.GOOD_FOOD);
            } else if (typeRoll < 20) {
                // 10% 概率生成坏食物（骷髅头）
                food.setType(Food.FoodType.BAD_FOOD);
            } else {
                // 80% 概率生成普通食物（苹果）
                food.setType(Food.FoodType.APPLE);
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
                
                // 随机生成不同类型的食物
                Random foodTypeRandom = new Random();
                int typeRoll = foodTypeRandom.nextInt(100);
                if (typeRoll < 5) {
                    food.setType(Food.FoodType.GOOD_FOOD);
                } else if (typeRoll < 15) {
                    food.setType(Food.FoodType.BAD_FOOD);
                } else {
                    food.setType(Food.FoodType.APPLE);
                }
                
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
                
                // 随机生成不同类型的食物
                int typeRoll = random.nextInt(100);
                if (typeRoll < 5) {
                    food.setType(Food.FoodType.GOOD_FOOD);
                } else if (typeRoll < 15) {
                    food.setType(Food.FoodType.BAD_FOOD);
                } else {
                    food.setType(Food.FoodType.APPLE);
                }
                
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
        // 只有活着的玩家才能控制移动
        if (!isGameActive || gameWorld.getMySnake() == null || !gameWorld.getMySnake().isAlive()) return;
        
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
        
        if (isTimedScoreMode) {
            gameStartTime = System.currentTimeMillis();
            startTimeCountdown();
        }
        
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
    
    @Override
    public void restartGame() {
        // 停止当前游戏
        endGame();
        
        // 重新初始化游戏状态
        isGameActive = false;
        gameWorld = new GameWorld();
        gameWorld.setWorldMapCols(100);
        gameWorld.setWorldMapRows(100);
        gameWorld.setGameSpeed(150); // 保持一致的游戏速度
        
        // 重新设置定时模式（总是启用）
        initializeTimedScoreMode(Long.parseLong(playerId), playerNickname, playerColor);
        
        // 启动新游戏
        startGame();
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
        if (timeUpdateRunnable != null) {
            mainHandler.removeCallbacks(timeUpdateRunnable);
        }
    }
    
    private void startTimeCountdown() {
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - gameStartTime;
                long remaining = GAME_DURATION_MS - elapsed;
                
                if (remaining <= 0) {
                    // 游戏时间结束
                    endTimedGame();
                } else {
                    // 更新剩余时间显示
                    if (view != null) {
                        int remainingTimeSeconds = (int) (remaining / 1000);
                        view.onTimeUpdate(remainingTimeSeconds);
                    }
                    
                    // 每秒更新一次
                    mainHandler.postDelayed(this, 1000);
                }
            }
        };
        mainHandler.post(timeUpdateRunnable);
    }
    
    private void endTimedGame() {
        isGameActive = false;
        gameWorld.setGameRunning(false);
        stopGameLoop();
        
        if (view != null) {
            // 计算获胜者信息
            String winnerMessage = "游戏结束！";
            Snake mySnake = gameWorld.getMySnake();
            if (mySnake != null) {
                winnerMessage = "游戏结束！最终得分：" + mySnake.getScore();
            }
            view.onTimedGameEnded(winnerMessage);
        }
    }
    
    private void handlePlayerDeathInTimedMode() {
        Snake mySnake = gameWorld.getMySnake();
        if (mySnake != null && mySnake.isAlive()) {
            Point headPosition = getSnakeHead(mySnake);
            if (headPosition != null) {
                // 创建死亡玩家食物
                DeadPlayerFood deadFood = new DeadPlayerFood(
                    new Point(headPosition.getX(), headPosition.getY()),
                    mySnake.getScore(),
                    playerNickname,
                    playerColor
                );
                deadPlayerFoods.add(deadFood);
                
                // 将玩家标记为死亡，分数固定，进入旁观模式
                mySnake.setAlive(false);
                
                if (view != null) {
                    view.onPlayerDiedInTimedMode(playerNickname, mySnake.getScore());
                }
                
                // 不再重生，玩家进入旁观模式
                // 可以将视野切换到排行榜第一名或保持当前视野
                switchToSpectatorMode();
            }
        }
    }
    
    private void switchToSpectatorMode() {
        // 进入旁观模式，可以将视野切换到排行榜第一名的蛇
        if (gameWorld.getLeaderboard() != null && !gameWorld.getLeaderboard().isEmpty()) {
            // 找到排行榜第一名对应的活着的蛇
            Player topPlayer = gameWorld.getLeaderboard().get(0);
            Snake targetSnake = null;
            
            // 在其他蛇中寻找排行榜第一名
            if (gameWorld.getOtherSnakes() != null) {
                for (Snake snake : gameWorld.getOtherSnakes()) {
                    if (snake.isAlive() && snake.getPlayerId().equals(topPlayer.getPlayerId())) {
                        targetSnake = snake;
                        break;
                    }
                }
            }
            
            // 如果找到目标蛇，将视野切换到它的位置
            if (targetSnake != null) {
                Point targetHead = getSnakeHead(targetSnake);
                if (targetHead != null) {
                    gameWorld.updateViewToCenter(targetHead);
                }
            }
        }
        
        if (view != null) {
            // 注意：需要在GameContract.View接口中添加onEnterSpectatorMode()方法
            // view.onEnterSpectatorMode();
        }
    }
    
    // 修改updateGame方法
    private void updateGame() {
        // 只有活着的玩家才更新蛇的移动和碰撞检测
        if (gameWorld.getMySnake() != null && gameWorld.getMySnake().isAlive()) {
            moveSnake(gameWorld.getMySnake());
            
            // 更新视野以蛇头为中心
            Point head = getSnakeHead(gameWorld.getMySnake());
            if (head != null) {
                gameWorld.updateViewToCenter(head);
            }
            
            checkBoundaryCollision();
            checkOtherSnakeCollision(); // 检查与其他蛇的碰撞
            checkFoodCollision();
        } else if (gameWorld.getMySnake() != null && !gameWorld.getMySnake().isAlive()) {
            // 玩家已死亡，进入旁观模式
            // 可以跟随排行榜第一名的视野，或者保持当前视野让玩家自由观看
            updateSpectatorView();
        }
        
        updateBotSnakes();
        updateLeaderboard();
        
        // 确保有足够的食物，但不要频繁重新生成
        ensureFoodInViewport();
        
        if (view != null) {
            view.onGameWorldUpdated(gameWorld);
        }
    }

    private void updateSpectatorView() {
        // 在旁观模式下，可以跟随排行榜第一名的蛇
        if (gameWorld.getLeaderboard() != null && !gameWorld.getLeaderboard().isEmpty()) {
            Player topPlayer = gameWorld.getLeaderboard().get(0);
            Snake targetSnake = null;
            
            // 寻找排行榜第一名对应的活着的蛇
            if (gameWorld.getOtherSnakes() != null) {
                for (Snake snake : gameWorld.getOtherSnakes()) {
                    if (snake.isAlive() && snake.getPlayerId().equals(topPlayer.getPlayerId())) {
                        targetSnake = snake;
                        break;
                    }
                }
            }
            
            // 如果找到目标蛇，更新视野跟随它
            if (targetSnake != null) {
                Point targetHead = getSnakeHead(targetSnake);
                if (targetHead != null) {
                    gameWorld.updateViewToCenter(targetHead);
                }
            }
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
        
        // 如果没有待增长的节数，移除尾部
        if (!snake.isGrowing()) {
            bodyPoints.remove(bodyPoints.size() - 1);
        } else {
            // 减少一个待增长节数
            if (snake.getGrowthPending() > 0) {
                snake.decreaseGrowthPending();
            } else {
                snake.setGrowing(false);
            }
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
            
            if (isTimedScoreMode) {
                handlePlayerDeathInTimedMode();
            } else {
                gameWorld.getMySnake().setAlive(false);
                endGame();
            }
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
                            
                            if (isTimedScoreMode) {
                                handlePlayerDeathInTimedMode();
                            } else {
                                gameWorld.getMySnake().setAlive(false);
                                endGame();
                            }
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
    }
    
    private void checkFoodCollision() {
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
            
            Snake mySnake = gameWorld.getMySnake();
            
            // 根据食物类型产生不同效果
            switch (eatenFood.getType()) {
                case APPLE:
                    // 普通苹果：+10分，长度+1
                    mySnake.setScore(mySnake.getScore() + eatenFood.getValue());
                    mySnake.grow();
                    break;
                    
                case GOOD_FOOD:
                    // 星星：+20分，长度+2
                    mySnake.setScore(mySnake.getScore() + eatenFood.getValue());
                    mySnake.growByAmount(2);
                    break;
                    
                case BAD_FOOD:
                    // 骷髅头：-5分，长度-1
                    mySnake.setScore(Math.max(0, mySnake.getScore() + eatenFood.getValue()));
                    mySnake.shrink();
                    break;
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
        
        // 随机生成不同类型的食物
        int typeRoll = random.nextInt(100);
        if (typeRoll < 5) {
            food.setType(Food.FoodType.GOOD_FOOD);
        } else if (typeRoll < 15) {
            food.setType(Food.FoodType.BAD_FOOD);
        } else {
            food.setType(Food.FoodType.APPLE);
        }
        
        gameWorld.getFoods().add(food);
    }
}

// 死亡玩家食物类
class DeadPlayerFood {
    private Point position;
    private int score;
    private String playerName;
    private String playerColor;
    
    public DeadPlayerFood(Point position, int score, String playerName, String playerColor) {
        this.position = position;
        this.score = score;
        this.playerName = playerName;
        this.playerColor = playerColor;
    }
    
    // Getters
    public Point getPosition() { return position; }
    public int getScore() { return score; }
    public String getPlayerName() { return playerName; }
    public String getPlayerColor() { return playerColor; }
}
}