package com.example.aircraftwar.ranking;

/**
 * 单条排行榜记录：所属难度、玩家名、分数、时间（照搬 Windows 版 ScoreRecord 并扩展难度维度）。
 * <p>
 * 三个难度（0 简单 / 1 普通 / 2 困难）的排行榜分开存储与展示。
 * id 用于 SQLite 实现精确定位数据库行；文件实现中为 -1（无行号）。
 *
 * @author hitsz
 */
public class ScoreRecord {

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;

    private final long id;
    private final int difficulty;
    private final String playerName;
    private final int score;
    private final String time;

    public ScoreRecord(int difficulty, String playerName, int score, String time) {
        this(-1L, difficulty, playerName, score, time);
    }

    public ScoreRecord(long id, int difficulty, String playerName, int score, String time) {
        this.id = id;
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public int getDifficulty() {
        return difficulty;
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
