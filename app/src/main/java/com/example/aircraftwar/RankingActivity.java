package com.example.aircraftwar;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.aircraftwar.ranking.RankingService;
import com.example.aircraftwar.ranking.ScoreRecord;

import java.util.List;

/**
 * 排行榜页面（实验四）：
 * <ul>
 *   <li>简单/普通/困难三个难度各有独立榜单，顶部按钮切换；</li>
 *   <li>ListView 纵向排列记录，第一名高亮显示；</li>
 *   <li>自定义 BaseAdapter（视图复用 + ViewHolder）管理数据，notifyDataSetChanged 刷新视图；</li>
 *   <li>点击行末删除按钮，同步移除数据源并刷新列表；</li>
 *   <li>“添加记录”按钮与游戏结束保存共用 RankingService（底层 SQLite，重开 App 依然保留）。</li>
 * </ul>
 *
 * @author hitsz
 */
public class RankingActivity extends AppCompatActivity {

    /** 从游戏结算跳转时携带的难度（默认简单） */
    public static final String EXTRA_DIFFICULTY = "difficulty";

    private RankingService rankingService;
    private RankAdapter adapter;

    private int currentDifficulty = ScoreRecord.DIFFICULTY_EASY;
    private Button btnEasy;
    private Button btnNormal;
    private Button btnHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankingService = new RankingService(this);

        btnEasy = findViewById(R.id.btn_easy);
        btnNormal = findViewById(R.id.btn_normal);
        btnHard = findViewById(R.id.btn_hard);

        btnEasy.setOnClickListener(v -> switchDifficulty(ScoreRecord.DIFFICULTY_EASY));
        btnNormal.setOnClickListener(v -> switchDifficulty(ScoreRecord.DIFFICULTY_NORMAL));
        btnHard.setOnClickListener(v -> switchDifficulty(ScoreRecord.DIFFICULTY_HARD));

        // 找到 ListView -> 初始化数据源 -> 创建 Adapter -> 绑定到 ListView
        ListView listView = findViewById(R.id.lv_rank);
        adapter = new RankAdapter();
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.tv_empty));

        findViewById(R.id.btn_add).setOnClickListener(view -> showAddDialog());

        // 从游戏结算跳转时，直接展示对应难度的榜单
        int difficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, ScoreRecord.DIFFICULTY_EASY);
        switchDifficulty(difficulty);
    }

    /** 切换当前难度榜单：更新按钮选中样式并刷新列表 */
    private void switchDifficulty(int difficulty) {
        currentDifficulty = difficulty;

        int gold = ContextCompat.getColor(this, R.color.gold);
        int white = ContextCompat.getColor(this, R.color.white);

        styleModeButton(btnEasy, difficulty == ScoreRecord.DIFFICULTY_EASY, gold, white);
        styleModeButton(btnNormal, difficulty == ScoreRecord.DIFFICULTY_NORMAL, gold, white);
        styleModeButton(btnHard, difficulty == ScoreRecord.DIFFICULTY_HARD, gold, white);

        refresh();
    }

    private void styleModeButton(Button button, boolean selected, int gold, int white) {
        button.setTextColor(selected ? gold : white);
        button.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
    }

    /** 数据变化后重新拉取数据源并通知列表刷新 */
    private void refresh() {
        adapter.setRecords(rankingService.getRankingList(currentDifficulty));
        adapter.notifyDataSetChanged();
    }

    private static String difficultyName(int difficulty) {
        switch (difficulty) {
            case ScoreRecord.DIFFICULTY_NORMAL:
                return "普通模式";
            case ScoreRecord.DIFFICULTY_HARD:
                return "困难模式";
            case ScoreRecord.DIFFICULTY_EASY:
            default:
                return "简单模式";
        }
    }

    /** “＋ 添加记录”对话框：手动输入姓名与分数，计入当前难度榜单 */
    private void showAddDialog() {
        EditText nameInput = new EditText(this);
        nameInput.setHint("玩家姓名");
        nameInput.setText("Player");

        EditText scoreInput = new EditText(this);
        scoreInput.setHint("分数");
        scoreInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, padding / 2);
        container.addView(nameInput);
        container.addView(scoreInput);

        new AlertDialog.Builder(this)
                .setTitle("添加记录（" + difficultyName(currentDifficulty) + "）")
                .setView(container)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        name = "Player";
                    }
                    int score = 0;
                    String scoreText = scoreInput.getText().toString().trim();
                    if (!scoreText.isEmpty()) {
                        try {
                            score = Integer.parseInt(scoreText);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    rankingService.addScore(currentDifficulty, name, score);
                    refresh();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /** 自定义适配器：继承 BaseAdapter，重写 4 个抽象方法 */
    private class RankAdapter extends BaseAdapter {

        private List<ScoreRecord> records = List.of();

        void setRecords(List<ScoreRecord> records) {
            this.records = records;
        }

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            // 视图复用：convertView 非空时直接复用，降低内存占用
            if (convertView == null) {
                convertView = LayoutInflater.from(RankingActivity.this)
                        .inflate(R.layout.item_rank, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScoreRecord record = records.get(position);
            holder.bind(record, position);
            return convertView;
        }
    }

    /** 内部类缓存行控件，避免每次 getView 都 findViewById */
    private class ViewHolder {

        private final View itemRoot;
        private final TextView tvRank;
        private final TextView tvName;
        private final TextView tvScore;
        private final Button btnDelete;

        ViewHolder(View itemView) {
            itemRoot = itemView;
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(ScoreRecord record, int position) {
            tvRank.setText(String.valueOf(position + 1));
            tvName.setText(record.getPlayerName());
            tvScore.setText(String.valueOf(record.getScore()));

            // 第一名高亮显示
            boolean isTop = (position == 0);
            int gold = ContextCompat.getColor(RankingActivity.this, R.color.gold);
            int white = ContextCompat.getColor(RankingActivity.this, R.color.white);
            tvRank.setTextColor(isTop ? gold : white);
            tvName.setTextColor(isTop ? gold : white);
            tvScore.setTextColor(isTop ? gold : white);
            tvRank.setTypeface(null, isTop ? Typeface.BOLD : Typeface.NORMAL);
            itemRoot.setBackgroundColor(
                    isTop ? ContextCompat.getColor(RankingActivity.this, R.color.gold_transparent)
                            : android.graphics.Color.TRANSPARENT);

            // 绑定行内控件交互事件：点击删除按钮，同步移除数据源并刷新列表
            btnDelete.setOnClickListener(view -> {
                rankingService.deleteRecord(record);
                refresh();
            });
        }
    }
}
