package com.passwordmanager.ui.panels;

import com.passwordmanager.model.SecurityStats;
import com.passwordmanager.ui.theme.ModernPanel;
import com.passwordmanager.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private final JLabel totalLabel = new JLabel("0");
    private final JLabel weakLabel = new JLabel("0");
    private final JLabel reusedLabel = new JLabel("0");
    private final JLabel categoryLabel = new JLabel("-");

    public DashboardPanel() {
        setBackground(Theme.BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Security Insights");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        add(title);

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.add(metricCard("Total Passwords", totalLabel, Theme.ACCENT_BLUE));
        cardsGrid.add(metricCard("Weak & Unsafe", weakLabel, Theme.ACCENT_RED));
        cardsGrid.add(metricCard("Reused Items", reusedLabel, Theme.ACCENT_GREEN));
        add(cardsGrid);

        add(new JPanel() {{
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        }});

        ModernPanel categoryCard = new ModernPanel(20);
        categoryCard.setBackground(Theme.CARD);
        categoryCard.setLayout(new BorderLayout());
        categoryCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel catTitle = new JLabel("Category Distribution");
        catTitle.setForeground(Theme.SUBTEXT);
        catTitle.setFont(Theme.FONT_MEDIUM);
        categoryLabel.setForeground(Theme.TEXT);
        categoryLabel.setFont(Theme.FONT_SUBTITLE);
        
        categoryCard.add(catTitle, BorderLayout.NORTH);
        categoryCard.add(categoryLabel, BorderLayout.CENTER);
        add(categoryCard);
    }

    private JPanel metricCard(String title, JLabel valueLabel, java.awt.Color accent) {
        ModernPanel panel = new ModernPanel(20);
        panel.setBackground(Theme.CARD);
        panel.setLayout(new GridLayout(2, 1, 0, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Theme.SUBTEXT);
        titleLabel.setFont(Theme.FONT_MEDIUM);
        
        valueLabel.setForeground(accent);
        valueLabel.setFont(Theme.FONT_TITLE);
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        return panel;
    }

    public void updateStats(SecurityStats stats) {
        totalLabel.setText(String.valueOf(stats.getTotalPasswords()));
        weakLabel.setText(String.valueOf(stats.getWeakPasswords()));
        reusedLabel.setText(String.valueOf(stats.getReusedPasswords()));

        Map<String, Integer> map = stats.getCategoryDistribution();
        if (map.isEmpty()) {
            categoryLabel.setText("No data available yet.");
            return;
        }
        String categories = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> "<html><font color='#888888'>" + e.getKey() + ":</font> " + e.getValue() + "</html>")
                .collect(Collectors.joining("  &nbsp;|&nbsp;  "));
        categoryLabel.setText("<html>" + categories + "</html>");
    }
}
