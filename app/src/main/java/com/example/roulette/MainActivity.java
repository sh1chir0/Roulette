package com.example.roulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    public static MediaPlayer buttonPlayer;
    private MediaPlayer musicPlayer;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );


        setContentView(R.layout.activity_main);

        user = new User().loggingIn(this);
        buttonPlayer = MediaPlayer.create(this, R.raw.button);
        buttonPlayer.setVolume(1f, 1f);

        musicPlayer = MediaPlayer.create(this, R.raw.splish_splash);
        musicPlayer.setVolume(0.2f, 0.2f);
        musicPlayer.setLooping(true);
        if(user.isMusic())
            musicPlayer.start();
    }

    public void onRouletteActivity(View view){
        if(user.isSounds())
            buttonPlayer.start();

        Intent intent = new Intent(this, RouletteActivity.class);
        startActivity(intent);
    }
    public void onSettings(View view){
        if(user.isSounds())
            buttonPlayer.start();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void onExit(View view){
        if(user.isSounds())
            buttonPlayer.start();
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonPlayer != null) {
            buttonPlayer.release();
        }
        if (musicPlayer != null) {
            musicPlayer.release();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        musicPlayer.pause();
    }

    @Override
    protected void onResume(){
        user = new User().loggingIn(this);
        super.onResume();
        if(user.isMusic())
            musicPlayer.start();
    }

}