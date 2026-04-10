package com.passwordmanager.service;

import com.passwordmanager.db.PasswordRepository;
import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.model.SecurityStats;
import com.passwordmanager.model.StrengthResult;
import com.passwordmanager.util.CryptoUtils;
import com.passwordmanager.util.PasswordStrengthAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PasswordService {
    private final PasswordRepository repository = new PasswordRepository();

    public List<PasswordEntry> getAllEntries() {
        return repository.findAll();
    }

    public void saveEntry(String website, String username, String plainPassword, String category, String masterPassword) {
        String encrypted = CryptoUtils.encrypt(plainPassword, masterPassword);
        repository.insert(website, username, encrypted, category);
    }

    public void saveOrUpdateEntry(String website, String username, String plainPassword, String category, String masterPassword) {
        String encrypted = CryptoUtils.encrypt(plainPassword, masterPassword);
        repository.upsert(website, username, encrypted, category);
    }

    public void deleteEntry(int id) {
        repository.deleteById(id);
    }

    public String decryptEntry(PasswordEntry entry, String masterPassword) {
        return CryptoUtils.decrypt(entry.getEncryptedPassword(), masterPassword);
    }

    public SecurityStats calculateStats(List<PasswordEntry> entries, String masterPassword) {
        int weak = 0;
        Map<String, Integer> categoryMap = new HashMap<>();
        Map<String, Integer> reuseCounter = new HashMap<>();

        for (PasswordEntry entry : entries) {
            categoryMap.merge(entry.getCategory(), 1, Integer::sum);
            try {
                String plain = decryptEntry(entry, masterPassword);
                StrengthResult result = PasswordStrengthAnalyzer.analyze(plain);
                if ("Weak".equals(result.getLabel())) {
                    weak++;
                }
                reuseCounter.merge(plain, 1, Integer::sum);
            } catch (Exception ignored) {
                // Skip entries that cannot be decrypted with current master password.
            }
        }

        int reused = reuseCounter.values().stream()
                .filter(count -> count > 1)
                .mapToInt(count -> count - 1)
                .sum();

        return new SecurityStats(entries.size(), weak, reused, categoryMap);
    }

    public List<PasswordEntry> filter(List<PasswordEntry> entries, String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isBlank()) {
            return entries;
        }
        return entries.stream()
                .filter(e -> e.getWebsite().toLowerCase().contains(q)
                        || e.getUsername().toLowerCase().contains(q)
                        || e.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public void seedDemoEntries(String masterPassword) {
        // Replace seeded demo data with only the demo rows requested for academic demonstration.
        // Plain demo password values are set equal to the demo usernames so they decrypt correctly.
        repository.deleteAll();

        saveEntry("School Portal", "student.navtej", "student.navtej", "Work", masterPassword);
        saveEntry("TestSite", "test_user", "test_user", "Work", masterPassword);
        saveEntry("schoot", "school_user", "school_user", "Other", masterPassword);
    }
}
