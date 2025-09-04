package com.example.snakegame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.net.*;
import java.io.*;

public class JoinRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showJoinRoomDialog();
    }
    
    private void showJoinRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_join_room, null);
        builder.setView(dialogView);
        
        EditText etRoomId = dialogView.findViewById(R.id.et_room_id);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnJoin = dialogView.findViewById(R.id.btn_join);
        
        AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = etRoomId.getText().toString().trim();
                if (roomId.isEmpty()) {
                    Toast.makeText(JoinRoomActivity.this, "请输入6位房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 验证6位数字格式
                if (!isValidRoomId(roomId)) {
                    Toast.makeText(JoinRoomActivity.this, "请输入有效的6位房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 搜索房间
                searchForRoom(roomId, dialog);
            }
        });
        
        dialog.show();
    }
    
    private boolean isValidRoomId(String roomId) {
        if (roomId.length() != 6) {
            return false;
        }
        
        try {
            Integer.parseInt(roomId);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void searchForRoom(String roomId, AlertDialog dialog) {
        Toast.makeText(this, "正在搜索房间...", Toast.LENGTH_SHORT).show();
        
        // 使用UDP广播搜索局域网内的房间
        searchRoomByBroadcast(roomId, dialog);
    }
    
    private void searchRoomByBroadcast(String roomId, AlertDialog dialog) {
        new Thread(() -> {
            DatagramSocket socket = null;
            try {
                // 发送UDP广播搜索房间
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setSoTimeout(5000); // 5秒超时
                
                String searchMessage = "SEARCH_ROOM:" + roomId;
                byte[] buffer = searchMessage.getBytes();
                
                // 尝试多个广播地址
                String[] broadcastAddresses = {
                    "255.255.255.255",
                    "192.168.1.255",
                    "192.168.0.255",
                    "10.0.0.255"
                };
                
                boolean found = false;
                
                for (String broadcastAddr : broadcastAddresses) {
                    try {
                        InetAddress broadcastAddress = InetAddress.getByName(broadcastAddr);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8889);
                        
                        socket.send(packet);
                        Log.d("JoinRoomActivity", "发送广播搜索到 " + broadcastAddr + ": " + searchMessage);
                        
                        // 等待响应
                        byte[] responseBuffer = new byte[1024];
                        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                        
                        try {
                            socket.receive(responsePacket);
                            
                            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                            Log.d("JoinRoomActivity", "收到响应: " + response);
                            
                            if (response.startsWith("ROOM_FOUND:")) {
                                String serverIP = responsePacket.getAddress().getHostAddress();
                                found = true;
                                
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "找到房间，正在连接...", Toast.LENGTH_SHORT).show();
                                    
                                    // 直接加入房间
                                    Intent intent = new Intent(JoinRoomActivity.this, RoomActivity.class);
                                    intent.putExtra("isHost", false);
                                    intent.putExtra("roomId", roomId);
                                    intent.putExtra("serverIP", serverIP);
                                    startActivity(intent);
                                    
                                    dialog.dismiss();
                                    finish();
                                });
                                break;
                            }
                        } catch (SocketTimeoutException e) {
                            // 继续尝试下一个广播地址
                            Log.d("JoinRoomActivity", "广播地址 " + broadcastAddr + " 超时");
                        }
                    } catch (Exception e) {
                        Log.e("JoinRoomActivity", "广播到 " + broadcastAddr + " 失败: " + e.getMessage());
                    }
                }
                
                if (!found) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "未找到房间 " + roomId + "，请确认房间号是否正确", Toast.LENGTH_LONG).show();
                    });
                }
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "搜索房间失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }).start();
    }
}
