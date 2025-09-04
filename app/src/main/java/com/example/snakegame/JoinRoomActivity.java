package com.example.snakegame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        
        // 简单实现：暂时让用户手动输入IP地址
        // 这里可以后续扩展为UDP广播搜索
        showIPInputDialog(roomId, dialog);
    }
    
    private void showIPInputDialog(String roomId, AlertDialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入房主IP地址");
        builder.setMessage("房间号: " + roomId + "\n请向房主询问其设备的IP地址");
        
        EditText etIP = new EditText(this);
        etIP.setHint("例如: 192.168.1.100");
        builder.setView(etIP);
        
        builder.setPositiveButton("连接", (d, which) -> {
            String serverIP = etIP.getText().toString().trim();
            if (serverIP.isEmpty()) {
                Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!isValidIPAddress(serverIP)) {
                Toast.makeText(this, "请输入有效的IP地址", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 进入房间
            Intent intent = new Intent(JoinRoomActivity.this, RoomActivity.class);
            intent.putExtra("isHost", false);
            intent.putExtra("roomId", roomId);
            intent.putExtra("serverIP", serverIP);
            startActivity(intent);
            
            parentDialog.dismiss();
            d.dismiss();
            finish();
        });
        
        builder.setNegativeButton("取消", (d, which) -> d.dismiss());
        builder.show();
    }
    
    private boolean isValidIPAddress(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
