package com.example.aircraftwar.aircraft;

import android.util.Log;

import com.example.aircraftwar.application.GameConfig;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.prop.Tool;
import com.example.aircraftwar.prop.ToolFactory;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends MobEnemy {

    private int power = 30;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        // ❗ 只左右移动
        locationX += speedX;

        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            speedX = -speedX;
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> bullets = new LinkedList<>();

        // 扇形弹幕（7发）
        for (int i = -3; i <= 3; i++) {
            bullets.add(new EnemyBullet(
                    locationX,
                    locationY,
                    i * 2,
                    5,
                    power
            ));
        }

        return bullets;
    }
    @Override
    public Tool dropProp() {
        return ToolFactory.createTool(locationX, locationY, 5);
    }
}

