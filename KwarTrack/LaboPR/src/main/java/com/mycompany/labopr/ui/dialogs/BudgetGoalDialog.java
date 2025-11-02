package com.mycompany.labopr.ui.dialogs;

import com.mycompany.labopr.data.BudgetData;
import com.mycompany.labopr.data.TransactionData;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BudgetGoalDialog extends JDialog {
    private JComboBox<String> categoryCombo;
    private JTextField goalField;
    private Map<String, JTextField> categoryFields;
    private boolean confirmed = false;
    private String selectedMonth;
    private boolean isSetAllMode;
    
    // Constructor for single goal (add/edit)
    public BudgetGoalDialog(JFrame parent, String month) {
        this(parent, month, null, 0, false);
    }
    
    // Constructor for editing specific category
    public BudgetGoalDialog(JFrame parent, String month, String category, double currentGoal) {
        this(parent, month, category, currentGoal, false);
    }
    
    // Constructor for set all goals mode
    public BudgetGoalDialog(JFrame parent, String month, boolean setAllMode) {
        this(parent, month, null, 0, setAllMode);
    }
    
    private BudgetGoalDialog(JFrame parent, String month, String category, double currentGoal, boolean setAllMode) {
        super(parent, setAllMode ? "Set All Budget Goals" : (category == null ? "Add Budget Goal" : "Edit Budget Goal"), true);
        this.selectedMonth = month;
        this.isSetAllMode = setAllMode;
        
        if (setAllMode) {
            setSize(500, 600);
            this.categoryFields = new HashMap<>();
        } else {
            setSize(400, 250);
        }
        
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        if (setAllMode) {
            initSetAllComponents();
        } else {
            initSingleGoalComponents(category, currentGoal);
        }
    }
    
    // Initialize components for single goal mode
    private void initSingleGoalComponents(String preselectedCategory, double currentGoal) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.PRIMARY_GREEN);
        
        // Month label
        JLabel monthLabel = new JLabel("Month: " + selectedMonth);
        monthLabel.setForeground(UITheme.TEXT_COLOR);
        monthLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 16));
        monthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(monthLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Category dropdown
        Set<String> categories = TransactionData.getCategories("Expenses");
        String[] categoryArray = categories.toArray(new String[0]);
        Arrays.sort(categoryArray);
        
        categoryCombo = new JComboBox<>(categoryArray);
        categoryCombo.setMaximumSize(new Dimension(350, 30));
        
        if (preselectedCategory != null) {
            categoryCombo.setSelectedItem(preselectedCategory);
            categoryCombo.setEnabled(false); // Don't allow changing category when editing
        }
        
        JPanel categoryPanel = createFieldPanel("Category:", categoryCombo);
        mainPanel.add(categoryPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Goal field
        goalField = new JTextField(15);
        if (currentGoal > 0) {
            goalField.setText(String.valueOf(currentGoal));
        }
        mainPanel.add(createFieldPanel("Goal Amount (₱):", goalField));
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.PRIMARY_GREEN);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton saveBtn = ButtonFactory.createRoundedButton("Save");
        saveBtn.setPreferredSize(new Dimension(100, 40));
        saveBtn.addActionListener(e -> handleSaveSingle());
        
        JButton cancelBtn = ButtonFactory.createRoundedButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Initialize components for set all goals mode
    private void initSetAllComponents() {
        // Top panel with month label
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.PRIMARY_GREEN);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel monthLabel = new JLabel("Set Goals for Month: " + selectedMonth);
        monthLabel.setForeground(UITheme.TEXT_COLOR);
        monthLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 18));
        topPanel.add(monthLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with scrollable list of categories
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(UITheme.PRIMARY_GREEN);
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Get all expense categories
        Set<String> categories = TransactionData.getCategories("Expenses");
        List<String> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);
        
        // Get existing goals for this month
        Map<String, Double> existingGoals = BudgetData.getBudgetGoalsForMonth(selectedMonth);
        
        for (String category : sortedCategories) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setOpaque(false);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            
            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setForeground(UITheme.TEXT_COLOR);
            categoryLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
            categoryLabel.setPreferredSize(new Dimension(200, 30));
            
            JTextField goalField = new JTextField(10);
            goalField.setPreferredSize(new Dimension(150, 30));
            
            // Pre-fill existing goal if available
            if (existingGoals.containsKey(category)) {
                goalField.setText(String.valueOf(existingGoals.get(category)));
            }
            
            categoryFields.put(category, goalField);
            
            rowPanel.add(categoryLabel, BorderLayout.WEST);
            
            JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            fieldWrapper.setOpaque(false);
            JLabel pesoLabel = new JLabel("₱ ");
            pesoLabel.setForeground(UITheme.TEXT_COLOR);
            pesoLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
            fieldWrapper.add(pesoLabel);
            fieldWrapper.add(goalField);
            
            rowPanel.add(fieldWrapper, BorderLayout.CENTER);
            
            categoriesPanel.add(rowPanel);
            categoriesPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_GREEN);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.PRIMARY_GREEN);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton saveBtn = ButtonFactory.createRoundedButton("Save All");
        saveBtn.setPreferredSize(new Dimension(120, 40));
        saveBtn.addActionListener(e -> handleSaveAll());
        
        JButton cancelBtn = ButtonFactory.createRoundedButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setForeground(UITheme.TEXT_COLOR);
        label.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setMaximumSize(new Dimension(350, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        
        return panel;
    }
    
    private void handleSaveSingle() {
        // Validate category
        String category = (String) categoryCombo.getSelectedItem();
        if (category == null || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate goal
        String goalText = goalField.getText().trim();
        if (goalText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a goal amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double goal;
        try {
            goal = Double.parseDouble(goalText);
            if (goal < 0) {
                JOptionPane.showMessageDialog(this, "Goal must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Save budget goal
        BudgetData.BudgetGoal budgetGoal = new BudgetData.BudgetGoal(category, selectedMonth, goal);
        BudgetData.saveBudgetGoal(budgetGoal);
        
        confirmed = true;
        dispose();
    }
    
    private void handleSaveAll() {
        List<BudgetData.BudgetGoal> goals = new ArrayList<>();
        
        for (Map.Entry<String, JTextField> entry : categoryFields.entrySet()) {
            String category = entry.getKey();
            String goalText = entry.getValue().getText().trim();
            
            if (!goalText.isEmpty()) {
                try {
                    double goal = Double.parseDouble(goalText);
                    if (goal >= 0) {
                        goals.add(new BudgetData.BudgetGoal(category, selectedMonth, goal));
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid amount for category: " + category,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
        }
        
        if (goals.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter at least one goal.",
                "Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        BudgetData.saveBudgetGoals(goals);
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}