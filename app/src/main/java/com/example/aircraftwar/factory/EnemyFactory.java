package com.example.aircraftwar.factory;

import com.example.aircraftwar.aircraft.AbstractAircraft;

public interface EnemyFactory {

    AbstractAircraft createEnemy(
            int locationX,
            int locationY,
            int speedX,
            int speedY,
            int hp
    );
}
