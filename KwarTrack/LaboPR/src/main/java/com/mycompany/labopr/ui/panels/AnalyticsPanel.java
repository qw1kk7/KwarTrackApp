package com.mycompany.labopr.ui.panels;

import com.mycompany.labopr.data.AnalyticsData;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AnalyticsPanel extends JPanel implements UITheme.ThemeChangeListener {
    private JFrame parentFrame;
    private JComboBox<String> monthSelector;
    private String currentMonth;
    private JPanel chartsPanel;
    private JPanel metricsPanel;
    private JPanel topCategoriesPanel;
    
    public AnalyticsPanel(JFrame parent) {
        this.parentFrame = parent;
        this.currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.PRIMARY_GREEN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        UITheme.addThemeChangeListener(this);
        
        initComponents();
        loadAnalytics();
    }
    
    private void initComponents() {
        // Top panel with title and month selector
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Financial Analytics");
        titleLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 32));
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Month selector panel
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        monthPanel.setOpaque(false);
        
        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 16));
        monthLabel.setForeground(UITheme.TEXT_COLOR);
        
        String[] months = generateMonthOptions();
        monthSelector = new JComboBox<>(months);
        monthSelector.setSelectedItem(currentMonth);
        monthSelector.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        monthSelector.setPreferredSize(new Dimension(150, 30));
        monthSelector.addActionListener(e -> {
            currentMonth = (String) monthSelector.getSelectedItem();
            loadAnalytics();
        });
        
        monthPanel.add(monthLabel);
        monthPanel.add(monthSelector);
        
        topPanel.add(monthPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - main content area with scroll
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(UITheme.PRIMARY_GREEN);
        
        // Charts panel
        chartsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        chartsPanel.setOpaque(false);
        mainContentPanel.add(chartsPanel);
        
        mainContentPanel.add(Box.createVerticalStrut(20));
        
        // Metrics panel
        metricsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        metricsPanel.setOpaque(false);
        mainContentPanel.add(metricsPanel);
        
        mainContentPanel.add(Box.createVerticalStrut(20));
        
        // Top categories panel
        topCategoriesPanel = new JPanel();
        topCategoriesPanel.setLayout(new BoxLayout(topCategoriesPanel, BoxLayout.Y_AXIS));
        topCategoriesPanel.setOpaque(false);
        mainContentPanel.add(topCategoriesPanel);
        
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_GREEN);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with export button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        JButton exportBtn = ButtonFactory.createRoundedButton("Export Report");
        exportBtn.setPreferredSize(new Dimension(150, 45));
        exportBtn.addActionListener(e -> handleExportReport());
        
        bottomPanel.add(exportBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private String[] generateMonthOptions() {
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        
        cal.add(Calendar.MONTH, -11);
        
        for (int i = 0; i < 24; i++) {
            months.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
        }
        
        return months.toArray(new String[0]);
    }
    
    private void loadAnalytics() {
        // Clear existing content
        chartsPanel.removeAll();
        metricsPanel.removeAll();
        topCategoriesPanel.removeAll();
        
        // Load charts
        loadCharts();
        
        // Load metrics
        loadMetrics();
        
        // Load top categories
        loadTopCategories();
        
        // Refresh display
        revalidate();
        repaint();
    }
    
    private void loadCharts() {
        // Spending by Category (Pie Chart)
        Map<String, Double> categorySpending = AnalyticsData.getSpendingByCategory(currentMonth);
        ChartPanel pieChart = new ChartPanel("Spending by Category", categorySpending);
        chartsPanel.add(pieChart);
        
        // Income vs Expenses Trend (Bar Chart) - show current month and 2 previous months
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        List<String> trendMonths = new ArrayList<>();
        
        try {
            cal.setTime(sdf.parse(currentMonth));
        } catch (Exception e) {
            cal = Calendar.getInstance();
        }
        
        cal.add(Calendar.MONTH, -2);
        for (int i = 0; i < 3; i++) {
            trendMonths.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
        }
        
        Map<String, AnalyticsData.MonthlyData> trendData = 
            AnalyticsData.getIncomeExpensesTrend(trendMonths);
        ChartPanel barChart = new ChartPanel("Income vs Expenses Trend", trendData, true);
        chartsPanel.add(barChart);
    }
    
    private void loadMetrics() {
        double totalIncome = AnalyticsData.getTotalIncome(currentMonth);
        double totalExpenses = AnalyticsData.getTotalExpenses(currentMonth);
        double netSavings = AnalyticsData.getNetSavings(currentMonth);
        double savingsRate = AnalyticsData.getSavingsRate(currentMonth);
        
        // Total Income Card
        metricsPanel.add(createMetricCard("Total Income", totalIncome, new Color(0x7ed957)));
        
        // Total Expenses Card
        metricsPanel.add(createMetricCard("Total Expenses", totalExpenses, new Color(0xe57373)));
        
        // Net Savings Card
        Color savingsColor = netSavings >= 0 ? new Color(0x66bb6a) : new Color(0xef5350);
        metricsPanel.add(createMetricCard("Net Savings", netSavings, savingsColor));
        
        // Savings Rate Card
        Color rateColor = savingsRate >= 0 ? new Color(0x66bb6a) : new Color(0xef5350);
        metricsPanel.add(createSavingsRateCard("Savings Rate", savingsRate, rateColor));
    }
    
    private JPanel createMetricCard(String label, double value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(200, 100));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        labelText.setForeground(Color.DARK_GRAY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueText = new JLabel("₱" + String.format("%,.2f", value));
        valueText.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 22));
        valueText.setForeground(accentColor);
        valueText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(labelText);
        card.add(Box.createVerticalStrut(10));
        card.add(valueText);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private JPanel createSavingsRateCard(String label, double percentage, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(200, 100));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        labelText.setForeground(Color.DARK_GRAY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueText = new JLabel(String.format("%.1f%%", percentage));
        valueText.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 22));
        valueText.setForeground(accentColor);
        valueText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(labelText);
        card.add(Box.createVerticalStrut(10));
        card.add(valueText);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private void loadTopCategories() {
        List<AnalyticsData.CategorySpending> topCategories = 
            AnalyticsData.getTopExpenseCategories(currentMonth, 3);
        
        if (topCategories.isEmpty()) {
            return;
        }
        
        // Title
        JLabel title = new JLabel("Top 3 Expense Categories");
        title.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 20));
        title.setForeground(UITheme.TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        topCategoriesPanel.add(title);
        topCategoriesPanel.add(Box.createVerticalStrut(15));
        
        // Category cards
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cardsPanel.setOpaque(false);
        
        Color[] colors = {
            new Color(0xff6b6b), // Gold-ish red for #1
            new Color(0xfeca57), // Silver-ish yellow for #2
            new Color(0x48dbfb)  // Bronze-ish blue for #3
        };
        
        for (int i = 0; i < topCategories.size(); i++) {
            AnalyticsData.CategorySpending cat = topCategories.get(i);
            cardsPanel.add(createTopCategoryCard(i + 1, cat.category, cat.amount, colors[i]));
        }
        
        topCategoriesPanel.add(cardsPanel);
    }
    
    private JPanel createTopCategoryCard(int rank, String category, double amount, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setPreferredSize(new Dimension(220, 90));
        
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 16));
        rankLabel.setForeground(accentColor);
        rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        categoryLabel.setForeground(Color.DARK_GRAY);
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel amountLabel = new JLabel("₱" + String.format("%,.2f", amount));
        amountLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 18));
        amountLabel.setForeground(Color.BLACK);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(rankLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(categoryLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(amountLabel);
        
        return card;
    }
    
    private void handleExportReport() {
        String message = String.format(
            "<html><h3>Export Analytics Report</h3>" +
            "<p>Month: %s</p>" +
            "<p>This feature will export a CSV/PDF report with:</p>" +
            "<ul>" +
            "<li>Income and expense summary</li>" +
            "<li>Category breakdown</li>" +
            "<li>Savings analysis</li>" +
            "</ul>" +
            "<p><i>Feature coming soon...</i></p></html>",
            currentMonth
        );
        
        JOptionPane.showMessageDialog(
            parentFrame,
            message,
            "Export Report",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public void onThemeChanged() {
        setBackground(UITheme.PRIMARY_GREEN);
        
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            }
        }
        
        repaint();
    }
    
    private void updatePanelTheme(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(UITheme.TEXT_COLOR);
            } else if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            }
        }
    }
    
    public void cleanup() {
        UITheme.removeThemeChangeListener(this);
    }
}