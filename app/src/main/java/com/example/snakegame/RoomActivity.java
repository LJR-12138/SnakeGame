package com.example.snakegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snakegame.network.NetworkMessage;
import com.example.snakegame.network.RoomServer;
import com.example.snakegame.network.RoomClient;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity implements RoomServer.RoomServerListener, RoomClient.RoomClientListener {
    
    private TextView tvRoomId, tvPlayerCount;
    private Button btnCopyRoomId, btnLeaveRoom, btnStartGame;
    private RecyclerView rvPlayers;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList;
    private SharedPreferences sharedPreferences;
    private String roomId;  // 6位房间号
    private String serverIP; // 实际的服务器IP地址
    private boolean isHost;
    private int domainId;  // 当前房间的DDS域号
    
    // 网络相关
    private RoomServer roomServer;
    private RoomClient roomClient;
    private String currentPlayerId;
    private String currentPlayerNickname;
    
    // DDS配置 - 域号范围
    // domainId 是DDS (Data Distribution Service) 的域标识符，用于隔离不同的DDS应用
    // 每个房间分配一个独立的域号，确保不同房间之间的DDS通信完全隔离
    // 域号范围：0-232，房主创建房间时随机生成，其他玩家通过网络消息获得
    private static final int MIN_DOMAIN_ID = 0;
    private static final int MAX_DOMAIN_ID = 232;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        isHost = getIntent().getBooleanExtra("isHost", false);
        roomId = getIntent().getStringExtra("roomId");
        serverIP = getIntent().getStringExtra("serverIP"); // 获取服务器IP地址
        
        // 初始化当前玩家信息
        currentPlayerId = String.valueOf(System.currentTimeMillis()); // 使用时间戳作为唯一ID
        currentPlayerNickname = sharedPreferences.getString("currentNickname", "玩家");
        
        initViews();
        setupRecyclerView();
        setupListeners();
        
        if (isHost) {
            // 创建房间（启动服务器）
            createRoom();
        } else {
            // 加入房间（连接服务器）
            joinRoom();
        }
    }
    
    private void initViews() {
        tvRoomId = findViewById(R.id.tv_room_id);
        tvPlayerCount = findViewById(R.id.tv_player_count);
        btnCopyRoomId = findViewById(R.id.btn_copy_room_id);
        btnLeaveRoom = findViewById(R.id.btn_leave_room);
        btnStartGame = findViewById(R.id.btn_start_game);
        rvPlayers = findViewById(R.id.rv_players);
    }
    
    private void setupRecyclerView() {
        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(playerList);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(playerAdapter);
    }
    
    private void setupListeners() {
        btnCopyRoomId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 复制房间信息到剪贴板（仅显示房间号给用户）
                String roomInfo = "房间号: " + roomId + "\nIP地址: " + (serverIP != null ? serverIP : "未知");
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("房间信息", roomInfo);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(RoomActivity.this, "房间信息已复制", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnLeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送离开房间消息
                if (roomClient != null) {
                    NetworkMessage leaveMessage = new NetworkMessage(
                        NetworkMessage.MessageType.PLAYER_LEAVE,
                        currentPlayerId,
                        currentPlayerNickname,
                        null
                    );
                    roomClient.sendMessage(leaveMessage);
                }
                
                cleanupNetworkConnections();
                finish();
            }
        });
        
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }
    
    private void createRoom() {
        try {
            roomServer = new RoomServer(this);
            roomServer.startServer(); // 现在这个方法已经是异步的了
            
            // 显示正在创建房间
            Toast.makeText(this, "正在创建房间...", Toast.LENGTH_SHORT).show();
            
            // 房间ID使用6位随机数字
            roomId = generateRoomId();
            // 为这个房间随机生成一个DDS域号
            domainId = generateDomainId();
            
            // 获取服务器IP地址用于网络连接
            serverIP = roomServer.getLocalIPAddress();
            
            // 添加房主到玩家列表
            Player hostPlayer = new Player(currentPlayerNickname, true);
            synchronized (playerList) {
                playerList.clear();
                playerList.add(hostPlayer);
            }
            updateUI();
            
            tvRoomId.setText(roomId);
            Toast.makeText(this, "房间创建成功！房间号: " + roomId + " (域号: " + domainId + ")", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "创建房间异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void joinRoom() {
        if (roomId == null || roomId.isEmpty()) {
            Toast.makeText(this, "房间号无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (serverIP == null || serverIP.isEmpty()) {
            Toast.makeText(this, "服务器地址无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        try {
            roomClient = new RoomClient(this);
            roomClient.connectToServer(serverIP); // 使用实际的IP地址连接
            
            tvRoomId.setText(roomId);
            Toast.makeText(this, "正在连接房间...", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "加入房间异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    // RoomServer.RoomServerListener 实现
    @Override
    public void onPlayerJoined(String playerId, String nickname) {
        runOnUiThread(() -> {
            Player newPlayer = new Player(nickname, false);
            playerList.add(newPlayer);
            updateUI();
            Toast.makeText(this, nickname + " 加入了房间", Toast.LENGTH_SHORT).show();
            
            // 向新加入的玩家发送当前玩家列表
            sendPlayerListToClient(playerId);
        });
    }
    
    @Override
    public void onPlayerLeft(String playerId) {
        runOnUiThread(() -> {
            // 从列表中移除玩家
            for (int i = 0; i < playerList.size(); i++) {
                // 这里简化处理，实际应该根据playerId匹配
                if (i > 0) { // 不移除房主
                    playerList.remove(i);
                    break;
                }
            }
            updateUI();
            Toast.makeText(this, "有玩家离开了房间", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onMessageReceived(NetworkMessage message) {
        try {
            // 处理收到的网络消息
            switch (message.getType()) {
                case PLAYER_JOIN:
                    if (!isHost) {
                        // 客户端收到其他玩家加入
                        runOnUiThread(() -> {
                            try {
                                Player newPlayer = new Player(message.getPlayerNickname(), false);
                                synchronized (playerList) {
                                    playerList.add(newPlayer);
                                }
                                updateUI();
                                Toast.makeText(this, message.getPlayerNickname() + " 加入了房间", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "处理玩家加入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case PLAYER_LEAVE:
                    runOnUiThread(() -> {
                        try {
                            // 移除离开的玩家
                            synchronized (playerList) {
                                for (int i = playerList.size() - 1; i >= 0; i--) {
                                    if (playerList.get(i).getNickname().equals(message.getPlayerNickname())) {
                                        playerList.remove(i);
                                        break;
                                    }
                                }
                            }
                            updateUI();
                            Toast.makeText(this, message.getPlayerNickname() + " 离开了房间", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "处理玩家离开失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case GAME_START:
                    runOnUiThread(() -> {
                        // 客户端收到游戏开始消息，检查是否包含域ID
                        if (message.getData() instanceof DomainIdData) {
                            DomainIdData domainData = (DomainIdData) message.getData();
                            domainId = domainData.domainId; // 保存域号
                            startMultiplayerGameWithDomainId(domainData.domainId);
                        } else {
                            // 兼容旧版本，生成一个默认域ID
                            domainId = generateDomainId();
                            startMultiplayerGameWithDomainId(domainId);
                        }
                    });
                    break;
                case PLAYER_LIST_UPDATE:
                    runOnUiThread(() -> {
                        // 更新玩家列表
                        updatePlayerListFromMessage(message);
                    });
                    break;
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "处理网络消息失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
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
            Toast.makeText(this, "连接成功！", Toast.LENGTH_SHORT).show();
            
            // 在新线程中发送加入房间消息
            new Thread(() -> {
                try {
                    NetworkMessage joinMessage = new NetworkMessage(
                        NetworkMessage.MessageType.PLAYER_JOIN,
                        currentPlayerId,
                        currentPlayerNickname,
                        null
                    );
                    
                    if (roomClient != null && roomClient.isConnected()) {
                        roomClient.sendMessage(joinMessage);
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "发送加入消息失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
            
            // 清空玩家列表，等待服务器发送完整列表
            playerList.clear();
            updateUI();
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
    
    private void sendPlayerListToClient(String targetPlayerId) {
        if (roomServer != null) {
            new Thread(() -> {
                try {
                    // 创建玩家列表数据
                    List<String> playerNames = new ArrayList<>();
                    List<Boolean> hostStatus = new ArrayList<>();
                    
                    synchronized (playerList) {
                        for (Player player : playerList) {
                            playerNames.add(player.getNickname());
                            hostStatus.add(player.isHost());
                        }
                    }
                    
                    // 创建包含玩家列表的数据
                    PlayerListData listData = new PlayerListData(playerNames, hostStatus);
                    
                    NetworkMessage listMessage = new NetworkMessage(
                        NetworkMessage.MessageType.PLAYER_LIST_UPDATE,
                        currentPlayerId,
                        currentPlayerNickname,
                        listData
                    );
                    
                    roomServer.broadcastMessage(listMessage);
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "发送玩家列表失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }
    
    private void updatePlayerListFromMessage(NetworkMessage message) {
        try {
            if (message.getData() instanceof PlayerListData) {
                PlayerListData listData = (PlayerListData) message.getData();
                
                synchronized (playerList) {
                    playerList.clear();
                    if (listData.playerNames != null && listData.hostStatus != null 
                        && listData.playerNames.size() == listData.hostStatus.size()) {
                        
                        for (int i = 0; i < listData.playerNames.size(); i++) {
                            String name = listData.playerNames.get(i);
                            boolean isHost = listData.hostStatus.get(i);
                            playerList.add(new Player(name, isHost));
                        }
                    }
                }
                updateUI();
            }
        } catch (Exception e) {
            Toast.makeText(this, "更新玩家列表失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateUI() {
        tvPlayerCount.setText("房间玩家 (" + playerList.size() + "/6)");
        playerAdapter.notifyDataSetChanged();
        
        // 只有房主显示开始游戏按钮
        btnStartGame.setVisibility(isHost ? View.VISIBLE : View.GONE);
    }
    
    private String generateRoomId() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // 为房间随机生成一个DDS域号
    private int generateDomainId() {
        Random random = new Random();
        return random.nextInt(MAX_DOMAIN_ID - MIN_DOMAIN_ID + 1) + MIN_DOMAIN_ID;
    }
    
    private void startGame() {
        if (playerList.size() < 1) {
            Toast.makeText(this, "至少需要1个玩家才能开始游戏", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isHost) {
            // 房主使用生成的域号启动游戏
            startMultiplayerGameWithDomainId(domainId);
        } else {
            // 非房主直接开始游戏（域ID由房主决定并通过网络传递）
            startMultiplayerGame();
        }
    }
    
    private void startMultiplayerGame() {
        // 启动多人游戏
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isMultiplayer", true);
        intent.putExtra("roomId", roomId);
        intent.putExtra("playerCount", playerList.size());
        intent.putExtra("isHost", isHost);
        intent.putExtra("playerId", currentPlayerId);
        intent.putExtra("playerNickname", currentPlayerNickname);
        
        // 注意：不要在这里销毁网络连接，让MainActivity处理
        startActivity(intent);
        
        // 不要调用finish()，保持RoomActivity在后台
        // finish();
    }

    private void startMultiplayerGameWithDomainId(int domainId) {
        // 启动多人游戏并传递域ID
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isMultiplayer", true);
        intent.putExtra("roomId", roomId);
        intent.putExtra("playerCount", playerList.size());
        intent.putExtra("isHost", isHost);
        intent.putExtra("playerId", currentPlayerId);
        intent.putExtra("playerNickname", currentPlayerNickname);
        intent.putExtra("domainId", domainId);  // 传递域ID给后端
        
        // 如果是房主，发送开始游戏消息，包含域ID
        if (isHost) {
            DomainIdData domainData = new DomainIdData(domainId);
            NetworkMessage startMessage = new NetworkMessage(
                NetworkMessage.MessageType.GAME_START,
                currentPlayerId,
                currentPlayerNickname,
                domainData
            );
            
            if (roomServer != null) {
                roomServer.broadcastMessage(startMessage);
            }
            
            // 房主创建游戏时记录域号信息
            Toast.makeText(this, "开始游戏 - 房间: " + roomId + ", 域号: " + domainId, Toast.LENGTH_SHORT).show();
        } else {
            // 客户端加入游戏时记录域号信息
            Toast.makeText(this, "加入游戏 - 房间: " + roomId + ", 域号: " + domainId, Toast.LENGTH_SHORT).show();
        }
        
        // 注意：不要在这里销毁网络连接，让MainActivity处理
        startActivity(intent);
        
        // 不要调用finish()，保持RoomActivity在后台
        // finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 只有在真正销毁Activity时才清理网络连接
        cleanupNetworkConnections();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 游戏开始时不要断开连接
    }
    
    private void cleanupNetworkConnections() {
        // 清理网络连接
        if (roomServer != null) {
            roomServer.stopServer();
            roomServer = null;
        }
        
        if (roomClient != null) {
            roomClient.disconnect();
            roomClient = null;
        }
    }
    
    // 内部类：玩家数据模型
    public static class Player {
        private String nickname;
        private boolean isHost;
        private String status;
        
        public Player(String nickname, boolean isHost) {
            this.nickname = nickname;
            this.isHost = isHost;
            this.status = "准备中";
        }
        
        // Getters
        public String getNickname() { return nickname; }
        public boolean isHost() { return isHost; }
        public String getStatus() { return status; }
    }
    
    // 玩家列表数据类
    public static class PlayerListData implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public List<String> playerNames;
        public List<Boolean> hostStatus;
        
        public PlayerListData() {
            // 默认构造函数
        }
        
        public PlayerListData(List<String> playerNames, List<Boolean> hostStatus) {
            this.playerNames = new ArrayList<>(playerNames);
            this.hostStatus = new ArrayList<>(hostStatus);
        }
    }

    // DDS域ID数据类
    public static class DomainIdData implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public int domainId;
        
        public DomainIdData() {
            // 默认构造函数
        }
        
        public DomainIdData(int domainId) {
            this.domainId = domainId;
        }
    }
}
