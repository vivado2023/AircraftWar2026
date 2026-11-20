package com.example.aircraftwar.bullet;

import android.util.Log;

import com.example.aircraftwar.application.GameConfig;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.observer.EffectObserver;

/**
 * 子弹基类
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject implements EffectObserver {

    private int power = 0;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            vanish();
        }

        if (speedY > 0 && locationY >= GameConfig.WINDOW_HEIGHT) {
            vanish();
        } else if (locationY <= 0) {
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        Log.d(TAG, "敌机子弹受到冰冻：静止5秒");
        freezeFor(5000);
    }


    private static final String TAG = "AircraftWar";
}
