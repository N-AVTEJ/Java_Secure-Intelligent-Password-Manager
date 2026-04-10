package com.passwordmanager.ui;

import com.passwordmanager.ui.theme.ModernPanel;
import com.passwordmanager.ui.theme.RoundedButton;
import com.passwordmanager.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("GUARD PRO - Login");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        ModernPanel card = new ModernPanel(24);
        card.setPreferredSize(new Dimension(380, 300));
        card.setBackground(Theme.CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_TITLE);
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter master password to unlock", SwingConstants.CENTER);
        subtitle.setForeground(Theme.SUBTEXT);
        subtitle.setFont(Theme.FONT_REGULAR);
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);
        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JLabel passLabel = new JLabel("Master Password");
        passLabel.setForeground(Theme.TEXT);
        passLabel.setFont(Theme.FONT_BOLD);
        passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(Theme.BG);
        passwordField.setForeground(Theme.TEXT);
        passwordField.setCaretColor(Theme.ACCENT_BLUE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        RoundedButton loginButton = new RoundedButton("Unlock Vault", Theme.ACCENT_BLUE);
        loginButton.setMaximumSize(new Dimension(320, 45));
        loginButton.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        body.add(passLabel);
        body.add(Box.createVerticalStrut(10));
        body.add(passwordField);
        body.add(Box.createVerticalStrut(24));
        body.add(loginButton);

        card.add(body, BorderLayout.CENTER);
        root.add(card);

        loginButton.addActionListener(e -> {
            String masterPassword = new String(passwordField.getPassword());
            if (masterPassword.isBlank() || masterPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Master password must be at least 6 characters.", "Security", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                MainFrame mainFrame = new MainFrame(masterPassword);
                mainFrame.setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not open main screen: " + ex.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
