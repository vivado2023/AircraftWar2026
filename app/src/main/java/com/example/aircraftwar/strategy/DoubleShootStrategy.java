package com.example.aircraftwar.strategy;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class DoubleShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(
            int locationX,
            int locationY,
            int speedX,
            int speedY,
            int power,
            boolean isEnemy
    ) {
        List<BaseBullet> bullets = new LinkedList<>();

        if (isEnemy) {
            bullets.add(new EnemyBullet(locationX - 15, locationY, 0, 10, power));
            bullets.add(new EnemyBullet(locationX + 15, locationY, 0, 10, power));
        } else {
            bullets.add(new HeroBullet(locationX - 15, locationY, 0, -10, power));
            bullets.add(new HeroBullet(locationX + 15, locationY, 0, -10, power));
        }

        return bullets;
    }
}
