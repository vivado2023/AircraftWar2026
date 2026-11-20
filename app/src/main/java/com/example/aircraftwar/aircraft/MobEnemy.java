package com.example.aircraftwar.aircraft;

import android.util.Log;

import com.example.aircraftwar.application.GameConfig;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.prop.*;
import com.example.aircraftwar.strategy.*;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    private static final Random RANDOM = new Random();
    private ShootStrategy shootStrategy;
    private int power = 0;

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new NoShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WINDOW_HEIGHT ) {
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
    public void onFreeze() {
        Log.d(TAG, "普通敌机受到冰冻：永久静止");
        stopForever();
    }



    private static final String TAG = "AircraftWar";
}
