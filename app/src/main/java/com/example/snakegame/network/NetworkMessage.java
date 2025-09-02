package com.example.snakegame.network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    
    public enum MessageType {
        PLAYER_JOIN,        // 玩家加入房间
        PLAYER_LEAVE,       // 玩家离开房间
        PLAYER_MOVE,        // 玩家移动
        GAME_START,         // 游戏开始
        GAME_UPDATE,        // 游戏状态更新
        PLAYER_LIST_UPDATE, // 玩家列表更新
        PING,               // 心跳检测
        PONG                // 心跳响应
    }
    
    private MessageType type;
    private String playerId;
    private String playerNickname;
    private Object data;
    private long timestamp;
    
    public NetworkMessage(MessageType type, String playerId, String playerNickname, Object data) {
        this.type = type;
        this.playerId = playerId;
        this.playerNickname = playerNickname;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getPlayerNickname() { return playerNickname; }
    public void setPlayerNickname(String playerNickname) { this.playerNickname = playerNickname; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
