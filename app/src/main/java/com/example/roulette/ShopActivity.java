package com.example.roulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;

public class ShopActivity extends AppCompatActivity {
    private RewardedAd rewardedAd;
    private User user;

    private TextView text;
    private Toast toast;

    private boolean pressedButton;

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

        setContentView(R.layout.activity_shop);
        user = new User().loggingIn(this);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                findViewById(R.id.custom_toast_container));

        text = layout.findViewById(R.id.text);
        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);


        ImageButton freeChipButton = findViewById(R.id.freeChipButton);
        freeChipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pressedButton){
                    if(user.isSounds())
                        MainActivity.buttonPlayer.start();


                    text.setText("The ad is loading");
                    toast.show();

                    rewardedAd = createAndLoadRewardedAd();
                    pressedButton = true;
                }
            }
        });
    }

    private RewardedAd createAndLoadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        text.setText("Currently unavailable");
                        toast.show();
                        System.out.println("LOAD AD ERROR: " + loadAdError);

                        rewardedAd = null;

                        pressedButton = false;
                    }
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        toast.show();
                        rewardedAd = ad;
                        showAd();

                        pressedButton = false;
                    }
                });

        return rewardedAd;
    }
    private void showAd() {
        if (rewardedAd != null) {
            Activity activityContext = ShopActivity.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int rewardAmount = rewardItem.getAmount();
                    Intent intent = new Intent(ShopActivity.this, RouletteActivity.class);
                    intent.putExtra("sum", rewardAmount);
                    startActivity(intent);
                }
            });
        }
    }

    public void onBack(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        onBackPressed();
    }
}