package com.mycompany.labopr.ui.dialogs;

import com.mycompany.labopr.data.TransactionData;
import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionDialog extends JDialog {
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JTextField dateField;
    private JTextArea commentArea;
    private boolean confirmed = false;
    private String transactionType;
    
    public TransactionDialog(JFrame parent, String type) {
        super(parent, "Add " + type, true);
        this.transactionType = type;
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.PRIMARY_GREEN);
        
        // Amount field
        mainPanel.add(createFieldPanel("Amount (₱):", amountField = new JTextField(15)));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Category dropdown
        Set<String> categories = TransactionData.getCategories(transactionType);
        String[] categoryArray = categories.toArray(new String[0]);
        Arrays.sort(categoryArray);
        
        // Add "Create Category" option
        String[] categoriesWithCreate = new String[categoryArray.length + 1];
        System.arraycopy(categoryArray, 0, categoriesWithCreate, 0, categoryArray.length);
        categoriesWithCreate[categoryArray.length] = "➕ Create Category";
        
        categoryCombo = new JComboBox<>(categoriesWithCreate);
        categoryCombo.setMaximumSize(new Dimension(300, 30));
        categoryCombo.addActionListener(e -> handleCategorySelection());
        
        JPanel categoryPanel = createFieldPanel("Category:", categoryCombo);
        mainPanel.add(categoryPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Date field with current date
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 15);
        mainPanel.add(createFieldPanel("Date (YYYY-MM-DD):", dateField));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Comment area
        JLabel commentLabel = new JLabel("Comment (optional):");
        commentLabel.setForeground(UITheme.TEXT_COLOR);
        commentLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(commentLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        commentArea = new JTextArea(4, 15);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setMaximumSize(new Dimension(400, 100));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollPane);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.PRIMARY_GREEN);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton addBtn = ButtonFactory.createRoundedButton("Add");
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.addActionListener(e -> handleAdd());
        
        JButton cancelBtn = ButtonFactory.createRoundedButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);
        
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
        
        field.setMaximumSize(new Dimension(400, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        
        return panel;
    }
    
    private void handleCategorySelection() {
        if (categoryCombo.getSelectedItem().toString().equals("➕ Create Category")) {
            String newCategory = JOptionPane.showInputDialog(
                this,
                "Enter new category name:",
                "Create Category",
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (newCategory != null && !newCategory.trim().isEmpty()) {
                newCategory = newCategory.trim();
                TransactionData.addCustomCategory(transactionType, newCategory);
                
                // Refresh combo box
                Set<String> categories = TransactionData.getCategories(transactionType);
                String[] categoryArray = categories.toArray(new String[0]);
                Arrays.sort(categoryArray);
                
                String[] categoriesWithCreate = new String[categoryArray.length + 1];
                System.arraycopy(categoryArray, 0, categoriesWithCreate, 0, categoryArray.length);
                categoriesWithCreate[categoryArray.length] = "➕ Create Category";
                
                categoryCombo.setModel(new DefaultComboBoxModel<>(categoriesWithCreate));
                categoryCombo.setSelectedItem(newCategory);
            } else {
                categoryCombo.setSelectedIndex(0);
            }
        }
    }
    
    private void handleAdd() {
        // Validate amount
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate category
        String category = (String) categoryCombo.getSelectedItem();
        if (category == null || category.equals("➕ Create Category")) {
            JOptionPane.showMessageDialog(this, "Please select a category.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate date
        String date = dateField.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String comment = commentArea.getText().trim();
        
        // Save transaction
        TransactionData.Transaction transaction = new TransactionData.Transaction(
            transactionType, date, category, amount, comment
        );
        TransactionData.saveTransaction(transaction);
        
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}