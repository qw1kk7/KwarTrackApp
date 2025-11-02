package com.mycompany.labopr.ui.factories;

import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonFactory {

    // Hover colors
    private static final Color LIGHT_HOVER_COLOR = Color.decode("#cfffba");
    private static final Color DARK_HOVER_COLOR = Color.LIGHT_GRAY;

    public static JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.BUTTON_RADIUS, UITheme.BUTTON_RADIUS);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBackground(UITheme.BUTTON_BG);
        btn.setForeground(UITheme.BUTTON_TEXT);
        btn.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, UITheme.FONT_BUTTON));
        btn.setPreferredSize(UITheme.ACTION_BUTTON_SIZE);
        btn.setMaximumSize(UITheme.ACTION_BUTTON_SIZE);
        
        // Add hover effect
        addHoverEffect(btn, UITheme.BUTTON_BG);
        
        return btn;
    }

    public static JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBackground(UITheme.BUTTON_BG);
        btn.setForeground(UITheme.isDarkMode() ? Color.BLACK : UITheme.BUTTON_TEXT);
        btn.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, UITheme.FONT_NAV));
        btn.setPreferredSize(UITheme.NAV_BUTTON_SIZE);
        
        // Add hover effect
        addHoverEffect(btn, UITheme.BUTTON_BG);
        
        return btn;
    }

    public static JButton createDarkModeButton() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        updateDarkModeButtonAppearance(btn);
        
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 12));
        btn.setPreferredSize(UITheme.DARKMODE_BUTTON_SIZE);
        btn.setMaximumSize(UITheme.DARKMODE_BUTTON_SIZE);
        
        // Store original colors for hover effect
        Color originalBg = btn.getBackground();
        addHoverEffect(btn, originalBg);
        
        btn.addActionListener(e -> {
            UITheme.toggleDarkMode();
            updateDarkModeButtonAppearance(btn);
            // Update hover effect with new colors
            Color newOriginalBg = btn.getBackground();
            addHoverEffect(btn, newOriginalBg);
        });
        
        return btn;
    }

    private static void updateDarkModeButtonAppearance(JButton btn) {
        if (UITheme.isDarkMode()) {
            btn.setText("Light Mode");
            btn.setBackground(UITheme.LIGHT_BUTTON_BG);
            btn.setForeground(Color.BLACK);
        } else {
            btn.setText("Dark Mode");
            btn.setBackground(Color.DARK_GRAY);
            btn.setForeground(Color.WHITE);
        }
    }
    
    private static void addHoverEffect(JButton btn, Color originalBg) {
        // Remove existing mouse listeners to avoid duplicates
        for (MouseAdapter adapter : btn.getListeners(MouseAdapter.class)) {
            btn.removeMouseListener(adapter);
        }
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color hoverColor = UITheme.isDarkMode() ? DARK_HOVER_COLOR : LIGHT_HOVER_COLOR;
                btn.setBackground(hoverColor);
                
                // Set text color to black when hovering in both modes
                btn.setForeground(Color.BLACK);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalBg);
                
                // Restore original text color based on button type and theme
                if (btn.getText().equals("Dark Mode")) {
                    btn.setForeground(Color.WHITE);
                } else if (btn.getText().equals("Light Mode")) {
                    btn.setForeground(Color.BLACK);
                } else {
                    // For nav and action buttons
                    if (UITheme.isDarkMode()) {
                        btn.setForeground(Color.BLACK);
                    } else {
                        btn.setForeground(UITheme.BUTTON_TEXT);
                    }
                }
                btn.repaint();
            }
        });
    }
    
    // Method to update button appearance when theme changes
    public static void updateButtonForThemeChange(JButton btn) {
        if (btn.getText().equals("Dark Mode") || btn.getText().equals("Light Mode")) {
            // Dark mode button is handled by its own method
            return;
        }
        
        // Check if this is a nav button (smaller size indicates nav button)
        boolean isNavButton = btn.getPreferredSize().equals(UITheme.NAV_BUTTON_SIZE);
        
        Color newBg;
        Color newFg;
        
        if (isNavButton) {
            // Nav buttons have white background in dark mode
            newBg = UITheme.isDarkMode() ? Color.WHITE : UITheme.BUTTON_BG;
            newFg = UITheme.isDarkMode() ? Color.BLACK : UITheme.BUTTON_TEXT;
        } else {
            // Action buttons use theme colors
            newBg = UITheme.BUTTON_BG;
            newFg = UITheme.isDarkMode() ? Color.BLACK : UITheme.BUTTON_TEXT;
        }
        
        btn.setBackground(newBg);
        btn.setForeground(newFg);
        
        // Re-add hover effect with new colors
        addHoverEffect(btn, newBg);
        btn.repaint();
    }
}