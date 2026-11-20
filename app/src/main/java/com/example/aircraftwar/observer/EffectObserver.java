package com.example.aircraftwar.observer;

/**
 * 观察者接口：场上可能受到炸弹/冰冻影响的对象实现该接口。
 */
public interface EffectObserver {
    void onBomb();
    void onFreeze();
}
