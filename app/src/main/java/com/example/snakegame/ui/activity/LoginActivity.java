package com.example.snakegame.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.snakegame.MainActivity;
import com.example.snakegame.R;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etNickname;
    private Button btnStartGame;
    private View selectedColorView;
    private String selectedColor = "#FF0000"; // 默认红色
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_setup);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etNickname = findViewById(R.id.et_nickname);
        btnStartGame = findViewById(R.id.btn_start_game);
        
        // 默认选中红色
        selectedColorView = findViewById(R.id.color_red);
        selectedColorView.setScaleX(1.2f);
        selectedColorView.setScaleY(1.2f);
    }
    
    private void setupClickListeners() {
        // 颜色选择
        setupColorSelector(R.id.color_red, "#FF0000");
        setupColorSelector(R.id.color_green, "#00FF00");
        setupColorSelector(R.id.color_blue, "#0000FF");
        setupColorSelector(R.id.color_yellow, "#FFFF00");
        setupColorSelector(R.id.color_purple, "#FF00FF");
        
        // 开始游戏按钮
        btnStartGame.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString().trim();
            if (nickname.isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 跳转到游戏界面，传递昵称和颜色
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickname", nickname);
            intent.putExtra("color", selectedColor);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupColorSelector(int viewId, String color) {
        View colorView = findViewById(viewId);
        colorView.setOnClickListener(v -> {
            // 重置之前选中的颜色
            if (selectedColorView != null) {
                selectedColorView.setScaleX(1.0f);
                selectedColorView.setScaleY(1.0f);
            }
            
            // 设置新选中的颜色
            selectedColorView = v;
            selectedColorView.setScaleX(1.2f);
            selectedColorView.setScaleY(1.2f);
            selectedColor = color;
        });
    }
}