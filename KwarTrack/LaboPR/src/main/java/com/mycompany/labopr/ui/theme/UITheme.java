package com.mycompany.labopr.ui.theme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class UITheme {
    // Light theme colors
    public static final Color LIGHT_PRIMARY_GREEN = new Color(0x7ed957);
    public static final Color LIGHT_TEXT_COLOR = Color.WHITE;
    public static final Color LIGHT_BUTTON_TEXT = new Color(0x7ed957);
    public static final Color LIGHT_BUTTON_BG = Color.WHITE;
    
    // Dark theme colors
    public static final Color DARK_PRIMARY_BG = Color.BLACK;
    public static final Color DARK_TEXT_COLOR = Color.WHITE;
    public static final Color DARK_BUTTON_TEXT = Color.WHITE;
    public static final Color DARK_BUTTON_BG = new Color(0x333333);

    // Current theme state
    private static boolean isDarkMode = false;
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    // Dynamic colors that change based on theme
    public static Color PRIMARY_GREEN = LIGHT_PRIMARY_GREEN;
    public static Color TEXT_COLOR = LIGHT_TEXT_COLOR;
    public static Color BUTTON_TEXT = LIGHT_BUTTON_TEXT;
    public static Color BUTTON_BG = LIGHT_BUTTON_BG;

    public static final String FONT_FAMILY = "Poppins";
    public static final int FONT_LARGE = 56;
    public static final int FONT_MEDIUM = 20;
    public static final int FONT_BUTTON = 18;
    public static final int FONT_NAV = 14;

    public static final Dimension NAV_BUTTON_SIZE = new Dimension(100, 35);
    public static final Dimension ACTION_BUTTON_SIZE = new Dimension(250, 60);
    public static final Dimension DARKMODE_BUTTON_SIZE = new Dimension(120, 30);
    public static final int BUTTON_RADIUS = 25;

    public static final int PANEL_PADDING_TOP = 30;
    public static final int PANEL_PADDING_BOTTOM = 30;

    private static final Map<String, ImageIcon> logoCache = new HashMap<>();

    // Interface for components that need to update when theme changes
    public interface ThemeChangeListener {
        void onThemeChanged();
    }

    // Register a listener for theme changes
    public static void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    // Remove a listener
    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    // Toggle between light and dark mode
    public static void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        updateColors();
        notifyListeners();
    }

    // Get current theme state
    public static boolean isDarkMode() {
        return isDarkMode;
    }

    // Update colors based on current theme
    private static void updateColors() {
        if (isDarkMode) {
            PRIMARY_GREEN = DARK_PRIMARY_BG;
            TEXT_COLOR = DARK_TEXT_COLOR;
            BUTTON_TEXT = DARK_BUTTON_TEXT;
            BUTTON_BG = DARK_BUTTON_BG;
        } else {
            PRIMARY_GREEN = LIGHT_PRIMARY_GREEN;
            TEXT_COLOR = LIGHT_TEXT_COLOR;
            BUTTON_TEXT = LIGHT_BUTTON_TEXT;
            BUTTON_BG = LIGHT_BUTTON_BG;
        }
    }

    // Notify all registered listeners of theme change
    private static void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged();
        }
    }

    public static ImageIcon getCachedLogo(String path, int width, int height) {
        String key = path + "_" + width + "x" + height;
        if (logoCache.containsKey(key)) return logoCache.get(key);
        try {
            ImageIcon icon = new ImageIcon(UITheme.class.getResource(path));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);
            logoCache.put(key, scaledIcon);
            return scaledIcon;
        } catch (Exception e) {
            return null;
        }
    }
}