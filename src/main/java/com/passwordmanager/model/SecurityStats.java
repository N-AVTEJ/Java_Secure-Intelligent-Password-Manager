package com.passwordmanager.model;

import java.util.Map;

public class SecurityStats {
    private final int totalPasswords;
    private final int weakPasswords;
    private final int reusedPasswords;
    private final Map<String, Integer> categoryDistribution;

    public SecurityStats(int totalPasswords, int weakPasswords, int reusedPasswords, Map<String, Integer> categoryDistribution) {
        this.totalPasswords = totalPasswords;
        this.weakPasswords = weakPasswords;
        this.reusedPasswords = reusedPasswords;
        this.categoryDistribution = categoryDistribution;
    }

    public int getTotalPasswords() {
        return totalPasswords;
    }

    public int getWeakPasswords() {
        return weakPasswords;
    }

    public int getReusedPasswords() {
        return reusedPasswords;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }
}
