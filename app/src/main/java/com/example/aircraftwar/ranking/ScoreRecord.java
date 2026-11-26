package com.example.aircraftwar.ranking;

/**
 * 单条排行榜记录：玩家名、分数、时间（照搬 Windows 版 ScoreRecord）。
 * <p>
 * id 用于 SQLite 实现精确定位数据库行；文件实现中为 -1（无行号）。
 *
 * @author hitsz
 */
public class ScoreRecord {

    private final long id;
    private final String playerName;
    private final int score;
    private final String time;

    public ScoreRecord(String playerName, int score, String time) {
        this(-1L, playerName, score, time);
    }

    public ScoreRecord(long id, String playerName, int score, String time) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return playerName + "," + score + "," + time;
    }
}
