package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.strategy.*;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private volatile static HeroAircraft instance;

    public static HeroAircraft getInstance() {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft();
                }
            }
        }
        return instance;
    }

    // 子弹威力
    private int power = 30;

    /**
     * 当前射击策略
     */
    private ShootStrategy shootStrategy;

    /**
     * 默认射击策略：直射
     */
    private final ShootStrategy defaultShootStrategy = new SingleShootStrategy();

    private int maxHp;

    private HeroAircraft() {
        super(0, 0, 0, 0, 200);
        this.shootStrategy = defaultShootStrategy;
    }

    public void init(int locationX, int locationY, int speedX, int speedY, int hp, int maxHp) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
        this.maxHp = maxHp;

        // 每局游戏开始时，恢复为默认直射弹道
        this.shootStrategy = defaultShootStrategy;
    }

    /**
     * 切换射击策略
     *
     * 因为火力道具的恢复逻辑会在另一个线程里执行，
     * 所以这里加 synchronized，避免多线程同时修改 shootStrategy。
     */
    public synchronized void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    /**
     * 恢复默认直射策略
     */
    public synchronized void resetShootStrategy() {
        this.shootStrategy = defaultShootStrategy;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过 forward 函数移动
    }

    /**
     * 通过射击产生子弹
     *
     * @return 射击出的子弹 List
     */
    @Override
    public List<BaseBullet> shoot() {
        ShootStrategy currentStrategy;

        synchronized (this) {
            currentStrategy = this.shootStrategy;
        }

        return currentStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                this.getSpeedX(),
                this.getSpeedY(),
                power,
                false
        );
    }

    /**
     * 加血
     */
    public void increaseHp(int add) {
        this.hp = Math.min(this.hp + add, maxHp);
    }
}
