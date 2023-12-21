package com.example.roulette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RouletteActivity extends AppCompatActivity {
    private static final float FACTOR = 4.86f;
    private User user;
    private TextView arrowTV;
    private ImageView rouletteIV;
    private boolean spin = false;
    private Random random;
    private int old_degree;
    private int degree;
    private ArrayList<Bet> bets;
    private ArrayList<ImageButton> betButtons;
    private ImageButton oneChipButton;
    private ImageButton fiveChipButton;
    private ImageButton twentyFiveChipButton;
    private ImageButton fiftyChipButton;
    private ImageButton oneHundredChipButton;
    private ConstraintLayout innerConstraintLayout;
    private ConstraintLayout constraintLayout;
    private TextView balanceTV;
    private TextView rateTV;
    private int rate;
    private int currentChip;
    private ImageButton currentChipButton;
    private int numbers[] = {32, 15, 19, 4, 21, 2,
            25, 17, 34, 6, 27, 13, 36, 11,
            30, 8, 23, 10, 5, 24, 16, 33,
            1, 20, 14, 31, 9, 22, 18, 29,
            7, 28, 12 , 35, 3, 26, 0};
    private MediaPlayer spinPlayer;
    private MediaPlayer winPlayer;
    private MediaPlayer losePlayer;
    private MediaPlayer chipPlayer;
    private MediaPlayer betPlayer;
    private MediaPlayer musicPlayer;

    private AdView mAdView;



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

        setContentView(R.layout.activity_roulette);
        user = new User().loggingIn(this);
        init();
        chipButtonsInit();
        betButtonsInit();
        adInit();
    }

    private void adInit(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mAdView = findViewById(R.id.adView);

                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });
    }




    @Override
    public void onBackPressed() {
        user.saveUser(user, this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onPause(){
        user.saveUser(user, this);
        super.onPause();

        if(musicPlayer.isPlaying()){
            musicPlayer.pause();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        user = new User().loggingIn(this);
        if(user.isMusic())
            musicPlayer.start();
    }
    @Override
    public void onStop(){
        user.saveUser(user, this);
        super.onStop();
    }
    @Override
    public void onDestroy(){
        user.saveUser(user, this);
        super.onDestroy();
        if (spinPlayer != null) {
            spinPlayer.release();
        }
        if(musicPlayer != null)
            musicPlayer.release();
    }

    public void onStart(View view){
        // spin anim
        if(!spin && !bets.isEmpty()){
            spin = true;
            old_degree = degree % 360;
            degree = random.nextInt(3600) + 720;

            RotateAnimation rotateAnimation = new RotateAnimation(old_degree, degree, RotateAnimation.RELATIVE_TO_SELF,
                    0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(3600);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    spinPlayer.stop();
                    spinPlayer.reset();
                    int result = getResult(360 - (degree % 360));
                    arrowTV.setText(String.valueOf(result));

                    int betSum = 0;
                    int winSum = 0;

                    for (Bet bet: bets) {
                        System.out.println(bet.toString());
                    }

                    for (int i = 0; i < bets.size(); i++) {
                        Bet bet = bets.get(i);
                        betSum += bet.getAmount();
                        int numBet = bet.getBet();
                        if(numBet == result && result <= 36){ // for nums 0-36
                            winSum += bet.getAmount()*bet.getCoefficient();

                        }else if(numBet > 36 && numBet <= 48){
                            int[] validResults = null;
                            switch(numBet){
                                case 37: validResults = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34}; // 2 to 1(1-34)
                                    break;
                                case 38: validResults = new int[]{2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35}; // 2 to 1(2-35)
                                    break;
                                case 39: validResults = new int[]{3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36}; // 2 to 1(3-36)
                                    break;
                                case 40: validResults = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}; // 1 st 12
                                    break;
                                case 41: validResults = new int[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}; // 2 nd 12
                                    break;
                                case 42: validResults = new int[]{25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}; // 3 rd 12
                                    break;
                                case 43: validResults = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}; // 1 to 18
                                    break;
                                case 44: validResults = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36}; // even
                                    break;
                                case 45: validResults = new int[]{1, 3, 5 , 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36}; // red
                                    break;
                                case 46: validResults = new int[]{2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 29, 28, 31, 33, 35}; // black
                                    break;
                                case 47: validResults = new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35}; // odd
                                    break;
                                case 48: validResults = new int[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}; // 19 to 36
                                    break;
                                }
                            if(Arrays.stream(validResults).anyMatch(element -> element == result)){
                                winSum += bet.getAmount()*bet.getCoefficient();
                            }
                        }
                    }

                    System.out.println("balance " + user.getBalance());
                    System.out.println("win sum " + winSum);
                    System.out.println("bet sum " + betSum);

                    resultEffects(winSum, betSum);

                    clearBets();
                    updateRate(0, "null");
                    spin = false;
                    currentChip = 0;
                    currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    currentChipButton = null;

                    spinPlayer = MediaPlayer.create(RouletteActivity.this, R.raw.spin_roullete);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });

            if(user.isSounds())
                spinPlayer.start();
            rouletteIV.startAnimation(rotateAnimation);
        }
    }

    public void resultEffects(int winSum, int betSum){
        Snackbar snackbar;

        if(winSum < betSum){
            snackbar = Snackbar.make(constraintLayout, "You lost $" + (winSum-betSum) * (-1) + ". Try again!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.loseColor));
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.loseTextColor));
            if(user.isSounds())
                losePlayer.start();
            snackbar.show();
        }else if(winSum == betSum){
            updateBalance(winSum, "plus");
            snackbar = Snackbar.make(constraintLayout, "You won't win anything. Try again!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.zeroTextColor));
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.loseTextColor));
            snackbar.show();
        }else{
            updateBalance(winSum, "plus");
            snackbar = Snackbar.make(constraintLayout, "You won $" + winSum + "! Congratulations!", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.winColor));
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.winTextColor));
            if(user.isSounds())
                winPlayer.start();
            snackbar.show();
        }
        System.out.println("balance after" + user.getBalance());
    }

    private void clearBets(){
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            innerConstraintLayout.removeView(bet.getChip());
        }
        bets.clear();
    }
    private int getResult(int newDegree){
        int text = 0;

        int factor_x = 1;
        int factor_y = 3;
        for(int i=0; i<37; i++){
            if(newDegree >= (FACTOR * factor_x) && newDegree < (FACTOR * factor_y)){
                text = numbers[i];
            }
            factor_x += 2;
            factor_y += 2;
        }
        if(newDegree >= (FACTOR * 73) && newDegree < 360 || newDegree >= 0 && newDegree < (FACTOR * 1)){
            text = numbers[numbers.length -1];
        }

        return text;
    }

    private void init(){
        currentChip = 0;
        old_degree = 0;
        degree = 0;
        rate = 0;
        balanceTV = findViewById(R.id.balanceTV);
        balanceTV.setText("$" + String.valueOf(user.getBalance()));
        arrowTV = findViewById(R.id.resultTV);
        rouletteIV = findViewById(R.id.roulette);
        rateTV = findViewById(R.id.rateTV);
        random = new Random();
        betButtons = new ArrayList<>();
        bets = new ArrayList<>();
        innerConstraintLayout = findViewById(R.id.innerConstraintLayout);
        constraintLayout = findViewById(R.id.constraintLayout);
        spinPlayer = MediaPlayer.create(this, R.raw.spin_roullete);
        winPlayer = MediaPlayer.create(this, R.raw.win);
        losePlayer = MediaPlayer.create(this, R.raw.lose);
        chipPlayer = MediaPlayer.create(this, R.raw.chip);
        betPlayer = MediaPlayer.create(this, R.raw.bet);
        musicPlayer = MediaPlayer.create(this, R.raw.back_roulette);
        musicPlayer.setVolume(0.3f, 0.3f);
        musicPlayer.setLooping(true);
        if(user.isMusic())
            musicPlayer.start();
    }
    private void chipButtonsInit(){
        oneChipButton = findViewById(R.id.oneChipButton);
        fiveChipButton = findViewById(R.id.fiveChipButton);
        twentyFiveChipButton = findViewById(R.id.twentyfiveChipButton);
        fiftyChipButton = findViewById(R.id.fiftyChipButton);
        oneHundredChipButton = findViewById(R.id.onehundredChipButton);

        View.OnClickListener chipButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isSounds())
                    chipPlayer.start();
                if (v.getId() == R.id.oneChipButton && currentChip != 1) {
                    if(currentChipButton != null)
                        currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    oneChipButton.setBackgroundColor(getColor(R.color.spin_button));
                    currentChip = 1;
                    currentChipButton = oneChipButton;
                }else if(v.getId() == R.id.oneChipButton && currentChip == 1) {
                    currentChip = 0;
                    oneChipButton.setBackgroundColor(Color.TRANSPARENT);
                }else if (v.getId() == R.id.fiveChipButton && currentChip != 5) {
                    if(currentChipButton != null)
                        currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    fiveChipButton.setBackgroundColor(getColor(R.color.spin_button));
                    currentChip = 5;
                    currentChipButton = fiveChipButton;
                }else if(v.getId() == R.id.fiveChipButton && currentChip == 5){
                    currentChip = 0;
                    fiveChipButton.setBackgroundColor(Color.TRANSPARENT);
                }else if (v.getId() == R.id.twentyfiveChipButton && currentChip != 25) {
                    if(currentChipButton != null)
                        currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    twentyFiveChipButton.setBackgroundColor(getColor(R.color.spin_button));
                    currentChip = 25;
                    currentChipButton = twentyFiveChipButton;
                }else if(v.getId() == R.id.twentyfiveChipButton && currentChip == 25){
                    currentChip = 0;
                    twentyFiveChipButton.setBackgroundColor(Color.TRANSPARENT);
                }else if (v.getId() == R.id.fiftyChipButton && currentChip != 50) {
                    if(currentChipButton != null)
                        currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    fiftyChipButton.setBackgroundColor(getColor(R.color.spin_button));
                    currentChip = 50;
                    currentChipButton = fiftyChipButton;
                }else if(v.getId() == R.id.fiftyChipButton && currentChip == 50){
                    currentChip = 0;
                    fiftyChipButton.setBackgroundColor(Color.TRANSPARENT);
                }else if (v.getId() == R.id.onehundredChipButton && currentChip != 100) {
                    if(currentChipButton != null)
                        currentChipButton.setBackgroundColor(Color.TRANSPARENT);
                    oneHundredChipButton.setBackgroundColor(getColor(R.color.spin_button));
                    currentChip = 100;
                    currentChipButton = oneHundredChipButton;
                }else if(v.getId() == R.id.onehundredChipButton && currentChip == 100){
                    currentChip = 0;
                    oneHundredChipButton.setBackgroundColor(Color.TRANSPARENT);
                }

            }
        };

        oneChipButton.setOnClickListener(chipButtonClickListener);
        fiveChipButton.setOnClickListener(chipButtonClickListener);
        twentyFiveChipButton.setOnClickListener(chipButtonClickListener);
        fiftyChipButton.setOnClickListener(chipButtonClickListener);
        oneHundredChipButton.setOnClickListener(chipButtonClickListener);
    }

    private void betButtonsInit(){
        betButtons.add(findViewById(R.id.zeroButton));
        betButtons.add(findViewById(R.id.oneButton));
        betButtons.add(findViewById(R.id.twoButton));
        betButtons.add(findViewById(R.id.threeButton));
        betButtons.add(findViewById(R.id.fourButton));
        betButtons.add(findViewById(R.id.fiveButton));
        betButtons.add(findViewById(R.id.sixButton));
        betButtons.add(findViewById(R.id.sevenButton));
        betButtons.add(findViewById(R.id.eightButton));
        betButtons.add(findViewById(R.id.nineButton));
        betButtons.add(findViewById(R.id.tenButton));
        betButtons.add(findViewById(R.id.elevenButton));
        betButtons.add(findViewById(R.id.twelveButton));
        betButtons.add(findViewById(R.id.thirteenButton));
        betButtons.add(findViewById(R.id.fourteenButton));
        betButtons.add(findViewById(R.id.fifteenButton));
        betButtons.add(findViewById(R.id.sixteenButton));
        betButtons.add(findViewById(R.id.seventeenButton));
        betButtons.add(findViewById(R.id.eighteenButton));
        betButtons.add(findViewById(R.id.nineteenButton));
        betButtons.add(findViewById(R.id.twentyButton));
        betButtons.add(findViewById(R.id.twentyoneButton));
        betButtons.add(findViewById(R.id.twentytwoButton));
        betButtons.add(findViewById(R.id.twentythreeButton));
        betButtons.add(findViewById(R.id.twentyfourButton));
        betButtons.add(findViewById(R.id.twentyfiveButton));
        betButtons.add(findViewById(R.id.twentysixButton));
        betButtons.add(findViewById(R.id.twentysevenButton));
        betButtons.add(findViewById(R.id.twentyeightButton));
        betButtons.add(findViewById(R.id.twentynineButton));
        betButtons.add(findViewById(R.id.thirtyButton));
        betButtons.add(findViewById(R.id.thirtyoneButton));
        betButtons.add(findViewById(R.id.thirtytwoButton));
        betButtons.add(findViewById(R.id.thirtythreeButton));
        betButtons.add(findViewById(R.id.thirtyfourButton));
        betButtons.add(findViewById(R.id.thirtyfiveButton));
        betButtons.add(findViewById(R.id.thirtysixButton));
        betButtons.add(findViewById(R.id.thirtysevenButton));
        betButtons.add(findViewById(R.id.thirtyeightButton));
        betButtons.add(findViewById(R.id.thirtynineButton));
        betButtons.add(findViewById(R.id.fortyButton));
        betButtons.add(findViewById(R.id.fortyoneButton));
        betButtons.add(findViewById(R.id.fortytwoButton));
        betButtons.add(findViewById(R.id.fortythreeButton));
        betButtons.add(findViewById(R.id.fortyfourButton));
        betButtons.add(findViewById(R.id.fortyfiveButton));
        betButtons.add(findViewById(R.id.fortysixButton));
        betButtons.add(findViewById(R.id.fortysevenButton));
        betButtons.add(findViewById(R.id.fortyeightButton));


        for (int i = 0; i < betButtons.size(); i++) {
            int index = i;
            betButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentChip != 0){
                        if(user.isSounds())
                            betPlayer.start();
                        if (bets.isEmpty()) {
                            createBet(index);
                        } else {
                            boolean isExistingBet = false;

                            for (int j = 0; j < bets.size(); j++) {
                                Bet bet = bets.get(j);

                                if (currentChip == 0 && bet.getBet() == index) {
                                    innerConstraintLayout.removeView(bet.getChip());
                                    updateRate(bet.getAmount(), "minus");
                                    updateBalance(bet.getAmount(), "plus");
                                    bets.remove(bet);
                                    isExistingBet = true;
                                    break;
                                } else if (currentChip != 0 && bet.getBet() == index && currentChip <= user.getBalance()) {
                                    bets.get(j).setAmount(bet.getAmount() + currentChip);

                                    switch (currentChip) {
                                        case 1:
                                            bets.get(j).getChip().setImageResource(R.drawable.chip_one);
                                            break;
                                        case 5:
                                            bets.get(j).getChip().setImageResource(R.drawable.chip_five);
                                            break;
                                        case 25:
                                            bets.get(j).getChip().setImageResource(R.drawable.chip_twentyfive);
                                            break;
                                        case 50:
                                            bets.get(j).getChip().setImageResource(R.drawable.chip_fifth);
                                            break;
                                        case 100:
                                            bets.get(j).getChip().setImageResource(R.drawable.chip_onehundred);
                                            break;
                                    }

                                    updateRate(currentChip, "plus");
                                    updateBalance(currentChip, "minus");
                                    isExistingBet = true;
                                    break;
                                }
                            }
                            if (!isExistingBet) {
                                createBet(index);
                            }
                        }

                        for (int i1 = 0; i1 < bets.size(); i1++) {
                            Log.d("TAG", bets.get(i1).toString());
                        }
                    }
                }
            });
        }
    }

    private Long betId = -1L;
    private void createBet(int index){
        betId++;
        if(currentChip <= user.getBalance()) {
            Bet bet = new Bet();
            bet.setId(betId);
            ImageView imageView = new ImageView(this);
            imageView.setId(View.generateViewId());

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.image_width),
                    getResources().getDimensionPixelSize(R.dimen.image_height));

            ImageButton ib = betButtons.get(index);

            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            switch (currentChip) {
                case 1:
                    imageView.setImageResource(R.drawable.chip_one);
                    bet.setAmount(1);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.chip_five);
                    bet.setAmount(5);
                    break;
                case 25:
                    imageView.setImageResource(R.drawable.chip_twentyfive);
                    bet.setAmount(25);
                    break;
                case 50:
                    imageView.setImageResource(R.drawable.chip_fifth);
                    bet.setAmount(50);
                    break;
                case 100:
                    imageView.setImageResource(R.drawable.chip_onehundred);
                    bet.setAmount(100);
                    break;
            }

            innerConstraintLayout.addView(imageView);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(innerConstraintLayout);

            constraintSet.connect(imageView.getId(), ConstraintSet.START, ib.getId(), ConstraintSet.START, 0);
            constraintSet.connect(imageView.getId(), ConstraintSet.END, ib.getId(), ConstraintSet.END, 0);
            constraintSet.connect(imageView.getId(), ConstraintSet.TOP, ib.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, ib.getId(), ConstraintSet.BOTTOM, 0);

            constraintSet.applyTo(innerConstraintLayout);

            bet.setChip(imageView);

            bet.setBet(index);
            if(index <= 36)
                bet.setCoefficient(35);
            else if(index >= 43 && index <= 48)
                bet.setCoefficient(2);
            else if(index <= 42)
                bet.setCoefficient(3);

            bet.setPossibleGain(bet.getAmount() * bet.getCoefficient());

            bets.add(bet);

            updateRate(bet.getAmount(), "plus");
            updateBalance(bet.getAmount(), "minus");
        }

    }

    private void updateRate(int sum, String action){
        if(action.contains("plus")){
            rate += sum;
            rateTV.setText("$" + rate);
        }else if(action.contains("minus")){
            rate -= sum;
            rateTV.setText("$" + rate);
        }else if(action.contains("null")){
            rate = 0;
            rateTV.setText("$0");
        }
    }
    private void updateBalance(int sum, String action){
        if(action.contains("plus")){
            user.setBalance(user.getBalance() + sum);
            balanceTV.setText("$" + user.getBalance());
        }else if(action.contains("minus")) {
            user.setBalance(user.getBalance() - sum);
            balanceTV.setText("$" + user.getBalance());
        }
    }

    public void onHome(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        onBackPressed();
    }
    public void onShop(View view){
        if(user.isSounds())
            MainActivity.buttonPlayer.start();
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }
}