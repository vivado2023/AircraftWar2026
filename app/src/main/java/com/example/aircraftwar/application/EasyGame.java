package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.factory.EliteEnemyFactory;
import com.example.aircraftwar.factory.EnemyFactory;
import com.example.aircraftwar.factory.MobEnemyFactory;

/**
 * 简单难度：难度不随时间递进，且不会生成 Boss。
 * Windows 版 EasyGame extends Game(JPanel) -> Android 版 extends BaseGame(SurfaceView)。
 *
 * @author hitsz
 */
public class EasyGame extends BaseGame {

    private static final String TAG = "AircraftWar";

    public EasyGame(Context context) {
        super(context);
    }

    @Override
    protected Bitmap getBackgroundImage() {
        return ImageManager.BACKGROUND_EASY_IMAGE;
    }

    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 4;
        enemySpawnCycle = 28;
        heroShootCycle = 12;
        enemyShootCycle = 24;
        enemySpeedRate = 0.8;
        enemyHpRate = 0.8;

        Log.d(TAG, "当前难度：简单。难度不递进，不生成Boss敌机。");
    }

    @Override
    protected AbstractAircraft createEnemy(int x, int y) {
        EnemyFactory factory;

        if (Math.random() < 0.75) {
            factory = new MobEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(8), scaleHp(25));
        } else {
            factory = new EliteEnemyFactory();
            return factory.createEnemy(x, y, 0, scaleSpeed(7), scaleHp(40));
        }
    }

    @Override
    protected boolean canGenerateBoss() {
        return false;
    }

    @Override
    protected void onBossGenerated(BossEnemy bossEnemy) {
        // 简单难度不生成 Boss。
    }

    @Override
    protected void increaseDifficulty() {
        // 简单难度全程保持初始设定。
    }
}
