package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;

public class HpTool extends Tool {

    public HpTool(int locationX, int locationY, int speedX, int speedY, int effect) {
        super(locationX, locationY, speedX, speedY, effect);
    }

    @Override
    public void activate(HeroAircraft hero, PropEffectHandler game) {
        hero.increaseHp(effect);
    }
}


