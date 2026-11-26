package com.example.aircraftwar.ranking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite 数据库存储实现：与文件实现对外提供同一套 RankingDao 接口，
 * 页面与 Adapter 无需改动（面向接口编程，见 RankingDaoFactory）。
 * <p>
 * 数据库文件位于应用沙箱 databases/rank.db，默认私有。
 *
 * @author hitsz
 */
public class RankingDaoSqliteImpl implements RankingDao {

    private final RankDbHelper helper;

    public RankingDaoSqliteImpl(Context context) {
        helper = new RankDbHelper(context.getApplicationContext());
    }

    @Override
    public List<ScoreRecord> getAllRecords() {
        List<ScoreRecord> records = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                RankDbHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                RankDbHelper.COL_SCORE + " DESC, " + RankDbHelper.COL_ID + " DESC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(RankDbHelper.COL_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(RankDbHelper.COL_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(RankDbHelper.COL_SCORE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(RankDbHelper.COL_TIME));
            records.add(new ScoreRecord(id, name, score, time));
        }

        // 遍历取值后必须 close()
        cursor.close();
        return records;
    }

    @Override
    public void addRecord(ScoreRecord record) {
        ContentValues values = new ContentValues();
        values.put(RankDbHelper.COL_NAME, record.getPlayerName());
        values.put(RankDbHelper.COL_SCORE, record.getScore());
        values.put(RankDbHelper.COL_TIME, record.getTime());

        helper.getWritableDatabase().insert(RankDbHelper.TABLE_NAME, null, values);
    }

    @Override
    public void deleteRecord(ScoreRecord record) {
        SQLiteDatabase db = helper.getWritableDatabase();

        if (record.getId() >= 0) {
            // 数据库记录带行号，按 _id 精确删除
            db.delete(RankDbHelper.TABLE_NAME,
                    RankDbHelper.COL_ID + "=?",
                    new String[]{String.valueOf(record.getId())});
        } else {
            // 无行号时按业务字段匹配删除
            db.delete(RankDbHelper.TABLE_NAME,
                    RankDbHelper.COL_NAME + "=? AND "
                            + RankDbHelper.COL_SCORE + "=? AND "
                            + RankDbHelper.COL_TIME + "=?",
                    new String[]{
                            record.getPlayerName(),
                            String.valueOf(record.getScore()),
                            record.getTime()
                    });
        }
    }

    /**
     * 数据库创建与版本升级管理。
     */
    public static class RankDbHelper extends SQLiteOpenHelper {

        static final String DB_NAME = "rank.db";
        static final int DB_VERSION = 1;
        static final String TABLE_NAME = "rank";
        static final String COL_ID = "_id";
        static final String COL_NAME = "name";
        static final String COL_SCORE = "score";
        static final String COL_TIME = "time";

        public RankDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        /** 首次创建数据库时调用，执行建表 SQL */
        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT, "
                    + COL_SCORE + " INTEGER, "
                    + COL_TIME + " TEXT)";
            db.execSQL(sql);
        }

        /** 数据库版本升级时调用 */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
