package com.example.snakegame.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.snakegame.RoomActivity;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoomServer {
    private static final String TAG = "RoomServer";
    private static final int PORT = 8888;
    
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private Map<String, ClientHandler> connectedClients;
    private RoomServerListener listener;
    private Handler mainHandler;
    
    public interface RoomServerListener {
        void onPlayerJoined(String playerId, String nickname);
        void onPlayerLeft(String playerId);
        void onMessageReceived(NetworkMessage message);
        void onServerError(String error);
    }
    
    public RoomServer(RoomServerListener listener) {
        this.listener = listener;
        this.connectedClients = new ConcurrentHashMap<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void startServer() {
        if (isRunning) {
            Log.w(TAG, "服务器已经在运行");
            return;
        }
        
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                isRunning = true;
                
                Log.d(TAG, "服务器启动成功，端口: " + PORT);
                Log.d(TAG, "房间ID (IP地址): " + getLocalIPAddress());
                
                while (isRunning && !serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        Log.d(TAG, "新客户端连接: " + clientSocket.getInetAddress());
                        
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        new Thread(clientHandler).start();
                        
                    } catch (IOException e) {
                        if (isRunning) {
                            Log.e(TAG, "接受客户端连接失败", e);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "服务器启动失败", e);
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onServerError("服务器启动失败: " + e.getMessage());
                    }
                });
            }
        }).start();
    }
    
    public void stopServer() {
        isRunning = false;
        
        // 断开所有客户端
        for (ClientHandler client : connectedClients.values()) {
            client.disconnect();
        }
        connectedClients.clear();
        
        // 关闭服务器
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                Log.d(TAG, "服务器已关闭");
            } catch (IOException e) {
                Log.e(TAG, "关闭服务器失败", e);
            }
        }
    }
    
    public void broadcastMessage(NetworkMessage message) {
        for (ClientHandler client : connectedClients.values()) {
            client.sendMessage(message);
        }
    }
    
    public String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "获取IP地址失败", ex);
        }
        return "未知";
    }
    
    private void notifyMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }
    
    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private String playerId;
        private String playerNickname;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                
                while (isRunning && !socket.isClosed()) {
                    try {
                        NetworkMessage message = (NetworkMessage) inputStream.readObject();
                        handleMessage(message);
                    } catch (ClassNotFoundException | IOException e) {
                        Log.e(TAG, "读取消息失败", e);
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "客户端处理失败", e);
            } finally {
                disconnect();
            }
        }
        
        private void handleMessage(NetworkMessage message) {
            switch (message.getType()) {
                case PLAYER_JOIN:
                    playerId = message.getPlayerId();
                    playerNickname = message.getPlayerNickname();
                    connectedClients.put(playerId, this);
                    
                    notifyMainThread(() -> {
                        if (listener != null) {
                            listener.onPlayerJoined(playerId, playerNickname);
                        }
                    });
                    break;
                    
                case PLAYER_LEAVE:
                    notifyMainThread(() -> {
                        if (listener != null) {
                            listener.onPlayerLeft(playerId);
                        }
                    });
                    // 广播玩家离开消息给其他客户端
                    broadcastToOthers(message);
                    disconnect();
                    break;
                    
                case PLAYER_MOVE:
                case GAME_UPDATE:
                    // 转发给所有客户端
                    broadcastMessage(message);
                    break;
                    
                case PING:
                    // 响应心跳
                    sendMessage(new NetworkMessage(NetworkMessage.MessageType.PONG, "server", "server", null));
                    break;
            }
            
            notifyMainThread(() -> {
                if (listener != null) {
                    listener.onMessageReceived(message);
                }
            });
        }
        
        public void sendMessage(NetworkMessage message) {
            try {
                if (outputStream != null) {
                    outputStream.writeObject(message);
                    outputStream.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "发送消息失败", e);
            }
        }
        
        private void broadcastToOthers(NetworkMessage message) {
            for (Map.Entry<String, ClientHandler> entry : connectedClients.entrySet()) {
                if (!entry.getKey().equals(playerId)) {
                    entry.getValue().sendMessage(message);
                }
            }
        }
        
        public void disconnect() {
            if (playerId != null) {
                connectedClients.remove(playerId);
                
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onPlayerLeft(playerId);
                    }
                });
                
                // 通知其他客户端该玩家离开
                NetworkMessage leaveMessage = new NetworkMessage(
                    NetworkMessage.MessageType.PLAYER_LEAVE, 
                    playerId, 
                    playerNickname, 
                    null
                );
                broadcastToOthers(leaveMessage);
            }
            
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭连接失败", e);
            }
        }
    }
}
