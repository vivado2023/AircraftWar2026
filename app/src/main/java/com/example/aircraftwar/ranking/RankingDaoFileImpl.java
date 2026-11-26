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
 * 文件存储实现：沿用 Windows 版 RankingDaoImpl 的 CSV 读写逻辑，
 * 文件放在应用沙箱 getFilesDir() 下（不硬编码路径，卸载即清除）。
 *
 * @author hitsz
 */
public class RankingDaoFileImpl implements RankingDao {

    private static final String TAG = "AircraftWar";

    private final File file;
    private final List<ScoreRecord> records = new ArrayList<>();

    public RankingDaoFileImpl(Context context) {
        file = new File(context.getApplicationContext().getFilesDir(), "ranking.txt");
        load();
    }

    @Override
    public synchronized List<ScoreRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    @Override
    public synchronized void addRecord(ScoreRecord record) {
        records.add(record);
        records.sort(Comparator.comparingInt(ScoreRecord::getScore).reversed());
        save();
    }

    @Override
    public synchronized void deleteRecord(ScoreRecord record) {
        records.removeIf(r -> r.getPlayerName().equals(record.getPlayerName())
                && r.getScore() == record.getScore()
                && r.getTime().equals(record.getTime()));
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (ScoreRecord record : records) {
                writer.write(record.getPlayerName() + "," + record.getScore() + "," + record.getTime());
                writer.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "保存排行榜文件失败", e);
        }
    }

    private void load() {
        records.clear();

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    try {
                        records.add(new ScoreRecord(parts[0], Integer.parseInt(parts[1]), parts[2]));
                    } catch (NumberFormatException ignored) {
                        // 跳过格式错误的行
                    }
                }
            }
            records.sort(Comparator.comparingInt(ScoreRecord::getScore).reversed());
        } catch (IOException e) {
            Log.e(TAG, "读取排行榜文件失败", e);
        }
    }
}
