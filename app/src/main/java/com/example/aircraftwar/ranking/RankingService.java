package com.example.aircraftwar.ranking;

import android.content.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 排行榜业务层：页面只依赖本类与 DAO 接口，不感知底层存储实现（数据视图分离）。
 *
 * @author hitsz
 */
public class RankingService {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RankingDao rankingDao;

    public RankingService(Context context) {
        this.rankingDao = RankingDaoFactory.create(context);
    }

    /** 保存一条成绩：记录当前时间并写入存储 */
    public void addScore(String playerName, int score) {
        String time = LocalDateTime.now().format(TIME_FORMAT);
        rankingDao.addRecord(new ScoreRecord(playerName, score, time));
    }

    /** 获取按分数从高到低排列的榜单 */
    public List<ScoreRecord> getRankingList() {
        return rankingDao.getAllRecords();
    }

    /** 删除一条记录 */
    public void deleteRecord(ScoreRecord record) {
        rankingDao.deleteRecord(record);
    }
}
