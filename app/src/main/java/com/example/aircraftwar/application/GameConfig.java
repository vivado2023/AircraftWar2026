package com.example.aircraftwar.application;

/**
 * 游戏全局配置：替代 Windows 版的 Main.WINDOW_WIDTH / WINDOW_HEIGHT。
 * <p>
 * 逻辑窗口采用与 Windows 版一致的 512x768 坐标系，屏幕像素坐标按 SCALE 换算，
 * 由 BaseGame 在 surfaceChanged 中根据真实屏幕尺寸更新，避免写死分辨率。
 *
 * @author hitsz
 */
public class GameConfig {

    /** 逻辑窗口宽度（逻辑像素），随屏幕宽高比动态调整 */
    public static int WINDOW_WIDTH = 512;

    /** 逻辑窗口高度（逻辑像素），与 Windows 版保持一致 */
    public static int WINDOW_HEIGHT = 768;

    /** 屏幕像素 -> 逻辑坐标的缩放系数：逻辑坐标 = (屏幕像素 - OFFSET) / SCALE */
    public static float SCALE = 1.0f;

    /** 等比缩放居中后，逻辑区域左上角在屏幕上的像素偏移（letterbox 黑边） */
    public static float OFFSET_X = 0f;
    public static float OFFSET_Y = 0f;

    private GameConfig() {
    }
}
