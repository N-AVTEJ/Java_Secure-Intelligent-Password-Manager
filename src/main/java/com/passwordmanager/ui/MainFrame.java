package com.passwordmanager.ui;

import com.passwordmanager.model.SecurityStats;
import com.passwordmanager.service.PasswordService;
import com.passwordmanager.ui.panels.DashboardPanel;
import com.passwordmanager.ui.panels.GeneratorPanel;
import com.passwordmanager.ui.panels.VaultPanel;
import com.passwordmanager.ui.theme.RoundedButton;
import com.passwordmanager.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    private final PasswordService service = new PasswordService();
    private final String masterPassword;

    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final GeneratorPanel generatorPanel = new GeneratorPanel();
    private VaultPanel vaultPanel;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerPanel = new JPanel(cardLayout);

    public MainFrame(String masterPassword) {
        this.masterPassword = masterPassword;
        setTitle("Secure Intelligent Password Manager");
        setSize(1240, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        vaultPanel = new VaultPanel(service, masterPassword, this::refreshDashboard);

        centerPanel.add(dashboardPanel, "DASHBOARD");
        centerPanel.add(generatorPanel, "GENERATOR");
        centerPanel.add(vaultPanel, "VAULT");
        add(centerPanel, BorderLayout.CENTER);

        refreshDashboard();
        showCard("DASHBOARD");
    }

    private JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(240, getHeight()));
        panel.setBackground(Theme.SIDEBAR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel title = new JLabel("GUARD PRO");
        title.setForeground(Theme.ACCENT_BLUE);
        title.setFont(Theme.FONT_TITLE);
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("SECURE MANAGER");
        subtitle.setForeground(Theme.SUBTEXT);
        subtitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 10));
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        RoundedButton dashboardBtn = new RoundedButton("Dashboard", Theme.ACCENT_BLUE);
        RoundedButton generatorBtn = new RoundedButton("Generator", Theme.ACCENT_PURPLE);
        RoundedButton vaultBtn = new RoundedButton("Vault", Theme.ACCENT_BLUE);

        Dimension btnSize = new Dimension(200, 42);
        dashboardBtn.setMaximumSize(btnSize);
        generatorBtn.setMaximumSize(btnSize);
        vaultBtn.setMaximumSize(btnSize);

        dashboardBtn.setAlignmentX(RoundedButton.CENTER_ALIGNMENT);
        generatorBtn.setAlignmentX(RoundedButton.CENTER_ALIGNMENT);
        vaultBtn.setAlignmentX(RoundedButton.CENTER_ALIGNMENT);

        dashboardBtn.addActionListener(e -> showCard("DASHBOARD"));
        generatorBtn.addActionListener(e -> showCard("GENERATOR"));
        vaultBtn.addActionListener(e -> {
            vaultPanel.requestFocusInWindow();
            showCard("VAULT");
        });

        panel.add(title);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(dashboardBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(generatorBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(vaultBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void showCard(String card) {
        cardLayout.show(centerPanel, card);
    }

    private void refreshDashboard() {
        SecurityStats stats = service.calculateStats(vaultPanel.getAllEntries(), masterPassword);
        dashboardPanel.updateStats(stats);
    }
}
