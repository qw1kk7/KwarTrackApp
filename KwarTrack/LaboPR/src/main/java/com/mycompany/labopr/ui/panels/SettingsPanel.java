package com.mycompany.labopr.ui.panels;

import com.mycompany.labopr.views.GUI;
import com.mycompany.labopr.data.SettingsData;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JPanel implements UITheme.ThemeChangeListener {
    private JFrame parentFrame;
    
    // Profile settings fields
    private JTextField displayNameField;
    private JTextField emailField;
    private JComboBox<String> currencyCombo;
    private JComboBox<String> dateFormatCombo;
    
    // Appearance settings
    private JRadioButton lightModeRadio;
    private JRadioButton darkModeRadio;
    private JComboBox<String> accentColorCombo;
    
    public SettingsPanel(JFrame parent) {
        this.parentFrame = parent;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.PRIMARY_GREEN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        UITheme.addThemeChangeListener(this);
        
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        // Top panel with title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Settings & Preferences");
        titleLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 32));
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with all settings sections
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Add sections
        centerPanel.add(createProfileSection());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createAppearanceSection());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDataManagementSection());
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_GREEN);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createProfileSection() {
        JPanel section = createSectionPanel("Profile Settings");
        
        // Display Name
        displayNameField = new JTextField(20);
        section.add(createFieldRow("Display Name:", displayNameField));
        section.add(Box.createVerticalStrut(15));
        
        // Email
        emailField = new JTextField(20);
        emailField.setEditable(false);
        emailField.setBackground(new Color(0xf0f0f0));
        section.add(createFieldRow("Email:", emailField));
        section.add(Box.createVerticalStrut(15));
        
        // Currency
        String[] currencies = {"PHP (₱)", "USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)"};
        currencyCombo = new JComboBox<>(currencies);
        section.add(createFieldRow("Currency:", currencyCombo));
        section.add(Box.createVerticalStrut(15));
        
        // Date Format
        String[] dateFormats = {"YYYY-MM-DD", "DD/MM/YYYY", "MM/DD/YYYY"};
        dateFormatCombo = new JComboBox<>(dateFormats);
        section.add(createFieldRow("Date Format:", dateFormatCombo));
        section.add(Box.createVerticalStrut(20));
        
        // Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton saveBtn = ButtonFactory.createRoundedButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(150, 40));
        saveBtn.addActionListener(e -> handleSaveProfile());
        buttonPanel.add(saveBtn);
        
        section.add(buttonPanel);
        
        return section;
    }
    
    private JPanel createAppearanceSection() {
        JPanel section = createSectionPanel("Appearance");
        
        // Theme mode
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setOpaque(false);
        
        JLabel themeLabel = new JLabel("Theme Mode:");
        themeLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        themeLabel.setForeground(UITheme.TEXT_COLOR);
        
        lightModeRadio = new JRadioButton("Light Mode");
        darkModeRadio = new JRadioButton("Dark Mode");
        
        lightModeRadio.setOpaque(false);
        darkModeRadio.setOpaque(false);
        lightModeRadio.setForeground(UITheme.TEXT_COLOR);
        darkModeRadio.setForeground(UITheme.TEXT_COLOR);
        lightModeRadio.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        darkModeRadio.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightModeRadio);
        themeGroup.add(darkModeRadio);
        
        lightModeRadio.addActionListener(e -> handleThemeChange(false));
        darkModeRadio.addActionListener(e -> handleThemeChange(true));
        
        themePanel.add(themeLabel);
        themePanel.add(Box.createHorizontalStrut(20));
        themePanel.add(lightModeRadio);
        themePanel.add(Box.createHorizontalStrut(10));
        themePanel.add(darkModeRadio);
        
        section.add(themePanel);
        section.add(Box.createVerticalStrut(15));
        
        // Accent Color
        String[] accentColors = {"Green", "Blue", "Purple", "Orange", "Red"};
        accentColorCombo = new JComboBox<>(accentColors);
        accentColorCombo.addActionListener(e -> handleAccentColorChange());
        section.add(createFieldRow("Accent Color:", accentColorCombo));
        
        section.add(Box.createVerticalStrut(10));
        
        JLabel noteLabel = new JLabel("* Theme changes apply immediately");
        noteLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.ITALIC, 12));
        noteLabel.setForeground(UITheme.TEXT_COLOR);
        section.add(noteLabel);
        
        return section;
    }
    
    private JPanel createDataManagementSection() {
        JPanel section = createSectionPanel("Data Management");
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setOpaque(false);
        
        // Reset Data button
        JButton resetBtn = ButtonFactory.createRoundedButton("Reset All Data");
        resetBtn.setPreferredSize(new Dimension(160, 40));
        resetBtn.addActionListener(e -> handleResetData());
        buttonsPanel.add(resetBtn);
        
        // Export Data button
        JButton exportBtn = ButtonFactory.createRoundedButton("Export Data");
        exportBtn.setPreferredSize(new Dimension(160, 40));
        exportBtn.addActionListener(e -> handleExportData());
        buttonsPanel.add(exportBtn);
        
        // Import Data button
        JButton importBtn = ButtonFactory.createRoundedButton("Import Data");
        importBtn.setPreferredSize(new Dimension(160, 40));
        importBtn.addActionListener(e -> handleImportData());
        buttonsPanel.add(importBtn);
        
        section.add(buttonsPanel);
        section.add(Box.createVerticalStrut(15));
        
        // Logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setOpaque(false);
        
        JButton logoutBtn = ButtonFactory.createRoundedButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(160, 40));
        logoutBtn.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutBtn);
        
        section.add(logoutPanel);
        
        return section;
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xcccccc), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x333333));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        return panel;
    }
    
    private JPanel createFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        label.setForeground(new Color(0x333333));
        label.setPreferredSize(new Dimension(120, 25));
        
        if (field instanceof JTextField) {
            field.setPreferredSize(new Dimension(300, 30));
        } else if (field instanceof JComboBox) {
            field.setPreferredSize(new Dimension(200, 30));
        }
        
        row.add(label);
        row.add(field);
        
        return row;
    }
    
    private void loadSettings() {
        // Load profile settings
        displayNameField.setText(SettingsData.getSetting(SettingsData.DISPLAY_NAME));
        emailField.setText(SettingsData.getSetting(SettingsData.EMAIL));
        currencyCombo.setSelectedItem(SettingsData.getSetting(SettingsData.CURRENCY));
        dateFormatCombo.setSelectedItem(SettingsData.getSetting(SettingsData.DATE_FORMAT));
        
        // Load appearance settings
        String themeMode = SettingsData.getSetting(SettingsData.THEME_MODE);
        if (themeMode.equals("dark")) {
            darkModeRadio.setSelected(true);
        } else {
            lightModeRadio.setSelected(true);
        }
        
        accentColorCombo.setSelectedItem(SettingsData.getSetting(SettingsData.ACCENT_COLOR));
    }
    
    private void handleSaveProfile() {
        SettingsData.setSetting(SettingsData.DISPLAY_NAME, displayNameField.getText());
        SettingsData.setSetting(SettingsData.EMAIL, emailField.getText());
        SettingsData.setSetting(SettingsData.CURRENCY, (String) currencyCombo.getSelectedItem());
        SettingsData.setSetting(SettingsData.DATE_FORMAT, (String) dateFormatCombo.getSelectedItem());
        
        JOptionPane.showMessageDialog(
            parentFrame,
            "Profile settings saved successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void handleThemeChange(boolean isDark) {
        if (isDark != UITheme.isDarkMode()) {
            UITheme.toggleDarkMode();
        }
        SettingsData.setSetting(SettingsData.THEME_MODE, isDark ? "dark" : "light");
    }
    
    private void handleAccentColorChange() {
        String color = (String) accentColorCombo.getSelectedItem();
        SettingsData.setSetting(SettingsData.ACCENT_COLOR, color);
        
        // Note: Full accent color implementation would require updating UITheme
        JOptionPane.showMessageDialog(
            parentFrame,
            "Accent color preference saved: " + color + "\n(Will be applied in future updates)",
            "Accent Color",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void handleResetData() {
        int confirm = JOptionPane.showConfirmDialog(
            parentFrame,
            "Are you sure you want to reset all data?\n\n" +
            "This will permanently delete:\n" +
            "• All transactions\n" +
            "• All budget goals\n" +
            "• Balance information\n" +
            "• Custom categories\n\n" +
            "This action cannot be undone!",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (SettingsData.resetAllData()) {
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "All data has been reset successfully.",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "Failed to reset data. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void handleExportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("kwartrack_export.csv"));
        
        int result = fileChooser.showSaveDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filepath = file.getAbsolutePath();
            if (!filepath.endsWith(".csv")) {
                filepath += ".csv";
            }
            
            if (SettingsData.exportData(filepath)) {
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "Data exported successfully to:\n" + filepath,
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "Failed to export data. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void handleImportData() {
        int confirm = JOptionPane.showConfirmDialog(
            parentFrame,
            "Importing data will add to your existing data.\n" +
            "It's recommended to export your current data first as a backup.\n\n" +
            "Continue with import?",
            "Confirm Import",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Data");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            
            int result = fileChooser.showOpenDialog(parentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                if (SettingsData.importData(file.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "Data imported successfully!\nPlease refresh panels to see the updated data.",
                        "Import Complete",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "Failed to import data. Please check the file format.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            parentFrame,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.dispose();
            new GUI().setVisible(true);
        }
    }
    
    @Override
    public void onThemeChanged() {
        setBackground(UITheme.PRIMARY_GREEN);
        
        // Update radio buttons
        lightModeRadio.setForeground(UITheme.TEXT_COLOR);
        darkModeRadio.setForeground(UITheme.TEXT_COLOR);
        
        repaint();
    }
    
    public void cleanup() {
        UITheme.removeThemeChangeListener(this);
    }
}