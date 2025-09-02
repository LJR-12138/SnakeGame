package com.example.snakegame;

import android.os.Bundle;
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
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements GameContract.View {
    
    private GamePresenter presenter;
    private GameSurfaceView gameSurfaceView;
    private TextView tvScore;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 获取登录传入的参数
        playerNickname = getIntent().getStringExtra("nickname");
        playerColor = getIntent().getStringExtra("color");
        if (playerNickname == null) playerNickname = "Player1";
        if (playerColor == null) playerColor = "#FF0000";
        
        initViews();
        initPresenter();
        setupGestureDetector();
        setupDirectionControls();
        
        // 显示开始游戏提示
        showStartGamePrompt();
    }
    
    private void initViews() {
        gameSurfaceView = findViewById(R.id.game_surface_view);
        tvScore = findViewById(R.id.tv_score);
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
        tvGameStatus.setText("点击屏幕开始游戏");
        tvGameStatus.setVisibility(View.VISIBLE);
        tvScore.setText("Score: 0");
        tvLeaderboard.setText("等待开始...");
        gameStarted = false;
    }
    
    private void startGame() {
        gameStarted = true;
        
        // 隐藏状态提示
        tvGameStatus.setVisibility(View.GONE);
        
        // 初始化并开始游戏
        presenter.initializeGame(12345L, playerNickname, playerColor);
        presenter.startGame();
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
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }
}