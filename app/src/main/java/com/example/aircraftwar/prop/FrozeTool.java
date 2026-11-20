package com.example.aircraftwar.prop;

import android.util.Log;

import com.example.aircraftwar.aircraft.HeroAircraft;

public class FrozeTool extends Tool {

    public FrozeTool(int locationX, int locationY, int speedX, int speedY, int effect) {
        super(locationX, locationY, speedX, speedY, effect);
    }

    @Override
    public void activate(HeroAircraft hero, PropEffectHandler game) {
        Log.d(TAG, "FrozeSupply active!");
        game.activateFreeze();
    }


    private static final String TAG = "AircraftWar";
}

