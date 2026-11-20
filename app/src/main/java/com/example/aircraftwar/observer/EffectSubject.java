package com.example.aircraftwar.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象观察目标：维护观察者列表，并负责统一通知。
 */
public abstract class EffectSubject {
    protected final List<EffectObserver> observers = new ArrayList<>();

    public void addObserver(EffectObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(EffectObserver observer) {
        observers.remove(observer);
    }

    public void notifyBomb() {
        for (EffectObserver observer : new ArrayList<>(observers)) {
            observer.onBomb();
        }
    }

    public void notifyFreeze() {
        for (EffectObserver observer : new ArrayList<>(observers)) {
            observer.onFreeze();
        }
    }
}
