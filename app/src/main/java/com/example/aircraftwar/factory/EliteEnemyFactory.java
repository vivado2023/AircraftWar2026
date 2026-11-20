package com.example.aircraftwar.factory;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.EliteEnemy;

public class EliteEnemyFactory implements EnemyFactory {

    @Override
    public AbstractAircraft createEnemy(
            int locationX,
            int locationY,
            int speedX,
            int speedY,
            int hp
    ) {
        return new EliteEnemy(
                locationX,
                locationY,
                speedX,
                speedY,
                hp
        );
    }
}
