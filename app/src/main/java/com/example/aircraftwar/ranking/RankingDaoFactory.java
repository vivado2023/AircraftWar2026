package com.example.aircraftwar.ranking;

import android.content.Context;

/**
 * DAO 工厂：文件存储与 SQLite 存储对外提供同一套 RankingDao 接口。
 * <p>
 * 实验四第 4 步“改用 SQLite”只需替换 create() 返回的实现类，
 * 页面（RankingActivity）与 Adapter 无需任何改动——面向接口编程的价值。
 *
 * @author hitsz
 */
public final class RankingDaoFactory {

    /** 当前默认实现：SQLite 数据库存储（实验四第 4 步） */
    public static RankingDao create(Context context) {
        return new RankingDaoSqliteImpl(context);
    }

    /** 文件存储实现（实验四第 1~3 步使用，接口一致可直接替换） */
    public static RankingDao createFileBased(Context context) {
        return new RankingDaoFileImpl(context);
    }

    private RankingDaoFactory() {
    }
}
