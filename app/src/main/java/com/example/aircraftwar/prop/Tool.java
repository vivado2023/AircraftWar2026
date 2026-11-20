package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.basic.AbstractFlyingObject;

public abstract class Tool extends AbstractFlyingObject {

    protected int effect;

    public Tool(int locationX, int locationY, int speedX, int speedY, int effect) {
        super(locationX, locationY, speedX, speedY);
        this.effect = effect;
    }

    @Override
    public void forward() {
        super.forward();
    }

    public int getEffect() {
        return effect;
    }

    /**
     * 核心方法：道具生效
     */
    public abstract void activate(HeroAircraft hero, PropEffectHandler game);
}


