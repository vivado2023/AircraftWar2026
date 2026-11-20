package com.example.aircraftwar.aircraft;

import android.util.Log;

import com.example.aircraftwar.application.GameConfig;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.prop.*;
import com.example.aircraftwar.strategy.*;
import java.util.LinkedList;
import java.util.List;

public class AdvancedEnemy extends MobEnemy {

    private int power = 15;
    private ShootStrategy shootStrategy;

    public AdvancedEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new DoubleShootStrategy();
    }

    @Override
    public void forward() {
        locationX += speedX;
        locationY += speedY;

        // 左右边界反弹（可选）
        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            speedX = -speedX;
        }

        // 出界消失
        if (locationY >= GameConfig.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                this.getSpeedX(),
                this.getSpeedY(),
                power,
                true
        );
    }
    
    @Override
    public Tool dropProp() {
        //道具掉落概率
        if (Math.random() >= 0.5) {
            return null;
        }

        // 4种：不含冰冻
        return ToolFactory.createTool(locationX, locationY, 4);
    }

    @Override
    public void onFreeze() {
        Log.d(TAG, "精锐敌机受到冰冻：静止3秒");
        freezeFor(3000);
    }


    private static final String TAG = "AircraftWar";
}
