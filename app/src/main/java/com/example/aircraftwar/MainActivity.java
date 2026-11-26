package com.example.aircraftwar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat audioSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioSwitch = findViewById(R.id.switch_audio);

        findViewById(R.id.btn_easy).setOnClickListener(view -> openGame(0));
        findViewById(R.id.btn_normal).setOnClickListener(view -> openGame(1));
        findViewById(R.id.btn_hard).setOnClickListener(view -> openGame(2));
    }

    private void openGame(int difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_DIFFICULTY, difficulty);
        intent.putExtra(GameActivity.EXTRA_AUDIO_ENABLED, audioSwitch.isChecked());
        startActivity(intent);
    }
}