package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.factory.AdvancedEnemyFactory;
import com.example.aircraftwar.factory.AceEnemyFactory;
import com.example.aircraftwar.factory.EliteEnemyFactory;
import com.example.aircraftwar.factory.EnemyFactory;
import com.example.aircraftwar.factory.MobEnemyFactory;

/**
 * 困难难度：
 * 全部关键参数递进，且每次召唤Boss时提升Boss血量。
 *
 * @author hitsz
 */
public class HardGame extends BaseGame {

    private static final String TAG = "AircraftWar";

    public HardGame(Context context) {
        super(context);
    }

    @Override
    protected Bitmap getBackgroundImage() {
        return ImageManager.BACKGROUND_HARD_IMAGE;
    }

    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 7;
        enemySpawnCycle = 18;
        heroShootCycle = 17;
        enemyShootCycle = 14;
        enemySpeedRate = 1.15;
        enemyHpRate = 1.20;
        bossHp = 350;

        Log.d(TAG, "当前难度：困难。生成周期、速度、血量、英雄/敌机射击周期都会递进；Boss血量递增。");
    }

    @Override
    protected AbstractAircraft createEnemy(int x, int y) {
        double rand = Math.random();
        EnemyFactory factory;

        if (rand < 0.35) {
            factory = new MobEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(11), scaleHp(35));
        } else if (rand < 0.60) {
            factory = new EliteEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(9), scaleHp(60));
        } else if (rand < 0.85) {
            int speedX = Math.random() < 0.5 ? -4 : 4;
            factory = new AdvancedEnemyFactory();
            return factory.createEnemy(x, y, scaleSpeed(speedX), scaleSpeed(7), scaleHp(85));
        } else {
            int speedX = Math.random() < 0.5 ? -5 : 5;
            factory = new AceEnemyFactory();
            return factory.createEnemy(x, y, scaleSpeed(speedX), scaleSpeed(6), scaleHp(120));
        }
    }

    @Override
    protected boolean canGenerateBoss() {
        return true;
    }

    @Override
    protected void onBossGenerated(BossEnemy bossEnemy) {
        Log.d(TAG, "困难难度召唤Boss，本次Boss血量：" + bossHp);
        bossHp += 80;
        Log.d(TAG, "下次Boss血量提升为：" + bossHp);
    }

    @Override
    protected void increaseDifficulty() {
        enemySpawnCycle = Math.max(7, enemySpawnCycle - 1.5);
        enemySpeedRate += 0.08;
        enemyHpRate += 0.08;
        heroShootCycle = Math.min(28, heroShootCycle + 1);
        enemyShootCycle = Math.max(7, enemyShootCycle - 1);

        Log.d(TAG, "困难难度递进：敌机生成周期=" + enemySpawnCycle
                + "，速度倍率=" + String.format("%.2f", enemySpeedRate)
                + "，血量倍率=" + String.format("%.2f", enemyHpRate)
                + "，英雄射击周期=" + heroShootCycle
                + "，敌机射击周期=" + enemyShootCycle);
    }
}
