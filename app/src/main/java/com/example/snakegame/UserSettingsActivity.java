package com.example.snakegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserSettingsActivity extends AppCompatActivity {
    
    private TextView tvCurrentNickname;
    private EditText etNewNickname;
    private Button btnUpdateNickname, btnBack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        
        initViews();
        setupListeners();
        loadCurrentSettings();
    }
    
    private void initViews() {
        tvCurrentNickname = findViewById(R.id.tv_current_nickname);
        etNewNickname = findViewById(R.id.et_new_nickname);
        btnUpdateNickname = findViewById(R.id.btn_update_nickname);
        btnBack = findViewById(R.id.btn_back);
    }
    
    private void setupListeners() {
        btnUpdateNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickname();
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void loadCurrentSettings() {
        String currentNickname = sharedPreferences.getString("currentNickname", "玩家");
        tvCurrentNickname.setText("当前昵称：" + currentNickname);
        etNewNickname.setHint("当前：" + currentNickname);
    }
    
    private void updateNickname() {
        String newNickname = etNewNickname.getText().toString().trim();
        
        if (newNickname.isEmpty()) {
            Toast.makeText(this, "请输入新昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newNickname.length() < 2 || newNickname.length() > 12) {
            Toast.makeText(this, "昵称长度应在2-12字符之间", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 更新当前用户的昵称
        String currentUsername = sharedPreferences.getString("currentUsername", "");
        if (!currentUsername.isEmpty()) {
            // 更新用户数据中的昵称
            SharedPreferences userPrefs = getSharedPreferences("User_" + currentUsername, MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPrefs.edit();
            userEditor.putString("nickname", newNickname);
            userEditor.apply();
            
            // 更新当前登录状态中的昵称
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentNickname", newNickname);
            editor.apply();
            
            Toast.makeText(this, "昵称更新成功！", Toast.LENGTH_SHORT).show();
            loadCurrentSettings(); // 刷新显示
            etNewNickname.setText(""); // 清空输入框
        } else {
            Toast.makeText(this, "更新失败，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }
}
