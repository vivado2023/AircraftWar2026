package com.example.aircraftwar;

import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

import com.example.aircraftwar.music.GameAudio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.application.BaseGame;
import com.example.aircraftwar.application.EasyGame;
import com.example.aircraftwar.application.HardGame;
import com.example.aircraftwar.application.NormalGame;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_AUDIO_ENABLED = "audio_enabled";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private BaseGame gameView;
    private GameAudio audio;

    private boolean resumed;
    private boolean resultShown;
    private Integer finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int difficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, 0);
        BaseGame game;
        switch (difficulty) {
            case 1:
                game = new NormalGame(this);
                break;
            case 2:
                game = new HardGame(this);
                break;
            default:
                game = new EasyGame(this);
                break;
        }

        boolean audioEnabled = getIntent().getBooleanExtra(EXTRA_AUDIO_ENABLED, true);
        if (audioEnabled) {
            audio = new GameAudio(this);
        }

        gameView = game;
        gameView.setEventListener((event, score) ->
                mainHandler.post(() -> handleGameEvent(event, score)));

        setContentView(gameView);
    }

    private void handleGameEvent(BaseGame.GameEvent event, int score) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        switch (event) {
            case BULLET_HIT:
                if (audio != null) {
                    audio.playHit();
                }
                break;
            case BOMB:
                if (audio != null) {
                    audio.playBomb();
                }
                break;
            case SUPPLY:
                if (audio != null) {
                    audio.playSupply();
                }
                break;
            case BOSS_STARTED:
            case BOSS_ENDED:
                if (audio != null) {
                    audio.setBossActive(event == BaseGame.GameEvent.BOSS_STARTED);
                }
                break;
            case GAME_OVER:
                finalScore = score;
                if (audio != null) {
                    audio.finishGame();
                }
                showResultIfReady();
                break;
        }
    }

    private void showResultIfReady() {
        if (!resumed || resultShown || finalScore == null || isFinishing()) {
            return;
        }

        resultShown = true;
        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("本局得分：" + finalScore)
                .setPositiveButton("返回菜单", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        if (audio != null) {
            audio.setForeground(true);
        }
        showResultIfReady();
    }

    @Override
    protected void onPause() {
        resumed = false;
        if (audio != null) {
            audio.setForeground(false);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (gameView != null) {
            gameView.setEventListener(null);
        }
        mainHandler.removeCallbacksAndMessages(null);
        if (audio != null) {
            audio.release();
            audio = null;
        }
        super.onDestroy();
    }
}