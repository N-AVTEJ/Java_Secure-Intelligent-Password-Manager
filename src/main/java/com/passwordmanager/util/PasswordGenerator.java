package com.passwordmanager.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String SIMILAR = "O0l1I";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String generate(int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSymbols, boolean avoidSimilar) {
        if (length < 4) {
            throw new IllegalArgumentException("Length must be at least 4.");
        }

        List<String> selectedSets = new ArrayList<>();
        if (useUpper) {
            selectedSets.add(filter(UPPER, avoidSimilar));
        }
        if (useLower) {
            selectedSets.add(filter(LOWER, avoidSimilar));
        }
        if (useDigits) {
            selectedSets.add(filter(DIGITS, avoidSimilar));
        }
        if (useSymbols) {
            selectedSets.add(SYMBOLS);
        }

        if (selectedSets.isEmpty()) {
            throw new IllegalArgumentException("Select at least one character type.");
        }
        if (length < selectedSets.size()) {
            throw new IllegalArgumentException("Length is too short for selected character types.");
        }

        StringBuilder allChars = new StringBuilder();
        for (String set : selectedSets) {
            allChars.append(set);
        }

        List<Character> result = new ArrayList<>();

        // Ensure at least one character from each selected set.
        for (String set : selectedSets) {
            result.add(set.charAt(SECURE_RANDOM.nextInt(set.length())));
        }

        for (int i = result.size(); i < length; i++) {
            result.add(allChars.charAt(SECURE_RANDOM.nextInt(allChars.length())));
        }

        Collections.shuffle(result, SECURE_RANDOM);
        StringBuilder password = new StringBuilder();
        for (Character c : result) {
            password.append(c);
        }
        return password.toString();
    }

    private static String filter(String source, boolean avoidSimilar) {
        if (!avoidSimilar) {
            return source;
        }
        StringBuilder filtered = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (SIMILAR.indexOf(c) < 0) {
                filtered.append(c);
            }
        }
        return filtered.toString();
    }
}
