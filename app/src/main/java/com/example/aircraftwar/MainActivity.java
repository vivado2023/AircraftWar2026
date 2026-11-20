package com.example.aircraftwar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.application.EasyGame;

/**
 * 程序入口：Windows 版 Main.java（JFrame + Mode 面板）迁移为 Activity。
 * 默认使用简单模式（难度选择页面在实验三实现）。
 *
 * @author hitsz
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 默认使用简单模式
        setContentView(new EasyGame(this));
    }
}
