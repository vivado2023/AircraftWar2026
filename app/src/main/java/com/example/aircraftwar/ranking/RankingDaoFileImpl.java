package com.example.aircraftwar.ranking;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 文件存储实现：沿用 Windows 版 RankingDaoImpl 的 CSV 读写逻辑。
 * <p>
 * 三个难度分别存一个文件（ranking_easy.txt / ranking_normal.txt / ranking_hard.txt），
 * 文件放在应用沙箱 getFilesDir() 下（不硬编码路径，卸载即清除）。
 *
 * @author hitsz
 */
public class RankingDaoFileImpl implements RankingDao {

    private static final String TAG = "AircraftWar";

    private final Context context;

    public RankingDaoFileImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    private File fileFor(int difficulty) {
        String name;
        switch (difficulty) {
            case ScoreRecord.DIFFICULTY_NORMAL:
                name = "ranking_normal.txt";
                break;
            case ScoreRecord.DIFFICULTY_HARD:
                name = "ranking_hard.txt";
                break;
            case ScoreRecord.DIFFICULTY_EASY:
            default:
                name = "ranking_easy.txt";
                break;
        }
        return new File(context.getFilesDir(), name);
    }

    @Override
    public synchronized List<ScoreRecord> getAllRecords(int difficulty) {
        List<ScoreRecord> records = new ArrayList<>();

        File file = fileFor(difficulty);
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    try {
                        records.add(new ScoreRecord(
                                difficulty, parts[0], Integer.parseInt(parts[1]), parts[2]));
                    } catch (NumberFormatException ignored) {
                        // 跳过格式错误的行
                    }
                }
            }
            records.sort(Comparator.comparingInt(ScoreRecord::getScore).reversed());
        } catch (IOException e) {
            Log.e(TAG, "读取排行榜文件失败", e);
        }
        return records;
    }

    @Override
    public synchronized void addRecord(ScoreRecord record) {
        List<ScoreRecord> records = getAllRecords(record.getDifficulty());
        records.add(record);
        records.sort(Comparator.comparingInt(ScoreRecord::getScore).reversed());
        save(record.getDifficulty(), records);
    }

    @Override
    public synchronized void deleteRecord(ScoreRecord record) {
        List<ScoreRecord> records = getAllRecords(record.getDifficulty());
        records.removeIf(r -> r.getPlayerName().equals(record.getPlayerName())
                && r.getScore() == record.getScore()
                && r.getTime().equals(record.getTime()));
        save(record.getDifficulty(), records);
    }

    private void save(int difficulty, List<ScoreRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileFor(difficulty)), StandardCharsets.UTF_8))) {
            for (ScoreRecord record : records) {
                writer.write(record.getPlayerName() + "," + record.getScore() + "," + record.getTime());
                writer.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "保存排行榜文件失败", e);
        }
    }
}
