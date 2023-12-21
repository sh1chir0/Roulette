package com.example.roulette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_settings);
        user = new User().loggingIn(this);

        Switch musicSwitch = findViewById(R.id.turnMusic);
        musicSwitch.setChecked(user.isMusic());

        Switch soundSwitch = findViewById(R.id.turnSounds);
        soundSwitch.setChecked(user.isSounds());
    }

    public void onTurnMusic(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        user.setMusic(((Switch) view).isChecked());
    }
    public void onTurnSound(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        user.setSounds(((Switch) view).isChecked());
    }

    public void onBack(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        onBackPressed();
    }
    @Override
    protected void onPause(){
        user.saveUser(user, this);
        super.onPause();
    }
    @Override
    protected void onStop(){
        user.saveUser(user, this);
        super.onStop();
    }
    @Override
    public void onBackPressed(){
        user.saveUser(user, this);
        super.onBackPressed();
    }
}