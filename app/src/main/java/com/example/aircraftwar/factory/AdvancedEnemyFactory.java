package com.example.aircraftwar.factory;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.AdvancedEnemy;

public class AdvancedEnemyFactory implements EnemyFactory {

    @Override
    public AbstractAircraft createEnemy(
            int x,
            int y,
            int speedX,
            int speedY,
            int hp
    ) {
        return new AdvancedEnemy(x, y, speedX, speedY, hp);
    }
}
