package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.factory.BossEnemyFactory;
import com.example.aircraftwar.observer.PropEffectSubject;
import com.example.aircraftwar.prop.FirePowerManager;
import com.example.aircraftwar.prop.Tool;
import com.example.aircraftwar.prop.PropEffectHandler;
import com.example.aircraftwar.prop.ToolFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 游戏主面板：Windows 版 Game（JPanel + Timer）迁移为 Android 的 SurfaceView。
 * <ul>
 *   <li>实现 SurfaceHolder.Callback 与 Runnable，run() 中 lockCanvas → 绘制 → unlockCanvasAndPost；</li>
 *   <li>surfaceCreated 一次性加载并缓存图片，启动绘制线程；</li>
 *   <li>surfaceChanged 按真实屏幕尺寸换算缩放与偏移，不写死分辨率；</li>
 *   <li>surfaceDestroyed 置停止标志并等待线程结束；</li>
 *   <li>触屏事件（onTouchEvent）替代 Windows 版 HeroController 的鼠标拖动。</li>
 * </ul>
 * 模板模式：action() 固定游戏主循环骨架，简单/普通/困难三个子类只重写差异步骤。
 *
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable, PropEffectHandler {
    public enum GameEvent {
        BULLET_HIT, BOMB, SUPPLY, BOSS_STARTED, BOSS_ENDED, GAME_OVER
    }

    public interface EventListener {
        void onGameEvent(GameEvent event, int score);
    }

    private volatile EventListener eventListener;
    private boolean bossActiveLastFrame;

    public void setEventListener(EventListener listener) {
        eventListener = listener;
    }

    private void reportEvent(GameEvent event) {
        EventListener listener = eventListener;
        if (listener != null) {
            listener.onGameEvent(event, score);
        }
    }

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
    private int enemySpawnCounter = 0;

    protected double heroShootCycle = 15;
    protected double enemyShootCycle = 15;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;

    protected double enemySpeedRate = 1.0;
    protected double enemyHpRate = 1.0;
    protected int bossHp = 300;

    protected int score = 0;
    protected int nextBossScore = 200;

    private int difficultyCounter = 0;
    private boolean gameOverFlag = false;

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
        ImageManager.init(getContext().getApplicationContext());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        float scale = width / (float) GameConfig.WINDOW_WIDTH;
        GameConfig.SCALE = scale;
        GameConfig.WINDOW_HEIGHT = (int) Math.ceil(height / (double) scale);
        GameConfig.OFFSET_X = 0f;
        GameConfig.OFFSET_Y = 0f;

        if (backgroundImage == null) {
            heroAircraft.init(
                    GameConfig.WINDOW_WIDTH / 2,
                    GameConfig.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0, 0, 300, 300
            );
            backgroundImage = getBackgroundImage();
            initDifficultyParameters();
        }

        if (drawThread == null) {
            isRunning = true;
            drawThread = new Thread(this, "game-draw-thread");
            drawThread.start();
        }
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

            if (!gameOverFlag) {
                // 只负责「算」和「画」，不碰任何 UI 控件
                action();
            }
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

    // ==================== 模板方法：固定主循环流程 ====================

    public final void action() {
        enemySpawnAction();
        shootAction();
        bulletsMoveAction();
        aircraftsMoveAction();
        propsMoveAction();
        crashCheckAction();
        postProcessAction();
        difficultyIncreaseAction();
        checkResultAction();

        if (!gameOverFlag) {
            boolean bossActive = bossExists();
            if (bossActive != bossActiveLastFrame) {
                bossActiveLastFrame = bossActive;
                reportEvent(bossActive ? GameEvent.BOSS_STARTED : GameEvent.BOSS_ENDED);
            }
        }
    }

    protected abstract Bitmap getBackgroundImage();

    protected abstract void initDifficultyParameters();

    protected abstract AbstractAircraft createEnemy(int x, int y);

    protected abstract boolean canGenerateBoss();

    protected abstract void onBossGenerated(BossEnemy bossEnemy);

    protected abstract void increaseDifficulty();

    // ==================== 生成 / 射击 / 移动 ====================

    private void enemySpawnAction() {
        enemySpawnCounter++;

        if (enemySpawnCounter < enemySpawnCycle) {
            return;
        }

        enemySpawnCounter = 0;

        if (enemyAircrafts.size() < enemyMaxNumber) {
            int x = (int) (Math.random() * (GameConfig.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
            int y = (int) (Math.random() * GameConfig.WINDOW_HEIGHT * 0.05);
            enemyAircrafts.add(createEnemy(x, y));
        }

        if (canGenerateBoss() && score >= nextBossScore && !bossExists()) {
            BossEnemy bossEnemy = (BossEnemy) new BossEnemyFactory().createEnemy(
                    GameConfig.WINDOW_WIDTH / 2,
                    50,
                    3,
                    0,
                    bossHp
            );

            enemyAircrafts.add(bossEnemy);

            onBossGenerated(bossEnemy);

            nextBossScore += 200;

            Log.d(TAG, "Boss 敌机生成，血量=" + bossHp);
        }
    }

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

    private void difficultyIncreaseAction() {
        difficultyCounter++;

        if (difficultyCounter >= 500) {
            difficultyCounter = 0;
            increaseDifficulty();
        }
    }

    private void shootAction() {
        heroShootCounter++;

        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        enemyShootCounter++;

        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;

            for (AbstractAircraft enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }

        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (Tool prop : props) {
            prop.forward();
        }
    }

    private boolean bossExists() {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof BossEnemy && !enemy.notValid()) {
                return true;
            }
        }

        return false;
    }

    // ==================== 道具效果（观察者模式） ====================

    /**
     * 观察者模式：炸弹道具触发后，通知所有敌机和敌方子弹。
     */
    public void activateBomb() {
        PropEffectSubject subject = new PropEffectSubject();

        for (AbstractAircraft enemy : enemyAircrafts) {
            subject.addObserver(enemy);
        }

        for (BaseBullet bullet : enemyBullets) {
            subject.addObserver(bullet);
        }

        int beforeKilled = countInvalidEnemies();

        subject.notifyBomb();

        int afterKilled = countInvalidEnemies();

        int killedByBomb = Math.max(0, afterKilled - beforeKilled);

        score += killedByBomb * 10;

        Log.d(TAG, "炸弹道具生效，击毁敌机数量：" + killedByBomb
                + "，获得分数：" + (killedByBomb * 10));
    }

    /**
     * 观察者模式：冰冻道具触发后，通知所有敌机和敌方子弹。
     */
    public void activateFreeze() {
        PropEffectSubject subject = new PropEffectSubject();

        for (AbstractAircraft enemy : enemyAircrafts) {
            subject.addObserver(enemy);
        }

        for (BaseBullet bullet : enemyBullets) {
            subject.addObserver(bullet);
        }

        subject.notifyFreeze();

        Log.d(TAG, "冰冻道具生效，已通知所有可响应对象");
    }

    private int countInvalidEnemies() {
        int count = 0;

        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.notValid()) {
                count++;
            }
        }

        return count;
    }

    // ==================== 碰撞检测 ====================

    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }

            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                reportEvent(GameEvent.BULLET_HIT);
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }

            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }

                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    reportEvent(GameEvent.BULLET_HIT);

                    if (enemyAircraft.notValid()) {
                        score += 10;
                        dropPropsAfterKilled(enemyAircraft);
                    }
                }

                if (!enemyAircraft.notValid()
                        && (enemyAircraft.crash(heroAircraft)
                        || heroAircraft.crash(enemyAircraft))) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        for (Tool prop : props) {
            if (prop.notValid()) {
                continue;
            }

            if (heroAircraft.crash(prop)) {
                prop.activate(heroAircraft, this);
                prop.vanish();

                if (prop instanceof com.example.aircraftwar.prop.BoomTool) {
                    reportEvent(GameEvent.BOMB);
                } else {
                    reportEvent(GameEvent.SUPPLY);
                }
            }
        }
    }

    private void dropPropsAfterKilled(AbstractAircraft enemyAircraft) {
        if (enemyAircraft instanceof BossEnemy) {
            for (int i = 0; i < 3; i++) {
                int typeCount = 5;

                Tool prop = ToolFactory.createTool(
                        enemyAircraft.getLocationX() + (i - 1) * 40,
                        enemyAircraft.getLocationY(),
                        typeCount
                );

                if (prop != null) {
                    props.add(prop);
                }
            }
        } else {
            Tool prop = enemyAircraft.dropProp();

            if (prop != null) {
                props.add(prop);
            }
        }
    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    private void checkResultAction() {
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            gameOverFlag = true;

            FirePowerManager.shutdown();

            Log.d(TAG, "Game Over! 最终得分: " + score);
            reportEvent(GameEvent.GAME_OVER);
            // 实验三：通过 Handler/Message 切回主线程，跳转排行榜与输入姓名页面
        }
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
        int tileHeight = backgroundImage.getHeight();

        for (int y = backGroundTop - tileHeight;
             y < GameConfig.WINDOW_HEIGHT;
             y += tileHeight) {
            canvas.drawBitmap(backgroundImage, 0f, y, paint);
        }

        backGroundTop = (backGroundTop + 1) % tileHeight;

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

        if (gameOverFlag) {
            paintGameOver(canvas);
        }

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

    private void paintGameOver(Canvas canvas) {
        paint.setColor(Color.YELLOW);
        paint.setTextSize(48);

        String msg = "GAME OVER  score=" + score;
        canvas.drawText(
                msg,
                (GameConfig.WINDOW_WIDTH - paint.measureText(msg)) / 2f,
                GameConfig.WINDOW_HEIGHT / 2f,
                paint
        );
    }

    // ==================== 触屏：替代 Windows 版 HeroController ====================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) {
            // 屏幕像素 -> 逻辑坐标
            int x = (int) ((event.getX() - GameConfig.OFFSET_X) / GameConfig.SCALE);
            int y = (int) ((event.getY() - GameConfig.OFFSET_Y) / GameConfig.SCALE);

            if (x < 0 || x > GameConfig.WINDOW_WIDTH || y < 0 || y > GameConfig.WINDOW_HEIGHT) {
                // 防止超出边界（与 Windows 版 HeroController 一致）
                return true;
            }

            heroAircraft.setLocation(x, y);
            return true;
        }

        return super.onTouchEvent(event);
    }
}
