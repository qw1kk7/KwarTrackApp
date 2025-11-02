package com.mycompany.labopr.views;

import com.mycompany.labopr.ui.dialogs.AuthDialogs;
import com.mycompany.labopr.ui.factories.PanelFactory;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame implements UITheme.ThemeChangeListener {

    private JPanel logoPanel;
    private JPanel contentPanel;
    private JPanel buttonPanel;
    private JLabel title;
    private JLabel subtitle;

    public GUI() {
        setTitle("Main Menu");
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
        // Top panel with dark mode button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JPanel darkModePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        darkModePanel.setOpaque(false);
        darkModePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        darkModePanel.add(ButtonFactory.createDarkModeButton());
        topPanel.add(darkModePanel, BorderLayout.NORTH);

        // Logo panel
        logoPanel = new JPanel();
        logoPanel.setBorder(BorderFactory.createEmptyBorder(UITheme.PANEL_PADDING_TOP, 0, 20, 0));
        logoPanel.add(PanelFactory.createLogoLabel("/KLOGO.png", 300, 300));
        topPanel.add(logoPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Middle content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(Box.createVerticalGlue());
        title = PanelFactory.createLabel("Welcome to KwarTrack!", UITheme.FONT_LARGE, true);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle = PanelFactory.createLabel("Your personal finance tracker and manager", UITheme.FONT_MEDIUM, false);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(title);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(subtitle);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);

        // Bottom buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UITheme.PANEL_PADDING_TOP, 0, UITheme.PANEL_PADDING_BOTTOM, 0));

        JButton loginBtn = ButtonFactory.createRoundedButton("Login");
        loginBtn.addActionListener(e -> AuthDialogs.handleLogin(this));
        JButton signUpBtn = ButtonFactory.createRoundedButton("Sign Up");
        signUpBtn.addActionListener(e -> AuthDialogs.handleSignUp(this));

        JButton[] buttons = {loginBtn, signUpBtn};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(buttons[i]);
            if (i < buttons.length - 1) buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onThemeChanged() {
        updateTheme();
    }

    private void updateTheme() {
        getContentPane().setBackground(UITheme.PRIMARY_GREEN);
        logoPanel.setBackground(UITheme.PRIMARY_GREEN);
        title.setForeground(UITheme.TEXT_COLOR);
        subtitle.setForeground(UITheme.TEXT_COLOR);
        repaint();
    }

    @Override
    public void dispose() {
        // Unregister from theme change notifications
        UITheme.removeThemeChangeListener(this);
        super.dispose();
    }
}