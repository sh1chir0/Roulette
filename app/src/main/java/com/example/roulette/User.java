package com.example.roulette;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class User {
    private static final String FILE_NAME = "user_data.json";
    private int balance;
    private int maximumProfit;
    private boolean sounds;
    private boolean music;
    public User(){}

    public User(int balance, int maximumProfit, boolean sounds, boolean music) {
        this.balance = balance;
        this.maximumProfit = maximumProfit;
        this.sounds = sounds;
        this.music = music;
    }

    public User loggingIn(Context context){
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(FILE_NAME, "");

        if (json.isEmpty()) {
            User initialUser = new User();
            initialUser.setBalance(750);
            initialUser.setMusic(true);
            initialUser.setSounds(true);
            saveUser(initialUser, context);
            return initialUser;
        }

        return gson.fromJson(json, User.class);
    }

    public void saveUser(User user, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(user);

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FILE_NAME, json);
        editor.apply();
    }

    public boolean isSounds() {
        return sounds;
    }

    public void setSounds(boolean sounds) {
        this.sounds = sounds;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getMaximumProfit() {
        return maximumProfit;
    }

    public void setMaximumProfit(int maximumProfit) {
        this.maximumProfit = maximumProfit;
    }
}
