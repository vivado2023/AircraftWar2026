package com.example.aircraftwar.strategy;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class ScatterShootStrategy implements ShootStrategy {

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

        int direction = isEnemy ? 10 : -10;

        if (isEnemy) {
            bullets.add(new EnemyBullet(locationX, locationY, -2, direction, power));
            bullets.add(new EnemyBullet(locationX, locationY, 0, direction, power));
            bullets.add(new EnemyBullet(locationX, locationY, 2, direction, power));
        } else {
            bullets.add(new HeroBullet(locationX, locationY, -2, direction, power));
            bullets.add(new HeroBullet(locationX, locationY, 0, direction, power));
            bullets.add(new HeroBullet(locationX, locationY, 2, direction, power));
        }

        return bullets;
    }
}
