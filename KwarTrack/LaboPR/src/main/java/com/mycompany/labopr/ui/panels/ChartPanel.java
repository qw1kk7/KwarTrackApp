package com.mycompany.labopr.ui.panels;

import com.mycompany.labopr.data.AnalyticsData;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ChartPanel extends JPanel {
    private String chartType;
    private Map<String, Double> pieData;
    private Map<String, AnalyticsData.MonthlyData> trendData;
    private String title;
    
    // Constructor for pie chart
    public ChartPanel(String title, Map<String, Double> data) {
        this.title = title;
        this.chartType = "pie";
        this.pieData = data;
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xcccccc), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setPreferredSize(new Dimension(400, 350));
    }
    
    // Constructor for bar/trend chart
    public ChartPanel(String title, Map<String, AnalyticsData.MonthlyData> data, boolean isTrend) {
        this.title = title;
        this.chartType = "bar";
        this.trendData = data;
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xcccccc), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setPreferredSize(new Dimension(400, 350));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw title
        g2d.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 18));
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 30);
        
        if (chartType.equals("pie")) {
            drawPieChart(g2d);
        } else if (chartType.equals("bar")) {
            drawBarChart(g2d);
        }
        
        g2d.dispose();
    }
    
    private void drawPieChart(Graphics2D g2d) {
        if (pieData == null || pieData.isEmpty()) {
            drawNoDataMessage(g2d);
            return;
        }
        
        // Calculate total
        double total = pieData.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) {
            drawNoDataMessage(g2d);
            return;
        }
        
        // Chart area
        int centerX = getWidth() / 2;
        int centerY = (getHeight() + 40) / 2;
        int radius = Math.min(getWidth(), getHeight() - 80) / 3;
        
        // Color palette
        Color[] colors = {
            new Color(0x7ed957), new Color(0x4fc3f7), new Color(0xffb74d),
            new Color(0xe57373), new Color(0x9575cd), new Color(0x81c784),
            new Color(0xffd54f), new Color(0x64b5f6), new Color(0xff8a65),
            new Color(0xba68c8), new Color(0xa1887f)
        };
        
        // Draw pie slices
        int startAngle = 0;
        int colorIndex = 0;
        List<Map.Entry<String, Double>> entries = new ArrayList<>(pieData.entrySet());
        
        for (Map.Entry<String, Double> entry : entries) {
            double percentage = (entry.getValue() / total) * 100;
            int arcAngle = (int) Math.round((entry.getValue() / total) * 360);
            
            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcAngle);
            
            startAngle += arcAngle;
            colorIndex++;
        }
        
        // Draw legend
        int legendY = getHeight() - 60;
        int legendX = 20;
        g2d.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 12));
        
        colorIndex = 0;
        int itemsPerRow = 2;
        int currentItem = 0;
        
        for (Map.Entry<String, Double> entry : entries) {
            if (currentItem >= 6) break; // Show max 6 categories in legend
            
            int x = legendX + (currentItem % itemsPerRow) * (getWidth() / 2);
            int y = legendY + (currentItem / itemsPerRow) * 20;
            
            // Color box
            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillRect(x, y - 10, 12, 12);
            
            // Label
            g2d.setColor(Color.BLACK);
            String label = entry.getKey() + " (" + String.format("%.1f%%", 
                (entry.getValue() / total) * 100) + ")";
            g2d.drawString(label, x + 18, y);
            
            colorIndex++;
            currentItem++;
        }
    }
    
    private void drawBarChart(Graphics2D g2d) {
        if (trendData == null || trendData.isEmpty()) {
            drawNoDataMessage(g2d);
            return;
        }
        
        // Chart dimensions
        int chartX = 50;
        int chartY = 60;
        int chartWidth = getWidth() - 100;
        int chartHeight = getHeight() - 140;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight); // X-axis
        g2d.drawLine(chartX, chartY, chartX, chartY + chartHeight); // Y-axis
        
        // Find max value for scaling
        double maxValue = 0;
        for (AnalyticsData.MonthlyData data : trendData.values()) {
            maxValue = Math.max(maxValue, Math.max(data.income, data.expenses));
        }
        
        if (maxValue == 0) {
            drawNoDataMessage(g2d);
            return;
        }
        
        // Draw bars
        int barWidth = (chartWidth / trendData.size()) / 3;
        int groupWidth = chartWidth / trendData.size();
        int index = 0;
        
        g2d.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 10));
        
        for (Map.Entry<String, AnalyticsData.MonthlyData> entry : trendData.entrySet()) {
            int x = chartX + index * groupWidth + groupWidth / 4;
            
            // Income bar (green)
            int incomeHeight = (int) ((entry.getValue().income / maxValue) * chartHeight);
            g2d.setColor(new Color(0x7ed957));
            g2d.fillRect(x, chartY + chartHeight - incomeHeight, barWidth, incomeHeight);
            
            // Expense bar (red)
            int expenseHeight = (int) ((entry.getValue().expenses / maxValue) * chartHeight);
            g2d.setColor(new Color(0xe57373));
            g2d.fillRect(x + barWidth + 5, chartY + chartHeight - expenseHeight, barWidth, expenseHeight);
            
            // Month label
            g2d.setColor(Color.BLACK);
            String monthLabel = entry.getKey().substring(5); // Get MM part
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(monthLabel);
            g2d.drawString(monthLabel, x + barWidth - labelWidth / 2, chartY + chartHeight + 15);
            
            index++;
        }
        
        // Draw legend
        g2d.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 12));
        int legendY = getHeight() - 20;
        
        // Income legend
        g2d.setColor(new Color(0x7ed957));
        g2d.fillRect(chartX, legendY - 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Income", chartX + 20, legendY);
        
        // Expense legend
        g2d.setColor(new Color(0xe57373));
        g2d.fillRect(chartX + 100, legendY - 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Expenses", chartX + 120, legendY);
    }
    
    private void drawNoDataMessage(Graphics2D g2d) {
        g2d.setFont(new Font(UITheme.FONT_FAMILY, Font.ITALIC, 16));
        g2d.setColor(Color.GRAY);
        String message = "No data available";
        FontMetrics fm = g2d.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        g2d.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2);
    }
}