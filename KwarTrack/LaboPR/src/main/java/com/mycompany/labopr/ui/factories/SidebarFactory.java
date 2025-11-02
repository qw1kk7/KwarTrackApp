package com.mycompany.labopr.ui.factories;

import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarFactory {

    private static final Color SIDEBAR_BG_LIGHT = new Color(0xf5f5f5);
    private static final Color SIDEBAR_BG_DARK = new Color(0x1a1a1a);
    private static final Color BUTTON_HOVER_LIGHT = new Color(0xe0e0e0);
    private static final Color BUTTON_HOVER_DARK = new Color(0x2d2d2d);
    private static final Color BUTTON_ACTIVE_LIGHT = new Color(0x7ed957);
    private static final Color BUTTON_ACTIVE_DARK = new Color(0x5ca03f);

    public static JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        updateSidebarBackground(sidebar);
        return sidebar;
    }

    // Custom button class to hold state
    private static class SidebarButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;

        public SidebarButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Determine background color
            Color bgColor;
            if (isActive) {
                bgColor = UITheme.isDarkMode() ? BUTTON_ACTIVE_DARK : BUTTON_ACTIVE_LIGHT;
            } else if (isHovered) {
                bgColor = UITheme.isDarkMode() ? BUTTON_HOVER_DARK : BUTTON_HOVER_LIGHT;
            } else {
                bgColor = UITheme.isDarkMode() ? SIDEBAR_BG_DARK : SIDEBAR_BG_LIGHT;
            }
            
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.dispose();
            super.paintComponent(g);
        }

        public void setActive(boolean active) {
            this.isActive = active;
            updateTextStyle();
            repaint();
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint();
        }

        private void updateTextStyle() {
            if (isActive) {
                setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 14));
                setForeground(UITheme.isDarkMode() ? Color.WHITE : Color.BLACK);
            } else {
                setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
                setForeground(UITheme.isDarkMode() ? new Color(0xcccccc) : new Color(0x333333));
            }
        }
    }

    public static JButton createSidebarButton(String text, boolean isLogout) {
        SidebarButton btn = new SidebarButton(text);

        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Set initial text style
        btn.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        btn.setForeground(UITheme.isDarkMode() ? new Color(0xcccccc) : new Color(0x333333));

        // Add hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((SidebarButton) e.getSource()).setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((SidebarButton) e.getSource()).setHovered(false);
            }
        });

        return btn;
    }

    public static void updateSidebarBackground(JPanel sidebar) {
        sidebar.setBackground(UITheme.isDarkMode() ? SIDEBAR_BG_DARK : SIDEBAR_BG_LIGHT);
    }

    // Method to reset all buttons to inactive state
    public static void resetAllButtons(JButton[] buttons) {
        for (JButton btn : buttons) {
            if (btn instanceof SidebarButton) {
                ((SidebarButton) btn).setActive(false);
            }
        }
    }

    // Method to set a specific button as active
    public static void setActiveButton(JButton[] buttons, int index) {
        resetAllButtons(buttons);
        if (index >= 0 && index < buttons.length && buttons[index] instanceof SidebarButton) {
            ((SidebarButton) buttons[index]).setActive(true);
        }
    }
}