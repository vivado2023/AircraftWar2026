package com.example.aircraftwar.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.aircraftwar.R;

import java.util.HashSet;
import java.util.Set;

public final class GameAudio {

    private final MediaPlayer normalMusic;
    private final MediaPlayer bossMusic;
    private final SoundPool soundPool;
    private final Set<Integer> loadedSounds = new HashSet<>();

    private final int hitSound;
    private final int supplySound;
    private final int bombSound;
    private final int gameOverSound;

    private MediaPlayer playingMusic;
    private boolean foreground;
    private boolean bossActive;
    private boolean gameEnded;

    public GameAudio(Context context) {
        normalMusic = createLoop(context, R.raw.bgm);
        bossMusic = createLoop(context, R.raw.bgm_boss);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(4)
                .build();

        soundPool.setOnLoadCompleteListener((pool, soundId, status) -> {
            if (status == 0) {
                loadedSounds.add(soundId);
            }
        });

        hitSound = soundPool.load(context, R.raw.bullet_hit, 1);
        supplySound = soundPool.load(context, R.raw.get_supply, 1);
        bombSound = soundPool.load(context, R.raw.bomb_explosion, 1);
        gameOverSound = soundPool.load(context, R.raw.game_over, 1);
    }

    private static MediaPlayer createLoop(Context context, int resourceId) {
        MediaPlayer player = MediaPlayer.create(context, resourceId);
        if (player != null) {
            player.setLooping(true);
        }
        return player;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
        if (!foreground) {
            soundPool.autoPause();
        }
        updateMusic();
    }

    public void setBossActive(boolean bossActive) {
        this.bossActive = bossActive;
        updateMusic();
    }

    private void updateMusic() {
        MediaPlayer nextMusic = null;
        if (foreground && !gameEnded) {
            nextMusic = bossActive ? bossMusic : normalMusic;
        }

        if (playingMusic == nextMusic) {
            return;
        }

        if (playingMusic != null) {
            playingMusic.pause();
        }
        playingMusic = nextMusic;
        if (playingMusic != null) {
            playingMusic.start();
        }
    }

    public void playHit() {
        play(hitSound);
    }

    public void playSupply() {
        play(supplySound);
    }

    public void playBomb() {
        play(bombSound);
    }

    public void finishGame() {
        if (gameEnded) {
            return;
        }
        gameEnded = true;
        updateMusic();
        play(gameOverSound);
    }

    private void play(int soundId) {
        if (foreground && loadedSounds.contains(soundId)) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        }
    }

    public void release() {
        soundPool.release();
        if (normalMusic != null) {
            normalMusic.release();
        }
        if (bossMusic != null) {
            bossMusic.release();
        }
        playingMusic = null;
    }
}