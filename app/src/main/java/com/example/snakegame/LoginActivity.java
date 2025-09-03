package com.example.snakegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etNickname, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 检查是否已登录
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }
    
    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    
    private void login() {
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入昵称和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证用户信息 - 现在基于昵称查找用户
        SharedPreferences userPrefs = getSharedPreferences("User_" + nickname, MODE_PRIVATE);
        String savedPassword = userPrefs.getString("password", "");
        
        if (savedPassword.isEmpty()) {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!savedPassword.equals(password)) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 登录成功，保存登录状态
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("currentNickname", nickname);
        editor.apply();
        
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
