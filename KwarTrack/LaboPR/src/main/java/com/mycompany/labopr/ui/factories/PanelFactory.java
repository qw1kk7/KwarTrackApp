package com.mycompany.labopr.ui.factories;

import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;

public class PanelFactory {

    public static JPanel createNavPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(UITheme.PRIMARY_GREEN);
        return panel;
    }

    public static JLabel createLogoLabel(String path, int width, int height) {
        JLabel label = new JLabel();
        ImageIcon icon = UITheme.getCachedLogo(path, width, height);
        if (icon != null) label.setIcon(icon);
        else {
            label.setText("KwarTrack");
            label.setForeground(UITheme.TEXT_COLOR);
            label.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, UITheme.FONT_LARGE));
        }
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static JLabel createLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(UITheme.TEXT_COLOR);
        label.setFont(new Font(UITheme.FONT_FAMILY, bold ? Font.BOLD : Font.PLAIN, fontSize));
        return label;
    }
}