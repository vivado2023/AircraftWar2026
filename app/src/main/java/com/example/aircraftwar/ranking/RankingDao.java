package com.example.aircraftwar.ranking;

import java.util.List;

/**
 * 排行榜数据访问接口（沿用 Windows 版 RankingDao 的职责，增加难度维度）。
 * <p>
 * 三个难度（简单/普通/困难）的排行榜分开存储；文件存储与 SQLite 数据库存储
 * 对外提供同一套接口，只需替换实现类，页面与 Adapter 无需改动——面向接口编程。
 *
 * @author hitsz
 */
public interface RankingDao {

    /** 返回指定难度下按分数从高到低排列的全部记录 */
    List<ScoreRecord> getAllRecords(int difficulty);

    /** 新增一条记录（记录自带难度，实现内部负责排序与持久化） */
    void addRecord(ScoreRecord record);

    /** 删除指定记录 */
    void deleteRecord(ScoreRecord record);
}
