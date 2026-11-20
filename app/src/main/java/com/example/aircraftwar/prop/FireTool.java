package com.example.aircraftwar.prop;

import android.util.Log;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.strategy.ScatterShootStrategy;

public class FireTool extends Tool {

    private static final long DURATION_MS = 5000;

    public FireTool(int locationX, int locationY, int speedX, int speedY, int effect) {
        super(locationX, locationY, speedX, speedY, effect);
    }

    @Override
    public void activate(HeroAircraft hero, PropEffectHandler game) {
        Log.d(TAG, "FireSupply active!");

        FirePowerManager.activateFirePower(
                hero,
                new ScatterShootStrategy(),
                DURATION_MS
        );
    }


    private static final String TAG = "AircraftWar";
}

