package com.example.aircraftwar.strategy;

import com.example.aircraftwar.bullet.BaseBullet;
import java.util.List;

public interface ShootStrategy {
    List<BaseBullet> shoot(
            int locationX,
            int locationY,
            int speedX,
            int speedY,
            int power,
            boolean isEnemy
    );
}
