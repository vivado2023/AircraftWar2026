package com.example.aircraftwar.ranking;

import android.content.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 排行榜业务层：页面只依赖本类与 DAO 接口，不感知底层存储实现（数据视图分离）。
 * <p>
 * 三个难度（简单/普通/困难）的排行榜分开存储与查询。
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

    /** 保存一条成绩到指定难度的排行榜：记录当前时间并写入存储 */
    public void addScore(int difficulty, String playerName, int score) {
        String time = LocalDateTime.now().format(TIME_FORMAT);
        rankingDao.addRecord(new ScoreRecord(difficulty, playerName, score, time));
    }

    /** 获取指定难度按分数从高到低排列的榜单 */
    public List<ScoreRecord> getRankingList(int difficulty) {
        return rankingDao.getAllRecords(difficulty);
    }

    /** 删除一条记录（按记录所属难度定位存储） */
    public void deleteRecord(ScoreRecord record) {
        rankingDao.deleteRecord(record);
    }
}
