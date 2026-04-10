package com.passwordmanager.util;

import com.passwordmanager.model.StrengthResult;

public final class PasswordStrengthAnalyzer {
    private PasswordStrengthAnalyzer() {
    }

    public static StrengthResult analyze(String password) {
        if (password == null || password.isBlank()) {
            return new StrengthResult("Weak", 0, 0.0, "Instantly");
        }

        int pool = 0;
        if (password.matches(".*[a-z].*")) {
            pool += 26;
        }
        if (password.matches(".*[A-Z].*")) {
            pool += 26;
        }
        if (password.matches(".*\\d.*")) {
            pool += 10;
        }
        if (password.matches(".*[^a-zA-Z0-9].*")) {
            pool += 32;
        }
        if (pool == 0) {
            pool = 1;
        }

        double entropy = password.length() * (Math.log(pool) / Math.log(2));
        // Map entropy -> score. Lower denominator makes the score more responsive for short passwords.
        int score = (int) Math.min(100, Math.round((entropy / 60.0) * 100));

        String label;
        if (score < 40) {
            label = "Weak";
        } else if (score < 70) {
            label = "Medium";
        } else {
            label = "Strong";
        }

        double guessesPerSecond = 1e9;
        double seconds = Math.pow(2, entropy) / guessesPerSecond;
        String crackTime = formatDuration(seconds);

        return new StrengthResult(label, score, entropy, crackTime);
    }

    private static String formatDuration(double seconds) {
        if (seconds < 1) return "Instantly";
        if (seconds < 60) return String.format("%.0f seconds", seconds);
        if (seconds < 3600) return String.format("%.0f minutes", seconds / 60);
        if (seconds < 86400) return String.format("%.1f hours", seconds / 3600);
        if (seconds < 31536000) return String.format("%.1f days", seconds / 86400);
        if (seconds < 31536000L * 1000) return String.format("%.1f years", seconds / 31536000);
        return "Centuries+";
    }
}
