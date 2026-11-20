package com.example.aircraftwar.prop;

/**
 * 道具效果回调接口：把道具（炸弹/冰冻）需要的游戏全局操作抽象出来，
 * 避免 prop 包直接依赖 BaseGame（Android 移植时解耦，BaseGame 在步骤3才引入）。
 */
public interface PropEffectHandler {

    /** 炸弹效果：通知所有敌机和敌方子弹 */
    void activateBomb();

    /** 冰冻效果：通知所有敌机和敌方子弹 */
    void activateFreeze();
}
