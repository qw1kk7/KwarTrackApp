package com.mycompany.labopr.views;

import com.mycompany.labopr.views.GUI;
import com.mycompany.labopr.ui.panels.SettingsPanel;
import com.mycompany.labopr.ui.panels.BudgetsPanel;
import com.mycompany.labopr.ui.panels.AnalyticsPanel;
import com.mycompany.labopr.ui.factories.PanelFactory;
import com.mycompany.labopr.ui.factories.SidebarFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import com.mycompany.labopr.ui.panels.TransactionsPanel;
import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame implements UITheme.ThemeChangeListener {

    private JPanel sidebarPanel;
    private JPanel dashboardPanel;
    private JPanel mainContentArea;
    private JButton[] sidebarButtons;
    private TransactionsPanel transactionsPanel;
    private BudgetsPanel budgetsPanel;
    private AnalyticsPanel analyticsPanel;
    private SettingsPanel settingsPanel;
    
    // Placeholder colors for different sections
    private static final Color[] SECTION_COLORS_LIGHT = {
        new Color(0xffebee), // Dashboard - light red
        new Color(0xe3f2fd), // Transactions - light blue
        new Color(0xf3e5f5), // Budgets/Goals - light purple
        new Color(0xe8f5e9), // Analytics - light green
        new Color(0xfff3e0)  // Settings - light orange
    };
    
    private static final Color[] SECTION_COLORS_DARK = {
        new Color(0x2c1a1a), // Dashboard - dark red
        new Color(0x1a2432), // Transactions - dark blue
        new Color(0x2a1f2e), // Budgets/Goals - dark purple
        new Color(0x1f2b1f), // Analytics - dark green
        new Color(0x2e2419)  // Settings - dark orange
    };

    public MainApp() {
        setTitle("KwarTrack - Main Application");
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
        // Create sidebar
        sidebarPanel = SidebarFactory.createSidebarPanel();
        
        // Sidebar buttons
        String[] buttonLabels = {"Dashboard (Home)", "Transactions", "Budgets/Goals", "Analytics", "Settings"};
        sidebarButtons = new JButton[buttonLabels.length + 1]; // +1 for logout
        
        // Add navigation buttons
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton btn = SidebarFactory.createSidebarButton(buttonLabels[i], false);
            final int index = i;
            btn.addActionListener(e -> handleNavigation(index));
            sidebarButtons[i] = btn;
            sidebarPanel.add(btn);
            if (i < buttonLabels.length - 1) {
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        // Add spacing before logout button
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutBtn = SidebarFactory.createSidebarButton("Logout", true);
        logoutBtn.addActionListener(e -> handleLogout());
        sidebarButtons[buttonLabels.length] = logoutBtn;
        sidebarPanel.add(logoutBtn);
        
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content area
        mainContentArea = new JPanel(new BorderLayout());
        
        // Top dashboard panel
        dashboardPanel = PanelFactory.createNavPanel();
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel dashboardLabel = new JLabel("Dashboard Panel - Placeholder", SwingConstants.CENTER);
        dashboardLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 24));
        dashboardLabel.setForeground(UITheme.TEXT_COLOR);
        dashboardPanel.add(dashboardLabel);
        
        mainContentArea.add(dashboardPanel, BorderLayout.NORTH);
        
        // Center content area (will change based on navigation)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome! Select a menu item from the sidebar.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 18));
        welcomeLabel.setForeground(UITheme.TEXT_COLOR);
        centerPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        mainContentArea.add(centerPanel, BorderLayout.CENTER);
        add(mainContentArea, BorderLayout.CENTER);
        
        // Set initial active button
        SidebarFactory.setActiveButton(sidebarButtons, 0);
    }

    private void handleNavigation(int index) {
        // Set the clicked button as active
        SidebarFactory.setActiveButton(sidebarButtons, index);
        
        // Remove current center panel
        Component centerComp = ((BorderLayout) mainContentArea.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            mainContentArea.remove(centerComp);
        }
        
        // Remove dashboard panel too if present
        Component northComp = ((BorderLayout) mainContentArea.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (northComp == dashboardPanel) {
            mainContentArea.remove(northComp);
        }
        
        // Add appropriate panel based on selection
        if (index == 1) { // Transactions
            if (transactionsPanel == null) {
                transactionsPanel = new TransactionsPanel(this);
            }
            mainContentArea.add(transactionsPanel, BorderLayout.CENTER);
            mainContentArea.setBackground(UITheme.PRIMARY_GREEN);
        } else if (index == 2) { // Budgets/Goals
            if (budgetsPanel == null) {
                budgetsPanel = new BudgetsPanel(this);
            }
            mainContentArea.add(budgetsPanel, BorderLayout.CENTER);
            mainContentArea.setBackground(UITheme.PRIMARY_GREEN);
        } else if (index == 3) { // Analytics
            if (analyticsPanel == null) {
                analyticsPanel = new AnalyticsPanel(this);
            }
            mainContentArea.add(analyticsPanel, BorderLayout.CENTER);
            mainContentArea.setBackground(UITheme.PRIMARY_GREEN);
        } else if (index == 4) { // Settings
            if (settingsPanel == null) {
                settingsPanel = new SettingsPanel(this);
            }
            mainContentArea.add(settingsPanel, BorderLayout.CENTER);
            mainContentArea.setBackground(UITheme.PRIMARY_GREEN);
        } else if (index == 0) { // Dashboard (Home)
            // Show dashboard panel and placeholder
            mainContentArea.add(dashboardPanel, BorderLayout.NORTH);
            
            JPanel centerPanel = new JPanel(new BorderLayout());
            Color newColor = UITheme.isDarkMode() ? SECTION_COLORS_DARK[0] : SECTION_COLORS_LIGHT[0];
            centerPanel.setBackground(newColor);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel label = new JLabel("Dashboard (Home) - Coming Soon!", SwingConstants.CENTER);
            label.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 32));
            label.setForeground(UITheme.isDarkMode() ? Color.WHITE : new Color(0x333333));
            centerPanel.add(label, BorderLayout.CENTER);
            
            mainContentArea.add(centerPanel, BorderLayout.CENTER);
            mainContentArea.setBackground(newColor);
        }
        
        mainContentArea.revalidate();
        mainContentArea.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new GUI().setVisible(true);
        }
    }

    @Override
    public void onThemeChanged() {
        updateTheme();
    }

    private void updateTheme() {
        // Update sidebar
        SidebarFactory.updateSidebarBackground(sidebarPanel);
        
        // Update dashboard panel
        dashboardPanel.setBackground(UITheme.PRIMARY_GREEN);
        Component[] dashComponents = dashboardPanel.getComponents();
        for (Component comp : dashComponents) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(UITheme.TEXT_COLOR);
            }
        }
        
        // Update main content area
        mainContentArea.setBackground(UITheme.PRIMARY_GREEN);
        
        // Update center panel
        Component centerComp = ((BorderLayout) mainContentArea.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp instanceof JPanel) {
            JPanel centerPanel = (JPanel) centerComp;
            centerPanel.setBackground(UITheme.PRIMARY_GREEN);
            Component[] centerComponents = centerPanel.getComponents();
            for (Component comp : centerComponents) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setForeground(UITheme.TEXT_COLOR);
                }
            }
        }
        
        // Update all sidebar buttons
        for (JButton btn : sidebarButtons) {
            btn.repaint();
        }
        
        repaint();
    }

    @Override
    public void dispose() {
        // Cleanup transactions panel if it exists
        if (transactionsPanel != null) {
            transactionsPanel.cleanup();
        }
        
        // Cleanup budgets panel if it exists
        if (budgetsPanel != null) {
            budgetsPanel.cleanup();
        }
        
        // Cleanup analytics panel if it exists
        if (analyticsPanel != null) {
            analyticsPanel.cleanup();
        }
        
        // Cleanup settings panel if it exists
        if (settingsPanel != null) {
            settingsPanel.cleanup();
        }
        
        // Unregister from theme change notifications
        UITheme.removeThemeChangeListener(this);
        super.dispose();
    }
}