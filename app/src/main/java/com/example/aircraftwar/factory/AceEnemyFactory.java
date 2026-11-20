package com.example.aircraftwar.factory;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.AceEnemy;

public class AceEnemyFactory implements EnemyFactory {

    @Override
    public AbstractAircraft createEnemy(
            int x,
            int y,
            int speedX,
            int speedY,
            int hp
    ) {
        return new AceEnemy(x, y, speedX, speedY, hp);
    }
}
