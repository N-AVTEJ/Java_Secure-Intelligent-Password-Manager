package com.passwordmanager.ui.panels;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.service.PasswordService;
import com.passwordmanager.ui.theme.ModernPanel;
import com.passwordmanager.ui.theme.RoundedButton;
import com.passwordmanager.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class VaultPanel extends JPanel {
    private final PasswordService service;
    private final String masterPassword;
    private final Runnable onDataChanged;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField();
    private final JTextField websiteField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JTextField passwordField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Social", "Banking", "Work", "Personal", "Other"});

    private List<PasswordEntry> allEntries = new ArrayList<>();
    private List<PasswordEntry> visibleEntries = new ArrayList<>();

    public VaultPanel(PasswordService service, String masterPassword, Runnable onDataChanged) {
        this.service = service;
        this.masterPassword = masterPassword;
        this.onDataChanged = onDataChanged;

        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header & Search
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        
        JLabel title = new JLabel("Secure Vault");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_TITLE);
        topArea.add(title, BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchBar.setOpaque(false);
        styleInput(searchField);
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.addActionListener(e -> applyFilter());
        
        RoundedButton filterBtn = new RoundedButton("Search", Theme.ACCENT_BLUE);
        searchBar.add(searchField);
        searchBar.add(filterBtn);
        topArea.add(searchBar, BorderLayout.EAST);
        
        add(topArea, BorderLayout.NORTH);

        // Center: Table
        tableModel = new DefaultTableModel(new Object[]{"Website", "Username", "Category"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        add(scroll, BorderLayout.CENTER);

        // Bottom: Form & Actions
        JPanel bottomArea = new JPanel(new BorderLayout(24, 0));
        bottomArea.setOpaque(false);
        bottomArea.setPreferredSize(new Dimension(getWidth(), 180));

        ModernPanel formCard = new ModernPanel(20);
        formCard.setBackground(Theme.CARD);
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formCard.setLayout(new GridLayout(2, 4, 15, 10));

        styleInput(websiteField);
        styleInput(usernameField);
        styleInput(passwordField);
        styleCombo(categoryBox);

        formCard.add(label("Website"));
        formCard.add(label("Username"));
        formCard.add(label("Password"));
        formCard.add(label("Category"));
        formCard.add(websiteField);
        formCard.add(usernameField);
        formCard.add(passwordField);
        formCard.add(categoryBox);
        
        bottomArea.add(formCard, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        
        RoundedButton saveBtn = new RoundedButton("Save Entry", Theme.ACCENT_GREEN);
        RoundedButton viewBtn = new RoundedButton("View Password", Theme.ACCENT_BLUE);
        RoundedButton deleteBtn = new RoundedButton("Delete Item", Theme.ACCENT_RED);
        
        saveBtn.setMaximumSize(new Dimension(150, 40));
        viewBtn.setMaximumSize(new Dimension(150, 40));
        deleteBtn.setMaximumSize(new Dimension(150, 40));

        actions.add(saveBtn);
        actions.add(Box.createVerticalStrut(10));
        actions.add(viewBtn);
        actions.add(Box.createVerticalStrut(10));
        actions.add(deleteBtn);
        
        bottomArea.add(actions, BorderLayout.EAST);
        add(bottomArea, BorderLayout.SOUTH);

        // Listeners
        saveBtn.addActionListener(e -> addEntry());
        viewBtn.addActionListener(e -> viewSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        filterBtn.addActionListener(e -> applyFilter());

        refreshEntries();
    }

    private void styleTable(JTable t) {
        t.setRowHeight(35);
        t.setBackground(Theme.CARD);
        t.setForeground(Theme.TEXT);
        t.setSelectionBackground(new Color(Theme.ACCENT_BLUE.getRed(), Theme.ACCENT_BLUE.getGreen(), Theme.ACCENT_BLUE.getBlue(), 60));
        t.setSelectionForeground(Color.WHITE);
        t.setFont(Theme.FONT_REGULAR);
        t.setGridColor(Theme.BORDER);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setBackground(Theme.SIDEBAR);
        t.getTableHeader().setForeground(Theme.TEXT);
        t.getTableHeader().setFont(Theme.FONT_BOLD);
        t.getTableHeader().setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        t.setShowVerticalLines(false);
    }

    private void addEntry() {
        String web = websiteField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        String cat = (String) categoryBox.getSelectedItem();

        if (web.isBlank() || user.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            service.saveOrUpdateEntry(web, user, pass, cat, masterPassword);
            websiteField.setText("");
            usernameField.setText("");
            passwordField.setText("");
            refreshEntries();
            onDataChanged.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        visibleEntries = service.filter(allEntries, searchField.getText());
        loadTable(visibleEntries);
    }

    private void refreshEntries() {
        try {
            allEntries = service.getAllEntries();
            visibleEntries = new ArrayList<>(allEntries);
            loadTable(visibleEntries);
        } catch (Exception ex) {
            allEntries = new ArrayList<>();
            visibleEntries = new ArrayList<>();
            loadTable(visibleEntries);
        }
    }

    private void loadTable(List<PasswordEntry> entries) {
        tableModel.setRowCount(0);
        for (PasswordEntry e : entries) {
            tableModel.addRow(new Object[]{e.getWebsite(), e.getUsername(), e.getCategory()});
        }
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        PasswordEntry entry = visibleEntries.get(row);
        String input = JOptionPane.showInputDialog(this, "Enter Master Password to decrypt:");
        if (input == null || input.isBlank()) return;
        
        try {
            String decrypted = service.decryptEntry(entry, input);
            JOptionPane.showMessageDialog(this, "Password: " + decrypted, "Decrypted View", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(decrypted), null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Decryption failed. Check your master password.", "Security Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        PasswordEntry entry = visibleEntries.get(row);
        if (JOptionPane.showConfirmDialog(this, "Permanently delete this entry?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                service.deleteEntry(entry.getId());
                refreshEntries();
                onDataChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<PasswordEntry> getAllEntries() { return new ArrayList<>(allEntries); }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Theme.SUBTEXT);
        l.setFont(Theme.FONT_BOLD);
        return l;
    }

    private void styleInput(JTextField f) {
        f.setBackground(Theme.BG);
        f.setForeground(Theme.TEXT);
        f.setCaretColor(Theme.ACCENT_BLUE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleCombo(JComboBox<String> c) {
        c.setBackground(Theme.BG);
        c.setForeground(Theme.TEXT);
        c.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
    }
}
