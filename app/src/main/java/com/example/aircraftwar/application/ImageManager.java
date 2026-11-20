package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.aircraftwar.R;
import com.example.aircraftwar.aircraft.AceEnemy;
import com.example.aircraftwar.aircraft.AdvancedEnemy;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.EliteEnemy;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.aircraft.MobEnemy;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;
import com.example.aircraftwar.prop.BoomTool;
import com.example.aircraftwar.prop.FireTool;
import com.example.aircraftwar.prop.FrozeTool;
import com.example.aircraftwar.prop.HpTool;
import com.example.aircraftwar.prop.StrongFireTool;

import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问。
 * <p>
 * Windows 版使用 ImageIO.read(new FileInputStream(...)) 读取 src/images 下的文件；
 * Android 版改为 BitmapFactory.decodeResource + res/drawable 资源 ID。
 * 图片在 surfaceCreated 中一次性加载并缓存，游戏循环里只取用，不重复 decode。
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BACKGROUND_EASY_IMAGE;
    public static Bitmap BACKGROUND_NORMAL_IMAGE;
    public static Bitmap BACKGROUND_HARD_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ADVANCED_ENEMY_IMAGE;
    public static Bitmap ACE_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap HP_PROP_IMAGE;
    public static Bitmap FIRE_PROP_IMAGE;
    public static Bitmap STRONG_FIRE_PROP_IMAGE;
    public static Bitmap BOOM_PROP_IMAGE;
    public static Bitmap FROZE_PROP_IMAGE;

    private static volatile boolean initialized = false;

    private ImageManager() {
    }

    /**
     * 加载并缓存全部图片。使用 applicationContext 避免持有 Activity 导致内存泄漏。
     */
    public static synchronized void init(Context context) {
        if (initialized) {
            return;
        }

        Context appContext = context.getApplicationContext();

        BACKGROUND_EASY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bg2);
        BACKGROUND_NORMAL_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bg);
        BACKGROUND_HARD_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bg3);

        HERO_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.hero);
        MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.mob);
        ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.elite);
        ADVANCED_ENEMY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.elite_plus);
        ACE_ENEMY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.elite_pro);
        BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.boss);
        HERO_BULLET_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bullet_hero);
        ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bullet_enemy);
        HP_PROP_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.prop_blood);
        FIRE_PROP_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.prop_bullet);
        STRONG_FIRE_PROP_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.prop_bullet_plus);
        BOOM_PROP_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.prop_bomb);
        FROZE_PROP_IMAGE = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.prop_freeze);

        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(AdvancedEnemy.class.getName(), ADVANCED_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(AceEnemy.class.getName(), ACE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HpTool.class.getName(), HP_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FireTool.class.getName(), FIRE_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(StrongFireTool.class.getName(), STRONG_FIRE_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BoomTool.class.getName(), BOOM_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FrozeTool.class.getName(), FROZE_PROP_IMAGE);

        initialized = true;
    }

    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }
}
