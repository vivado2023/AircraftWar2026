package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.prop.Tool;

import java.util.LinkedList;
import java.util.List;

/**
 * 游戏主面板：Windows 版 Game（JPanel + Timer）迁移为 Android 的 SurfaceView。
 * <ul>
 *   <li>实现 SurfaceHolder.Callback 与 Runnable，run() 中 lockCanvas → 绘制 → unlockCanvasAndPost；</li>
 *   <li>surfaceCreated 一次性加载并缓存图片，启动绘制线程；</li>
 *   <li>surfaceChanged 按真实屏幕尺寸换算缩放与偏移，不写死分辨率；</li>
 *   <li>surfaceDestroyed 置停止标志并等待线程结束。</li>
 * </ul>
 * 模板模式：固定主循环流程，简单/普通/困难三个子类只重写差异步骤。
 * 本步骤（游戏循环）先跑通“背景滚动 + 英雄机”的静止画面，触屏交互与游戏逻辑在步骤4接入。
 *
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "AircraftWar";

    private final SurfaceHolder surfaceHolder;
    private Thread drawThread;
    private volatile boolean isRunning = false;

    private int backGroundTop = 0;

    /** 一帧逻辑的固定间隔（与 Windows 版 Timer 周期一致） */
    private final int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<Tool> props;

    private Bitmap backgroundImage;

    protected int enemyMaxNumber = 5;
    protected double enemySpawnCycle = 20;

    protected double heroShootCycle = 15;
    protected double enemyShootCycle = 15;

    protected double enemySpeedRate = 1.0;
    protected double enemyHpRate = 1.0;
    protected int bossHp = 300;

    protected int score = 0;

    private final Paint paint = new Paint();

    public BaseGame(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        setFocusable(true);
    }

    // ==================== SurfaceHolder.Callback ====================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 坑：图片在 surfaceCreated 里一次性 decode 并缓存，循环中只做绘制
        ImageManager.init(getContext().getApplicationContext());

        // 英雄机初始位置：底部居中（Windows 版在 Game 构造器中完成，
        // Android 移到 surfaceCreated，因为此时图片才可用）
        heroAircraft.init(
                GameConfig.WINDOW_WIDTH / 2,
                GameConfig.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0,
                0,
                300,
                300
        );

        backgroundImage = getBackgroundImage();
        initDifficultyParameters();

        isRunning = true;
        drawThread = new Thread(this, "game-draw-thread");
        drawThread.start();
        Log.d(TAG, "surfaceCreated: 图片加载完成，绘制线程已启动");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 坑：不写死分辨率——按真实屏幕尺寸等比缩放并居中（letterbox）
        float scaleX = width / (float) GameConfig.WINDOW_WIDTH;
        float scaleY = height / (float) GameConfig.WINDOW_HEIGHT;
        GameConfig.SCALE = Math.min(scaleX, scaleY);
        GameConfig.OFFSET_X = (width - GameConfig.WINDOW_WIDTH * GameConfig.SCALE) / 2f;
        GameConfig.OFFSET_Y = (height - GameConfig.WINDOW_HEIGHT * GameConfig.SCALE) / 2f;

        Log.d(TAG, "surfaceChanged: width=" + width + ", height=" + height
                + ", scale=" + GameConfig.SCALE
                + ", offset=(" + GameConfig.OFFSET_X + "," + GameConfig.OFFSET_Y + ")");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 坑：退出后必须停线程——置标志位并等待线程真正结束
        isRunning = false;
        if (drawThread != null) {
            try {
                drawThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        drawThread = null;
        Log.d(TAG, "surfaceDestroyed: 绘制线程已停止");
    }

    // ==================== Runnable：子线程游戏循环 ====================

    @Override
    public void run() {
        Log.d(TAG, "run: 游戏循环开始 (thread=" + Thread.currentThread().getName() + ")");
        while (isRunning) {
            long start = System.currentTimeMillis();

            // 本步骤只负责「画」，逻辑在步骤4接入
            drawFrame();

            long cost = System.currentTimeMillis() - start;
            long sleep = timeInterval - cost;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        Log.d(TAG, "run: 游戏循环退出");
    }

    private void drawFrame() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        try {
            paintScene(canvas);
        } catch (Exception e) {
            Log.e(TAG, "drawFrame error", e);
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // ==================== 模板方法：子类重写差异步骤 ====================

    protected abstract Bitmap getBackgroundImage();

    protected abstract void initDifficultyParameters();

    protected abstract AbstractAircraft createEnemy(int x, int y);

    protected abstract boolean canGenerateBoss();

    protected abstract void onBossGenerated(BossEnemy bossEnemy);

    protected abstract void increaseDifficulty();

    protected int scaleSpeed(int speed) {
        int scaled = (int) Math.round(speed * enemySpeedRate);

        if (speed != 0 && scaled == 0) {
            scaled = speed > 0 ? 1 : -1;
        }

        return scaled;
    }

    protected int scaleHp(int hp) {
        return Math.max(1, (int) Math.round(hp * enemyHpRate));
    }

    // ==================== 绘制 ====================

    private void paintScene(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        canvas.save();
        // 等比缩放并居中：逻辑 512x768 坐标系 -> 屏幕像素
        canvas.translate(GameConfig.OFFSET_X, GameConfig.OFFSET_Y);
        canvas.scale(GameConfig.SCALE, GameConfig.SCALE);
        // 裁剪到逻辑视口：Windows 版 JPanel 自动裁剪绘制区域，
        // Android Canvas 不裁剪，滚动背景会画进黑边（letterbox）里
        canvas.clipRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // 背景滚动：两张图首尾相接向下滚动
        canvas.drawBitmap(backgroundImage, 0, this.backGroundTop - GameConfig.WINDOW_HEIGHT, paint);
        canvas.drawBitmap(backgroundImage, 0, this.backGroundTop, paint);

        this.backGroundTop += 1;

        if (this.backGroundTop >= GameConfig.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        paintImageWithPositionRevised(canvas, enemyBullets);
        paintImageWithPositionRevised(canvas, heroBullets);
        paintImageWithPositionRevised(canvas, enemyAircrafts);
        paintImageWithPositionRevised(canvas, props);

        canvas.drawBitmap(
                ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2f,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2f,
                paint
        );

        paintScoreAndLife(canvas);

        canvas.restore();
    }

    private void paintImageWithPositionRevised(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();

            if (image == null) {
                Log.e(TAG, objects.getClass().getName() + " has no image! ");
                continue;
            }

            canvas.drawBitmap(
                    image,
                    object.getLocationX() - image.getWidth() / 2f,
                    object.getLocationY() - image.getHeight() / 2f,
                    paint
            );
        }
    }

    private void paintScoreAndLife(Canvas canvas) {
        paint.setColor(Color.RED);
        paint.setTextSize(22);

        canvas.drawText("SCORE: " + this.score, 10, 25, paint);
        canvas.drawText("LIFE: " + this.heroAircraft.getHp(), 10, 47, paint);
    }
}
