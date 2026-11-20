package com.example.aircraftwar.aircraft;

import android.util.Log;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.prop.*;
import com.example.aircraftwar.strategy.*;
import java.util.LinkedList;
import java.util.List;

public class EliteEnemy extends MobEnemy {

    private ShootStrategy shootStrategy;
    private int power = 10;        // 子弹伤害

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new SingleShootStrategy();
    }

    /**
     * 射击方法：保持原来的直线单发
     */
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

    /**
     * 道具掉落方法：50%概率掉落三种道具之一
     */
    @Override
    public Tool dropProp() {
        if (Math.random() >= 0.5) {
            return null;
        }

        return ToolFactory.createTool(locationX, locationY,3);
    }

    @Override
    public void onFreeze() {
        Log.d(TAG, "精英敌机受到冰冻：静止4秒");
        freezeFor(4000);
    }


    private static final String TAG = "AircraftWar";
}
