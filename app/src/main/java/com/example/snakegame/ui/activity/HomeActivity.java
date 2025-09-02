package com.example.snakegame.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.snakegame.MainActivity;
import com.example.snakegame.R;

public class HomeActivity extends AppCompatActivity {
    
    private Button btnStartGame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        btnStartGame = findViewById(R.id.btn_start_game);
    }
    
    private void setupClickListeners() {
        btnStartGame.setOnClickListener(v -> {
            // 跳转到游戏界面
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickname", "Player1");
            intent.putExtra("color", "#FF0000");
            startActivity(intent);
        });
    }
}