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
 * 普通难度：
 * 敌机产生周期、敌机速度、敌机血量随时间递进；
 * Boss血量不递增。
 *
 * @author hitsz
 */
public class NormalGame extends BaseGame {

    private static final String TAG = "AircraftWar";

    public NormalGame(Context context) {
        super(context);
    }

    @Override
    protected Bitmap getBackgroundImage() {
        return ImageManager.BACKGROUND_NORMAL_IMAGE;
    }

    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 5;
        enemySpawnCycle = 22;
        heroShootCycle = 15;
        enemyShootCycle = 18;
        enemySpeedRate = 1.0;
        enemyHpRate = 1.0;
        bossHp = 300;

        Log.d(TAG, "当前难度：普通。敌机产生周期、速度、血量会递进；Boss血量不递增。");
    }

    @Override
    protected AbstractAircraft createEnemy(int x, int y) {
        double rand = Math.random();
        EnemyFactory factory;

        if (rand < 0.50) {
            factory = new MobEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(10), scaleHp(30));
        } else if (rand < 0.72) {
            factory = new EliteEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(8), scaleHp(50));
        } else if (rand < 0.92) {
            int speedX = Math.random() < 0.5 ? -3 : 3;
            factory = new AdvancedEnemyFactory();
            return factory.createEnemy(x, y, scaleSpeed(speedX), scaleSpeed(6), scaleHp(70));
        } else {
            int speedX = Math.random() < 0.5 ? -4 : 4;
            factory = new AceEnemyFactory();
            return factory.createEnemy(x, y, scaleSpeed(speedX), scaleSpeed(5), scaleHp(100));
        }
    }

    @Override
    protected boolean canGenerateBoss() {
        return true;
    }

    @Override
    protected void onBossGenerated(BossEnemy bossEnemy) {
        Log.d(TAG, "普通难度召唤Boss，Boss血量保持：" + bossHp);
    }

    @Override
    protected void increaseDifficulty() {
        enemySpawnCycle = Math.max(10, enemySpawnCycle - 1);
        enemySpeedRate += 0.05;
        enemyHpRate += 0.05;

        Log.d(TAG, "普通难度递进：敌机生成周期=" + enemySpawnCycle
                + "，速度倍率=" + String.format("%.2f", enemySpeedRate)
                + "，血量倍率=" + String.format("%.2f", enemyHpRate));
    }
}
