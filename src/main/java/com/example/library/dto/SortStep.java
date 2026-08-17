package com.example.library.dto;

import java.util.List;

/**
 * Represents a single round of the bubble sort process.
 */
public class SortStep {

    private int round;
    private List<Integer> after;
    private boolean swapped;

    public SortStep() {
    }

    public SortStep(int round, List<Integer> after, boolean swapped) {
        this.round = round;
        this.after = after;
        this.swapped = swapped;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Integer> getAfter() {
        return after;
    }

    public void setAfter(List<Integer> after) {
        this.after = after;
    }

    public boolean isSwapped() {
        return swapped;
    }

    public void setSwapped(boolean swapped) {
        this.swapped = swapped;
    }
}