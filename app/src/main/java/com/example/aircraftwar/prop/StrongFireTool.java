package com.example.aircraftwar.prop;

import android.util.Log;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.strategy.CircleShootStrategy;

public class StrongFireTool extends Tool {

    private static final long DURATION_MS = 8000;

    public StrongFireTool(int locationX, int locationY, int speedX, int speedY, int effect) {
        super(locationX, locationY, speedX, speedY, effect);
    }

    @Override
    public void activate(HeroAircraft hero, PropEffectHandler game) {
        Log.d(TAG, "FireSupplyPlus active!");

        FirePowerManager.activateFirePower(
                hero,
                new CircleShootStrategy(),
                DURATION_MS
        );
    }


    private static final String TAG = "AircraftWar";
}

