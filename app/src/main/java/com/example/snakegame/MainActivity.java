package com.example.snakegame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.snakegame.data.model.GameWorld;
import com.example.snakegame.data.model.Player;
import com.example.snakegame.presentation.contract.GameContract;
import com.example.snakegame.presentation.presenter.GamePresenter;
import com.example.snakegame.ui.view.GameSurfaceView;
import com.example.snakegame.network.RoomServer;
import com.example.snakegame.network.RoomClient;
import com.example.snakegame.network.NetworkMessage;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements GameContract.View, RoomServer.RoomServerListener, RoomClient.RoomClientListener {
    
    private GamePresenter presenter;
    private GameSurfaceView gameSurfaceView;
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvLeaderboard;
    private TextView tvGameStatus;
    private LinearLayout controlPanel;
    private View loadingView;
    
    // 方向控制按钮
    private Button btnUp, btnDown, btnLeft, btnRight;
    private View directionControls;
    
    private String playerNickname;
    private String playerColor;
    private GestureDetector gestureDetector;
    private boolean gameStarted = false;
    
    // 多人模式相关
    private boolean isMultiplayer = false;
    private boolean isHost = false;
    private String roomId;
    private String playerId;
    private int playerCount;
    private RoomServer roomServer;
    private RoomClient roomClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 获取登录传入的参数
        playerNickname = getIntent().getStringExtra("nickname");
        playerColor = getIntent().getStringExtra("color");
        
        // 获取多人模式参数
        isMultiplayer = getIntent().getBooleanExtra("isMultiplayer", false);
        roomId = getIntent().getStringExtra("roomId");
        playerCount = getIntent().getIntExtra("playerCount", 1);
        isHost = getIntent().getBooleanExtra("isHost", false);
        playerId = getIntent().getStringExtra("playerId");
        int domainId = getIntent().getIntExtra("domainId", 42); // 获取域ID，默认为42
        
        // 如果是多人模式但没有传入playerNickname，从Intent获取
        if (isMultiplayer && playerNickname == null) {
            playerNickname = getIntent().getStringExtra("playerNickname");
        }
        
        if (playerNickname == null) playerNickname = "Player1";
        if (playerColor == null) playerColor = "#FF0000";
        
        initViews();
        initPresenter();
        setupGestureDetector();
        setupDirectionControls();
        
        if (isMultiplayer) {
            // 多人模式：恢复网络连接
            restoreNetworkConnection();
            Toast.makeText(this, "多人游戏模式 - 房间: " + roomId + " (域ID: " + domainId + ")", Toast.LENGTH_LONG).show();
            
            // 如果是房主且连接成功，延迟启动游戏
            if (isHost && isMultiplayer) {
                showMultiplayerGamePrompt(domainId);
                // 延迟3秒后自动开始游戏
                new Handler().postDelayed(() -> {
                    if (!gameStarted) {
                        startGame();
                    }
                }, 3000);
            } else if (isMultiplayer) {
                showMultiplayerGamePrompt(domainId);
                // 客户端等待连接成功后自动开始
            }
            // 如果isMultiplayer被设为false，说明连接失败，已经在restoreNetworkConnection中处理了
        } else {
            // 单人模式：显示开始游戏提示
            showStartGamePrompt();
        }
    }
    
    private void initViews() {
        gameSurfaceView = findViewById(R.id.game_surface_view);
        tvScore = findViewById(R.id.tv_score);
        tvTime = findViewById(R.id.tv_time);
        tvLeaderboard = findViewById(R.id.tv_leaderboard);
        tvGameStatus = findViewById(R.id.tv_game_status);
        controlPanel = findViewById(R.id.control_panel);
        loadingView = findViewById(R.id.loading_view);
        
        // 方向控制按钮
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        directionControls = findViewById(R.id.direction_controls);
        
        // 始终显示时间（定时积分赛模式）
        tvTime.setVisibility(View.VISIBLE);
    }
    
    private void initPresenter() {
        presenter = new GamePresenter();
        presenter.attachView(this);
        
        // 删除这行代码，因为我们不再使用滑动控制
        // gameSurfaceView.setOnDirectionChangeListener(direction -> {
        //     if (gameStarted) {
        //         presenter.handlePlayerMove(direction);
        //     }
        // });
    }
    
    private void setupDirectionControls() {
        // 方向控制始终显示
        directionControls.setVisibility(View.VISIBLE);
        
        // 设置方向按钮点击事件
        btnUp.setOnClickListener(v -> {
            if (gameStarted) {
                presenter.handlePlayerMove("UP");
            }
        });
        
        btnDown.setOnClickListener(v -> {
            if (gameStarted) {
                presenter.handlePlayerMove("DOWN");
            }
        });
        
        btnLeft.setOnClickListener(v -> {
            if (gameStarted) {
                presenter.handlePlayerMove("LEFT");
            }
        });
        
        btnRight.setOnClickListener(v -> {
            if (gameStarted) {
                presenter.handlePlayerMove("RIGHT");
            }
        });
    }
    
    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // 点击屏幕开始游戏
                if (!gameStarted) {
                    startGame();
                    return true;
                }
                return false;
            }
            
            // 删除onFling方法，因为我们不再使用滑动控制
            // @Override
            // public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //     // 不再处理滑动事件
            //     return false;
            // }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
    
    private void showStartGamePrompt() {
        tvGameStatus.setText("定时积分赛 - 5分钟挑战\n点击屏幕开始游戏");
        tvGameStatus.setVisibility(View.VISIBLE);
        tvScore.setText("Score: 0");
        tvLeaderboard.setText("等待开始...");
        gameStarted = false;
    }
    
    private void showMultiplayerGamePrompt(int domainId) {
        String roleText = isHost ? "房主" : "玩家";
        tvGameStatus.setText("多人定时积分赛 - 5分钟挑战\n" + roleText + " - 房间: " + roomId + "\nDDS域ID: " + domainId + "\n游戏即将开始...");
        tvGameStatus.setVisibility(View.VISIBLE);
        tvScore.setText("Score: 0");
        tvLeaderboard.setText("等待开始...");
        gameStarted = false;
    }
    
    private void startGame() {
        gameStarted = true;
        
        // 隐藏状态提示
        tvGameStatus.setVisibility(View.GONE);
        
        try {
            // 根据模式启动游戏
            if (isMultiplayer && playerId != null && !playerId.isEmpty()) {
                // 多人模式：使用传入的playerId
                long playerIdLong = Long.parseLong(playerId);
                presenter.initializeTimedScoreMode(playerIdLong, playerNickname, playerColor);
            } else {
                // 单人模式：使用默认ID
                presenter.initializeTimedScoreMode(12345L, playerNickname, playerColor);
            }
            
            presenter.startGame();
        } catch (Exception e) {
            Toast.makeText(this, "游戏启动失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            gameStarted = false;
            showStartGamePrompt();
        }
    }
    
    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }
    
    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onGameWorldUpdated(GameWorld gameWorld) {
        gameSurfaceView.updateGameWorld(gameWorld);

        // 更新实际的网格大小
    if (presenter != null) {
        presenter.updateGridSize(
            gameSurfaceView.getGridCols(), 
            gameSurfaceView.getGridRows()
        );
    }
        
        // 更新分数显示
        if (gameWorld.getMySnake() != null) {
            tvScore.setText("Score: " + gameWorld.getMySnake().getScore());
        }
        
        // 更新排行榜
        if (gameWorld.getLeaderboard() != null) {
            updateLeaderboard(gameWorld.getLeaderboard());
        }
    }
    
    @Override
    public void onGameStarted() {
        tvGameStatus.setVisibility(View.GONE);
    }
    
    @Override
    public void onGameEnded() {
        gameStarted = false;
        tvGameStatus.setText("游戏结束！点击重新开始");
        tvGameStatus.setVisibility(View.VISIBLE);
        Toast.makeText(this, "游戏结束！", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void showChatMessage(String playerName, String message) {
        Toast.makeText(this, playerName + ": " + message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void updateLeaderboard(List<Player> leaderboard) {
        if (leaderboard != null && !leaderboard.isEmpty()) {
            // 过滤掉重复的玩家和无效数据
            Set<String> addedPlayers = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            int rank = 1;
            
            for (Player player : leaderboard) {
                // 检查玩家是否有效且未重复
                if (player != null && 
                    player.getNickname() != null && 
                    !player.getNickname().isEmpty() &&
                    !addedPlayers.contains(player.getNickname()) &&
                    rank <= 3) {
                    
                    sb.append(rank).append(". ")
                      .append(player.getNickname())
                      .append(" (").append(player.getScore()).append(")");
                    
                    if (rank < 3 && rank < leaderboard.size()) {
                        sb.append("\n");
                    }
                    
                    addedPlayers.add(player.getNickname());
                    rank++;
                }
            }
            
            // 如果没有有效的排行榜数据，显示等待信息
            if (sb.length() == 0) {
                tvLeaderboard.setText("等待玩家...");
            } else {
                tvLeaderboard.setText(sb.toString());
            }
        } else {
            tvLeaderboard.setText("等待玩家...");
        }
    }

    @Override
    public void onTimeUpdate(int remainingTimeSeconds) {
        runOnUiThread(() -> {
            int minutes = remainingTimeSeconds / 60;
            int seconds = remainingTimeSeconds % 60;
            tvTime.setText(String.format("时间: %02d:%02d", minutes, seconds));
        });
    }

    @Override
    public void onTimedGameEnded(String winnerMessage) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("定时积分赛结束")
                    .setMessage(winnerMessage)
                    .setPositiveButton("再来一局", (dialog, which) -> {
                        presenter.restartGame();
                    })
                    .setNegativeButton("返回主页", (dialog, which) -> {
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    @Override
    public void onPlayerDiedInTimedMode(String playerName, int finalScore) {
        runOnUiThread(() -> {
            tvGameStatus.setText(playerName + " 死亡，得分固定为 " + finalScore + "，已转化为食物");
            tvGameStatus.setVisibility(View.VISIBLE);
            
            // 3秒后隐藏状态信息
            new Handler().postDelayed(() -> {
                tvGameStatus.setVisibility(View.GONE);
            }, 3000);
        });
    }
    
    // 多人模式网络连接相关方法
    private void restoreNetworkConnection() {
        if (isHost) {
            // 恢复房主的服务器连接
            restoreServerConnection();
        } else {
            // 恢复客户端连接
            restoreClientConnection();
        }
        
        // 检查连接是否成功
        if (!isMultiplayer) {
            Toast.makeText(this, "网络连接失败，转为单人模式", Toast.LENGTH_LONG).show();
            showStartGamePrompt();
        }
    }
    
    private void restoreServerConnection() {
        try {
            // 如果已有服务器，先完全停止它
            if (roomServer != null) {
                roomServer.stopServer();
                roomServer = null;
                // 等待一段时间确保端口释放
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            roomServer = new RoomServer(this);
            roomServer.startServer();
            Toast.makeText(this, "房主模式：服务器已恢复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "恢复服务器连接失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // 不再重新抛出异常，而是设置标志位
            isMultiplayer = false;
        }
    }
    
    private void restoreClientConnection() {
        try {
            if (roomClient != null) {
                roomClient.disconnect();
            }
            roomClient = new RoomClient(this);
            if (roomId != null && !roomId.isEmpty()) {
                roomClient.connectToServer(roomId);
                Toast.makeText(this, "客户端模式：正在重新连接...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "房间ID为空", Toast.LENGTH_LONG).show();
                isMultiplayer = false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "恢复客户端连接失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // 不再重新抛出异常，而是设置标志位
            isMultiplayer = false;
        }
    }
    
    // RoomServer.RoomServerListener 实现
    @Override
    public void onPlayerJoined(String playerId, String nickname) {
        runOnUiThread(() -> {
            Toast.makeText(this, nickname + " 重新加入了游戏", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onPlayerLeft(String playerId) {
        runOnUiThread(() -> {
            Toast.makeText(this, "有玩家离开了游戏", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onMessageReceived(NetworkMessage message) {
        // 处理网络消息，例如玩家移动、游戏同步等
        switch (message.getType()) {
            case PLAYER_MOVE:
                // 处理其他玩家的移动
                break;
            case GAME_UPDATE:
                // 处理游戏状态同步
                break;
            default:
                break;
        }
    }
    
    @Override
    public void onServerError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "服务器错误: " + error, Toast.LENGTH_LONG).show();
        });
    }
    
    // RoomClient.RoomClientListener 实现
    @Override
    public void onConnected() {
        runOnUiThread(() -> {
            Toast.makeText(this, "重新连接成功！", Toast.LENGTH_SHORT).show();
            // 自动开始游戏
            startGame();
        });
    }
    
    @Override
    public void onDisconnected() {
        runOnUiThread(() -> {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onConnectionError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "连接错误: " + error, Toast.LENGTH_LONG).show();
        });
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 暂停游戏
        if (presenter != null) {
            presenter.pauseGame();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 如果游戏已经开始，恢复游戏
        if (gameStarted && presenter != null) {
            presenter.startGame();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 清理网络连接
        if (roomServer != null) {
            roomServer.stopServer();
            roomServer = null;
        }
        
        if (roomClient != null) {
            roomClient.disconnect();
            roomClient = null;
        }
        
        if (presenter != null) {
            presenter.detachView();
        }
        
        // 等待一点时间确保资源完全释放
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}