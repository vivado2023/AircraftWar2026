package com.example.aircraftwar.strategy;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class CircleShootStrategy implements ShootStrategy {

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

        int bulletCount = 20;
        int bulletSpeed = 6;

        for (int i = 0; i < bulletCount; i++) {
            double angle = 2 * Math.PI * i / bulletCount;

            int vx = (int) (bulletSpeed * Math.cos(angle));
            int vy = (int) (bulletSpeed * Math.sin(angle));

            if (isEnemy) {
                bullets.add(new EnemyBullet(locationX, locationY, vx, vy, power));
            } else {
                bullets.add(new HeroBullet(locationX, locationY, vx, vy, power));
            }
        }

        return bullets;
    }
}
