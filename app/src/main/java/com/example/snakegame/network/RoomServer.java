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
    private DatagramSocket broadcastSocket; // UDP广播监听socket
    private boolean isRunning = false;
    private Map<String, ClientHandler> connectedClients;
    private RoomServerListener listener;
    private Handler mainHandler;
    private String currentRoomId; // 当前房间号
    
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
    
    // 设置房间号
    public void setRoomId(String roomId) {
        this.currentRoomId = roomId;
    }
    
    public void startServer() {
        if (isRunning) {
            Log.w(TAG, "服务器已经在运行");
            return;
        }
        
        // 先停止之前的服务器（如果有的话）
        stopServer();
        
        new Thread(() -> {
            ServerSocket tempServerSocket = null;
            int actualPort = PORT;
            
            // 尝试多个端口，直到找到可用的
            for (int port = PORT; port < PORT + 10; port++) {
                try {
                    tempServerSocket = new ServerSocket(port);
                    actualPort = port;
                    break;
                } catch (IOException e) {
                    Log.w(TAG, "端口 " + port + " 被占用，尝试下一个端口");
                    if (tempServerSocket != null) {
                        try {
                            tempServerSocket.close();
                        } catch (IOException ex) {
                            // 忽略关闭异常
                        }
                    }
                }
            }
            
            if (tempServerSocket == null) {
                Log.e(TAG, "无法找到可用端口");
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onServerError("无法找到可用端口");
                    }
                });
                return;
            }
            
            try {
                serverSocket = tempServerSocket;
                isRunning = true;
                
                Log.d(TAG, "服务器启动成功，端口: " + actualPort);
                Log.d(TAG, "房间ID: " + currentRoomId);
                
                // 启动UDP广播监听
                startBroadcastListener();
                
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
            } catch (Exception e) {
                Log.e(TAG, "服务器运行失败", e);
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onServerError("服务器运行失败: " + e.getMessage());
                    }
                });
            }
        }).start();
    }
    
    public void stopServer() {
        Log.d(TAG, "正在停止服务器...");
        isRunning = false;
        
        // 断开所有客户端
        for (ClientHandler client : connectedClients.values()) {
            try {
                client.disconnect();
            } catch (Exception e) {
                Log.w(TAG, "断开客户端连接时出错", e);
            }
        }
        connectedClients.clear();
        
        // 关闭UDP广播监听
        if (broadcastSocket != null && !broadcastSocket.isClosed()) {
            broadcastSocket.close();
            broadcastSocket = null;
        }
        
        // 关闭服务器
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                Log.d(TAG, "服务器已关闭");
            } catch (IOException e) {
                Log.e(TAG, "关闭服务器失败", e);
            } finally {
                serverSocket = null;
            }
        }
        
        // 给一点时间让端口释放
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
    
    // 启动UDP广播监听
    private void startBroadcastListener() {
        new Thread(() -> {
            try {
                broadcastSocket = new DatagramSocket(8889);
                Log.d(TAG, "UDP广播监听启动，端口: 8889，房间号: " + currentRoomId);
                
                byte[] buffer = new byte[1024];
                
                while (isRunning && !broadcastSocket.isClosed()) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        broadcastSocket.receive(packet);
                        
                        String message = new String(packet.getData(), 0, packet.getLength());
                        Log.d(TAG, "收到广播搜索: " + message);
                        
                        if (message.startsWith("SEARCH_ROOM:")) {
                            String searchRoomId = message.substring("SEARCH_ROOM:".length());
                            Log.d(TAG, "搜索房间号: " + searchRoomId + ", 当前房间号: " + currentRoomId);
                            
                            // 检查是否匹配当前房间号
                            if (currentRoomId != null && currentRoomId.equals(searchRoomId)) {
                                // 响应搜索请求
                                String response = "ROOM_FOUND:" + currentRoomId;
                                byte[] responseData = response.getBytes();
                                
                                DatagramPacket responsePacket = new DatagramPacket(
                                    responseData, 
                                    responseData.length, 
                                    packet.getAddress(), 
                                    packet.getPort()
                                );
                                
                                broadcastSocket.send(responsePacket);
                                Log.d(TAG, "响应房间搜索: " + searchRoomId + " 到 " + packet.getAddress().getHostAddress());
                            } else {
                                Log.d(TAG, "房间号不匹配，不响应搜索");
                            }
                        }
                        
                    } catch (IOException e) {
                        if (isRunning) {
                            Log.e(TAG, "UDP广播监听失败", e);
                        }
                    }
                }
                
            } catch (SocketException e) {
                Log.e(TAG, "启动UDP广播监听失败", e);
            } finally {
                if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                    broadcastSocket.close();
                }
                Log.d(TAG, "UDP广播监听已停止");
            }
        }).start();
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
