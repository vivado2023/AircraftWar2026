package com.example.aircraftwar.aircraft;

import android.util.Log;

import com.example.aircraftwar.application.GameConfig;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.prop.*;
import com.example.aircraftwar.strategy.*;
import java.util.LinkedList;
import java.util.List;

public class AceEnemy extends MobEnemy {

    private int power = 20;
    private ShootStrategy shootStrategy;

    public AceEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new ScatterShootStrategy();
    }

    @Override
    public void forward() {
        locationX += speedX;
        locationY += speedY;

        // 更快反弹
        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            speedX = -speedX;
        }

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
        //掉落概率
        if (Math.random() >= 0.7) {
            return null;
        }
        // 全部5种
        return ToolFactory.createTool(locationX, locationY, 5);
    }

    @Override
    public void onFreeze() {
        Log.d(TAG, "王牌敌机受到冰冻：减速5秒");
        slowFor(0.5, 5000);
    }


    private static final String TAG = "AircraftWar";
}
