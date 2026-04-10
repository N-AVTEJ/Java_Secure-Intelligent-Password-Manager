package com.passwordmanager.ui.panels;

import com.passwordmanager.model.StrengthResult;
import com.passwordmanager.ui.theme.ModernPanel;
import com.passwordmanager.ui.theme.RoundedButton;
import com.passwordmanager.ui.theme.Theme;
import com.passwordmanager.util.PasswordGenerator;
import com.passwordmanager.util.PasswordStrengthAnalyzer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class GeneratorPanel extends JPanel {
    private final JTextField output = new JTextField();
    private final JProgressBar strengthBar = new JProgressBar(0, 100);
    private final JLabel details = new JLabel("Strength: -, Entropy: 0 bits");
    private final JSlider lengthSlider = new JSlider(8, 64, 16);
    private final JCheckBox upper = option("Uppercase Letters");
    private final JCheckBox lower = option("Lowercase Letters");
    private final JCheckBox digits = option("Include Numbers");
    private final JCheckBox symbols = option("Special Symbols");
    private final JCheckBox avoidSimilar = option("Avoid Similar Characters (O, 0, l, 1)");

    public GeneratorPanel() {
        setBackground(Theme.BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Password Generator");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_TITLE);
        title.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(24));

        // Output Card
        ModernPanel outputCard = new ModernPanel(20);
        outputCard.setBackground(Theme.CARD);
        outputCard.setLayout(new BorderLayout(15, 0));
        outputCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        outputCard.setMaximumSize(new Dimension(1000, 120));
        
        styleOutputField(output);
        outputCard.add(output, BorderLayout.CENTER);

        JPanel outputActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        outputActions.setOpaque(false);
        RoundedButton genBtn = new RoundedButton("Generate", Theme.ACCENT_BLUE);
        RoundedButton copyBtn = new RoundedButton("Copy", Theme.ACCENT_GREEN);
        outputActions.add(genBtn);
        outputActions.add(copyBtn);
        outputCard.add(outputActions, BorderLayout.SOUTH);
        
        add(outputCard);
        add(Box.createVerticalStrut(20));

        // Settings Grid
        JPanel mainSettings = new JPanel(new GridLayout(1, 2, 20, 0));
        mainSettings.setOpaque(false);
        mainSettings.setMaximumSize(new Dimension(1000, 280));

        // Length & Options Card
        ModernPanel configCard = new ModernPanel(20);
        configCard.setBackground(Theme.CARD);
        configCard.setLayout(new BoxLayout(configCard, BoxLayout.Y_AXIS));
        configCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel lenLabel = new JLabel("Password Length: 16");
        lenLabel.setForeground(Theme.TEXT);
        lenLabel.setFont(Theme.FONT_BOLD);
        lengthSlider.setBackground(Theme.CARD);
        lengthSlider.addChangeListener(e -> lenLabel.setText("Password Length: " + lengthSlider.getValue()));
        
        configCard.add(lenLabel);
        configCard.add(Box.createVerticalStrut(10));
        configCard.add(lengthSlider);
        configCard.add(Box.createVerticalStrut(20));
        configCard.add(upper);
        configCard.add(lower);
        configCard.add(digits);
        configCard.add(symbols);
        configCard.add(avoidSimilar);
        
        upper.setSelected(true);
        lower.setSelected(true);
        digits.setSelected(true);
        symbols.setSelected(true);

        // Strength Analyzer Card
        ModernPanel strengthCard = new ModernPanel(20);
        strengthCard.setBackground(Theme.CARD);
        strengthCard.setLayout(new BoxLayout(strengthCard, BoxLayout.Y_AXIS));
        strengthCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel strLabel = new JLabel("Real-time Analysis");
        strLabel.setForeground(Theme.TEXT);
        strLabel.setFont(Theme.FONT_BOLD);
        
        strengthBar.setStringPainted(true);
        strengthBar.setBackground(Theme.BG);
        strengthBar.setBorder(BorderFactory.createEmptyBorder());
        strengthBar.setPreferredSize(new Dimension(300, 25));
        
        details.setForeground(Theme.SUBTEXT);
        details.setFont(Theme.FONT_REGULAR);
        
        strengthCard.add(strLabel);
        strengthCard.add(Box.createVerticalStrut(15));
        strengthCard.add(strengthBar);
        strengthCard.add(Box.createVerticalStrut(15));
        strengthCard.add(details);
        
        mainSettings.add(configCard);
        mainSettings.add(strengthCard);
        add(mainSettings);
        add(Box.createVerticalGlue());

        // Listeners
        genBtn.addActionListener(e -> generatePassword());
        copyBtn.addActionListener(e -> copyToClipboard());
        output.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { analyze(); }
            public void removeUpdate(DocumentEvent e) { analyze(); }
            public void changedUpdate(DocumentEvent e) { analyze(); }
        });
    }

    private void generatePassword() {
        String keyword = output.getText();
        if (keyword == null || keyword.isBlank()) {
            output.setText(PasswordGenerator.generate(lengthSlider.getValue(), upper.isSelected(), 
                lower.isSelected(), digits.isSelected(), symbols.isSelected(), avoidSimilar.isSelected()));
        } else {
            output.setText(generateToughFromKeyword(keyword, lengthSlider.getValue(), upper.isSelected(), 
                lower.isSelected(), digits.isSelected(), symbols.isSelected(), avoidSimilar.isSelected()));
        }
    }

    private void analyze() {
        String pass = output.getText();
        if (pass.isBlank()) {
            strengthBar.setValue(0);
            strengthBar.setString("Empty");
            details.setText("Strength: -, Entropy: 0 bits");
            return;
        }
        StrengthResult res = PasswordStrengthAnalyzer.analyze(pass);
        strengthBar.setValue(res.getScore());
        strengthBar.setString(res.getLabel() + " (" + res.getScore() + "%)");
        
        if (res.getScore() > 75) strengthBar.setForeground(Theme.ACCENT_GREEN);
        else if (res.getScore() > 40) strengthBar.setForeground(java.awt.Color.ORANGE);
        else strengthBar.setForeground(Theme.ACCENT_RED);
        
        details.setText("<html>Strength: " + res.getLabel() + "<br>Entropy: " + 
            String.format("%.1f", res.getEntropyBits()) + " bits<br>Time: " + res.getCrackTimeEstimate() + "</html>");
    }

    private void copyToClipboard() {
        if (output.getText().isBlank()) return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(output.getText()), null);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private JCheckBox option(String text) {
        JCheckBox c = new JCheckBox(text);
        c.setOpaque(false);
        c.setForeground(Theme.TEXT);
        c.setFont(Theme.FONT_REGULAR);
        c.setFocusPainted(false);
        return c;
    }

    private void styleOutputField(JTextField f) {
        f.setBackground(Theme.BG);
        f.setForeground(Theme.ACCENT_BLUE);
        f.setCaretColor(Theme.ACCENT_BLUE);
        f.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 22));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private String generateToughFromKeyword(String keyword, int length, boolean useU, boolean useL, boolean useD, boolean useS, boolean avoid) {
        // Simple adaptation of original logic
        String upperSet = filterSimilar("ABCDEFGHIJKLMNOPQRSTUVWXYZ", avoid);
        String lowerSet = filterSimilar("abcdefghijklmnopqrstuvwxyz", avoid);
        String digitSet = filterSimilar("0123456789", avoid);
        String symSet = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        
        StringBuilder res = new StringBuilder(keyword);
        java.security.SecureRandom rand = new java.security.SecureRandom();
        
        String pool = "";
        if (useU) pool += upperSet;
        if (useL) pool += lowerSet;
        if (useD) pool += digitSet;
        if (useS) pool += symSet;
        
        if (pool.isEmpty()) return keyword;
        
        while (res.length() < length) {
            res.append(pool.charAt(rand.nextInt(pool.length())));
        }
        return res.toString();
    }

    private String filterSimilar(String in, boolean avoid) {
        if (!avoid) return in;
        return in.replaceAll("[O0l1I]", "");
    }
}
