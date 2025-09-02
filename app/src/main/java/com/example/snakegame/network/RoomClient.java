package com.example.snakegame.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.*;
import java.net.*;

public class RoomClient {
    private static final String TAG = "RoomClient";
    private static final int PORT = 8888;
    
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean isConnected = false;
    private RoomClientListener listener;
    private Handler mainHandler;
    private String serverIP;
    
    public interface RoomClientListener {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(NetworkMessage message);
        void onConnectionError(String error);
    }
    
    public RoomClient(RoomClientListener listener) {
        this.listener = listener;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void connectToServer(String serverIP) {
        this.serverIP = serverIP;
        
        new Thread(() -> {
            try {
                Log.d(TAG, "尝试连接服务器: " + serverIP + ":" + PORT);
                socket = new Socket(serverIP, PORT);
                
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                
                isConnected = true;
                Log.d(TAG, "连接服务器成功");
                
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onConnected();
                    }
                });
                
                // 开始监听消息
                listenForMessages();
                
            } catch (IOException e) {
                Log.e(TAG, "连接服务器失败", e);
                notifyMainThread(() -> {
                    if (listener != null) {
                        listener.onConnectionError("连接失败: " + e.getMessage());
                    }
                });
            }
        }).start();
    }
    
    private void listenForMessages() {
        try {
            while (isConnected && !socket.isClosed()) {
                try {
                    NetworkMessage message = (NetworkMessage) inputStream.readObject();
                    
                    Log.d(TAG, "收到消息: " + message.getType());
                    
                    notifyMainThread(() -> {
                        if (listener != null) {
                            listener.onMessageReceived(message);
                        }
                    });
                    
                } catch (ClassNotFoundException | IOException e) {
                    if (isConnected) {
                        Log.e(TAG, "读取消息失败", e);
                        break;
                    }
                }
            }
        } finally {
            disconnect();
        }
    }
    
    public void sendMessage(NetworkMessage message) {
        if (isConnected && outputStream != null) {
            new Thread(() -> {
                try {
                    outputStream.writeObject(message);
                    outputStream.flush();
                } catch (IOException e) {
                    Log.e(TAG, "发送消息失败", e);
                    notifyMainThread(() -> {
                        if (listener != null) {
                            listener.onConnectionError("发送消息失败: " + e.getMessage());
                        }
                    });
                }
            }).start();
        }
    }
    
    public void disconnect() {
        isConnected = false;
        
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            
            Log.d(TAG, "已断开连接");
            
        } catch (IOException e) {
            Log.e(TAG, "断开连接失败", e);
        }
        
        notifyMainThread(() -> {
            if (listener != null) {
                listener.onDisconnected();
            }
        });
    }
    
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
    
    private void notifyMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
