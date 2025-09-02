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
                    Toast.makeText(JoinRoomActivity.this, "请输入房间号(IP地址)", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 验证IP地址格式
                if (!isValidIPAddress(roomId)) {
                    Toast.makeText(JoinRoomActivity.this, "请输入有效的IP地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 进入房间
                Intent intent = new Intent(JoinRoomActivity.this, RoomActivity.class);
                intent.putExtra("isHost", false);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
                
                dialog.dismiss();
                finish();
            }
        });
        
        dialog.show();
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
