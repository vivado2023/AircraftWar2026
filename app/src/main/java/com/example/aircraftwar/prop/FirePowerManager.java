package com.example.aircraftwar.prop;

import android.util.Log;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.strategy.ShootStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 火力道具效果管理器
 *
 * 作用：
 * 1. 道具生效时切换英雄机弹道
 * 2. 使用多线程计时
 * 3. 指定时间后自动恢复为直射状态
 */
public class FirePowerManager {

    private static final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();

    private static ScheduledFuture<?> recoverTask;

    /**
     * 激活火力道具效果
     *
     * @param heroAircraft 英雄机
     * @param shootStrategy 临时射击策略
     * @param durationMs 持续时间，单位毫秒
     */
    public static synchronized void activateFirePower(
            HeroAircraft heroAircraft,
            ShootStrategy shootStrategy,
            long durationMs
    ) {
        // 1. 立刻切换弹道
        heroAircraft.setShootStrategy(shootStrategy);

        // 2. 如果之前已经有火力恢复任务，先取消
        // 这样连续吃到火力道具时，会重新计时
        if (recoverTask != null && !recoverTask.isDone()) {
            recoverTask.cancel(false);
        }

        // 3. 开启新的延时恢复任务
        recoverTask = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                heroAircraft.resetShootStrategy();
                Log.d(TAG, "火力道具效果结束，恢复为直射弹道");
            }
        }, durationMs, TimeUnit.MILLISECONDS);
    }
    
    public static synchronized void shutdown() {
        if (recoverTask != null && !recoverTask.isDone()) {
            recoverTask.cancel(false);
        }

        executorService.shutdownNow();
    }


    private static final String TAG = "AircraftWar";
}
