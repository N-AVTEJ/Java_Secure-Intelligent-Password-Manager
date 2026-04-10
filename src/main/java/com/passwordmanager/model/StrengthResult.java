package com.passwordmanager.model;

public class StrengthResult {
    private final String label;
    private final int score;
    private final double entropyBits;
    private final String crackTimeEstimate;

    public StrengthResult(String label, int score, double entropyBits, String crackTimeEstimate) {
        this.label = label;
        this.score = score;
        this.entropyBits = entropyBits;
        this.crackTimeEstimate = crackTimeEstimate;
    }

    public String getLabel() {
        return label;
    }

    public int getScore() {
        return score;
    }

    public double getEntropyBits() {
        return entropyBits;
    }

    public String getCrackTimeEstimate() {
        return crackTimeEstimate;
    }
}
