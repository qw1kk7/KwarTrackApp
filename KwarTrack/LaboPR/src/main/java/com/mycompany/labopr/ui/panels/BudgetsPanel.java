package com.mycompany.labopr.ui.panels;

import com.mycompany.labopr.data.BudgetData;
import com.mycompany.labopr.data.TransactionData;
import com.mycompany.labopr.ui.dialogs.BudgetGoalDialog;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BudgetsPanel extends JPanel implements UITheme.ThemeChangeListener {
    private JFrame parentFrame;
    private JComboBox<String> monthSelector;
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private String currentMonth;
    
    public BudgetsPanel(JFrame parent) {
        this.parentFrame = parent;
        this.currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.PRIMARY_GREEN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        UITheme.addThemeChangeListener(this);
        
        initComponents();
        loadBudgets();
    }
    
    private void initComponents() {
        // Top panel with title and month selector
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Monthly Budget Goals");
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
        
        // Generate month options (current month + 11 months back and 12 months forward)
        String[] months = generateMonthOptions();
        monthSelector = new JComboBox<>(months);
        monthSelector.setSelectedItem(currentMonth);
        monthSelector.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        monthSelector.setPreferredSize(new Dimension(150, 30));
        monthSelector.addActionListener(e -> {
            currentMonth = (String) monthSelector.getSelectedItem();
            loadBudgets();
        });
        
        monthPanel.add(monthLabel);
        monthPanel.add(monthSelector);
        
        topPanel.add(monthPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with budget table
        String[] columnNames = {"Category", "Goal (₱)", "Spent (₱)", "Remaining (₱)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        budgetTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Color the Status column based on status
                if (column == 4) {
                    String status = (String) getValueAt(row, column);
                    if (status.equals("Under Budget")) {
                        c.setBackground(new Color(0xc8e6c9)); // Light green
                        c.setForeground(new Color(0x2e7d32)); // Dark green
                    } else if (status.equals("Nearing Limit")) {
                        c.setBackground(new Color(0xfff9c4)); // Light yellow
                        c.setForeground(new Color(0xf57f17)); // Dark yellow
                    } else if (status.equals("Overspent")) {
                        c.setBackground(new Color(0xffcdd2)); // Light red
                        c.setForeground(new Color(0xc62828)); // Dark red
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    if (!isRowSelected(row)) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                return c;
            }
        };
        
        budgetTable.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        budgetTable.setRowHeight(35);
        budgetTable.getTableHeader().setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 14));
        budgetTable.getTableHeader().setBackground(UITheme.BUTTON_BG);
        budgetTable.getTableHeader().setForeground(UITheme.BUTTON_TEXT);
        
        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        budgetTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        budgetTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        budgetTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        budgetTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Add double-click listener to edit goals
        budgetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = budgetTable.getSelectedRow();
                    if (row >= 0) {
                        handleEditGoal(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        JButton addEditBtn = ButtonFactory.createRoundedButton("Add/Edit Goal");
        addEditBtn.setPreferredSize(new Dimension(150, 45));
        addEditBtn.addActionListener(e -> handleAddEditGoal());
        
        JButton setAllBtn = ButtonFactory.createRoundedButton("Set All Goals");
        setAllBtn.setPreferredSize(new Dimension(150, 45));
        setAllBtn.addActionListener(e -> handleSetAllGoals());
        
        JButton saveBtn = ButtonFactory.createRoundedButton("Save Goals");
        saveBtn.setPreferredSize(new Dimension(150, 45));
        saveBtn.addActionListener(e -> handleSaveGoals());
        
        bottomPanel.add(addEditBtn);
        bottomPanel.add(setAllBtn);
        bottomPanel.add(saveBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private String[] generateMonthOptions() {
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        
        // Go back 11 months
        cal.add(Calendar.MONTH, -11);
        
        // Generate 24 months (11 back + current + 12 forward)
        for (int i = 0; i < 24; i++) {
            months.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
        }
        
        return months.toArray(new String[0]);
    }
    
    private void loadBudgets() {
        tableModel.setRowCount(0);
        
        // Get all relevant categories for this month
        Set<String> categories = BudgetData.getAllRelevantCategories(currentMonth);
        
        // If no categories, show all expense categories
        if (categories.isEmpty()) {
            categories = TransactionData.getCategories("Expenses");
        }
        
        List<String> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);
        
        for (String category : sortedCategories) {
            Double goalAmount = BudgetData.getBudgetGoal(category, currentMonth);
            double goal = (goalAmount != null) ? goalAmount : 0.0;
            double spent = BudgetData.getSpentForCategoryAndMonth(category, currentMonth);
            double remaining = goal - spent;
            
            BudgetData.BudgetStatus status = BudgetData.calculateStatus(goal, spent);
            String statusText;
            switch (status) {
                case UNDER_BUDGET:
                    statusText = "Under Budget";
                    break;
                case NEARING_LIMIT:
                    statusText = "Nearing Limit";
                    break;
                case OVERSPENT:
                    statusText = "Overspent";
                    break;
                default:
                    statusText = "-";
            }
            
            // Only show status if there's a goal set
            if (goal == 0) {
                statusText = "-";
            }
            
            tableModel.addRow(new Object[]{
                category,
                goal > 0 ? String.format("%.2f", goal) : "-",
                spent > 0 ? String.format("%.2f", spent) : "0.00",
                goal > 0 ? String.format("%.2f", remaining) : "-",
                statusText
            });
        }
    }
    
    private void handleAddEditGoal() {
        BudgetGoalDialog dialog = new BudgetGoalDialog(parentFrame, currentMonth);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadBudgets();
        }
    }
    
    private void handleEditGoal(int row) {
        String category = (String) tableModel.getValueAt(row, 0);
        String goalStr = (String) tableModel.getValueAt(row, 1);
        
        double currentGoal = 0.0;
        if (!goalStr.equals("-")) {
            try {
                currentGoal = Double.parseDouble(goalStr);
            } catch (NumberFormatException ignored) {
            }
        }
        
        BudgetGoalDialog dialog = new BudgetGoalDialog(parentFrame, currentMonth, category, currentGoal);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadBudgets();
        }
    }
    
    private void handleSetAllGoals() {
        BudgetGoalDialog dialog = new BudgetGoalDialog(parentFrame, currentMonth, true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadBudgets();
        }
    }
    
    private void handleSaveGoals() {
        JOptionPane.showMessageDialog(
            parentFrame,
            "Budget goals have been saved successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    @Override
    public void onThemeChanged() {
        setBackground(UITheme.PRIMARY_GREEN);
        
        // Update all labels
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            }
        }
        
        budgetTable.getTableHeader().setBackground(UITheme.BUTTON_BG);
        budgetTable.getTableHeader().setForeground(UITheme.BUTTON_TEXT);
        
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