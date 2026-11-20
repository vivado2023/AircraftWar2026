package com.example.aircraftwar.strategy;

import com.example.aircraftwar.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class NoShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(
            int locationX,
            int locationY,
            int speedX,
            int speedY,
            int power,
            boolean isEnemy
    ) {
        return new LinkedList<>();
    }
}
