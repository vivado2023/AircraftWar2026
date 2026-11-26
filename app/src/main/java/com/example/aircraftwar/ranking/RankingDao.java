package com.example.aircraftwar.ranking;

import java.util.List;

/**
 * 排行榜数据访问接口（沿用 Windows 版 RankingDao 的职责）。
 * <p>
 * 实验四：文件存储与 SQLite 数据库存储对外提供同一套 DAO 接口，
 * 只需替换实现类，页面与 Adapter 无需改动——面向接口编程。
 *
 * @author hitsz
 */
public interface RankingDao {

    /** 按分数从高到低返回全部记录 */
    List<ScoreRecord> getAllRecords();

    /** 新增一条记录（实现内部负责排序与持久化） */
    void addRecord(ScoreRecord record);

    /** 删除指定记录 */
    void deleteRecord(ScoreRecord record);
}
