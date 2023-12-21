package com.example.roulette;

import android.widget.ImageView;

public class Bet {
    private Long id;
    private int bet;

    @Override
    public String toString() {
        return "Bet{" +
                ", id=" + id +
                ", bet=" + bet +
                ", amount=" + amount +
                '}';
    }

    private int amount;
    private int possibleGain;
    private int coefficient;
    private ImageView chip;
    public Bet(){}

    public Bet(Long id, int bet, int amount, int possibleGain, int coefficient, ImageView chip) {
        this.id = id;
        this.bet = bet;
        this.amount = amount;
        this.possibleGain = possibleGain;
        this.coefficient = coefficient;
        this.chip = chip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPossibleGain() {
        return possibleGain;
    }

    public void setPossibleGain(int possibleGain) {
        this.possibleGain = possibleGain;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public ImageView getChip() {
        return chip;
    }

    public void setChip(ImageView chip) {
        this.chip = chip;
    }
}
