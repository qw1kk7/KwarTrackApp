package com.mycompany.labopr.views;

import com.mycompany.labopr.ui.factories.PanelFactory;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;

public class Landing extends JFrame implements UITheme.ThemeChangeListener {

    private JPanel topNav;
    private JPanel contentPanel;
    private JPanel buttonPanel;
    private JLabel title;
    private JLabel subtitle;

    public Landing() {
        setTitle("KwarTrack - Landing Page");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Register for theme change notifications
        UITheme.addThemeChangeListener(this);

        initializeComponents();
        updateTheme();

        setVisible(true);
    }

    private void initializeComponents() {
        // Top navigation with dark mode button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Navigation buttons
        topNav = PanelFactory.createNavPanel();
        topNav.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String[] navItems = {"Home", "About", "Contact"};
        for (String item : navItems) {
            JButton btn = ButtonFactory.createNavButton(item);
            btn.addActionListener(e -> showNavDialog(item));
            topNav.add(btn);
        }

        // Dark mode button
        JPanel darkModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        darkModePanel.setOpaque(false);
        darkModePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        darkModePanel.add(ButtonFactory.createDarkModeButton());

        topPanel.add(darkModePanel, BorderLayout.WEST);
        topPanel.add(topNav, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Main content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(Box.createVerticalStrut(100));
        contentPanel.add(PanelFactory.createLogoLabel("/KTrack Logo.png", 180, 180));
        contentPanel.add(Box.createVerticalStrut(20));

        title = PanelFactory.createLabel("Welcome to KwarTrack!", UITheme.FONT_LARGE, true);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(20));

        subtitle = PanelFactory.createLabel("Your personal finance tracker and manager", UITheme.FONT_MEDIUM, false);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitle);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom button panel with "Get Started" button
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UITheme.PANEL_PADDING_TOP, 0, UITheme.PANEL_PADDING_BOTTOM, 0));

        JButton getStartedBtn = ButtonFactory.createRoundedButton("Get Started");
        getStartedBtn.addActionListener(e -> handleGetStarted());
        getStartedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(getStartedBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleGetStarted() {
        dispose();
        new MainApp().setVisible(true);
    }

    @Override
    public void onThemeChanged() {
        updateTheme();
    }

    private void updateTheme() {
        getContentPane().setBackground(UITheme.PRIMARY_GREEN);
        topNav.setBackground(UITheme.PRIMARY_GREEN);
        title.setForeground(UITheme.TEXT_COLOR);
        subtitle.setForeground(UITheme.TEXT_COLOR);
        repaint();
    }

    private void showNavDialog(String item) {
        String message = switch (item) {
            case "Home" -> "<html><h2>Home</h2><p>This is the Home page draft.</p></html>";
            case "About" -> "<html><h2>About</h2><p>KwarTrack helps you manage and track your finances easily.</p></html>";
            case "Contact" -> "<html><h2>Contact</h2><p>Email: support@kwartrack.com<br>Phone: +123-456-7890</p></html>";
            default -> "";
        };
        JOptionPane.showMessageDialog(this, message, item, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void dispose() {
        // Unregister from theme change notifications
        UITheme.removeThemeChangeListener(this);
        super.dispose();
    }
}