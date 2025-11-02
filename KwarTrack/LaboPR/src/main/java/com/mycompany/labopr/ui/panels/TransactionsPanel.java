package com.mycompany.labopr.ui.panels;

import com.mycompany.labopr.ui.factories.ButtonFactory;
import com.mycompany.labopr.data.TransactionData;
import com.mycompany.labopr.ui.dialogs.TransactionDialog;
import com.mycompany.labopr.ui.theme.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionsPanel extends JPanel implements UITheme.ThemeChangeListener {
    private JFrame parentFrame;
    private JLabel balanceLabel;
    private JButton expensesBtn;
    private JButton incomeBtn;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private String currentType = "Expenses";
    private NumberFormat currencyFormat;
    
    public TransactionsPanel(JFrame parent) {
        this.parentFrame = parent;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0xe8f5e9));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        UITheme.addThemeChangeListener(this);
        
        // Check for starting balance
        checkAndSetBalance();
        
        initComponents();
        loadTransactions();
    }
    
    private void checkAndSetBalance() {
        Double balance = TransactionData.getBalance();
        if (balance == null) {
            String input = JOptionPane.showInputDialog(
                parentFrame,
                "Enter your starting balance (₱):",
                "Set Starting Balance",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double startBalance = Double.parseDouble(input.trim());
                    if (startBalance >= 0) {
                        TransactionData.setBalance(startBalance);
                    } else {
                        JOptionPane.showMessageDialog(
                            parentFrame,
                            "Balance must be non-negative. Setting to 0.",
                            "Invalid Balance",
                            JOptionPane.WARNING_MESSAGE
                        );
                        TransactionData.setBalance(0);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "Invalid number format. Setting balance to 0.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    TransactionData.setBalance(0);
                }
            } else {
                TransactionData.setBalance(0);
            }
        }
    }
    
    private void initComponents() {
        // Top panel with balance and toggle buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        // Balance label
        double currentBalance = TransactionData.calculateCurrentBalance();
        balanceLabel = new JLabel("Total Balance: ₱" + String.format("%,.2f", currentBalance));
        balanceLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 28));
        balanceLabel.setForeground(UITheme.TEXT_COLOR);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(balanceLabel, BorderLayout.NORTH);
        
        // Toggle buttons panel
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        togglePanel.setOpaque(false);
        
        expensesBtn = createToggleButton("Expenses");
        incomeBtn = createToggleButton("Income");
        
        expensesBtn.addActionListener(e -> switchView("Expenses"));
        incomeBtn.addActionListener(e -> switchView("Income"));
        
        togglePanel.add(expensesBtn);
        togglePanel.add(incomeBtn);
        
        topPanel.add(togglePanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        String[] columnNames = {"Date", "Category", "Amount", "Comment"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 14));
        transactionTable.setRowHeight(30);
        transactionTable.getTableHeader().setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(UITheme.BUTTON_BG);
        transactionTable.getTableHeader().setForeground(UITheme.BUTTON_TEXT);
        
        // Center align amount column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with Add button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        JButton addBtn = ButtonFactory.createRoundedButton("Add");
        addBtn.setPreferredSize(new Dimension(150, 50));
        addBtn.addActionListener(e -> handleAddTransaction());
        
        bottomPanel.add(addBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Set initial active button
        updateToggleButtons();
    }
    
    private JButton createToggleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(150, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        
        return btn;
    }
    
    private void updateToggleButtons() {
        if (currentType.equals("Expenses")) {
            expensesBtn.setBackground(UITheme.BUTTON_BG);
            expensesBtn.setForeground(UITheme.BUTTON_TEXT);
            incomeBtn.setBackground(UITheme.PRIMARY_GREEN);
            incomeBtn.setForeground(UITheme.TEXT_COLOR);
        } else {
            incomeBtn.setBackground(UITheme.BUTTON_BG);
            incomeBtn.setForeground(UITheme.BUTTON_TEXT);
            expensesBtn.setBackground(UITheme.PRIMARY_GREEN);
            expensesBtn.setForeground(UITheme.TEXT_COLOR);
        }
    }
    
    private void switchView(String type) {
        currentType = type;
        updateToggleButtons();
        loadTransactions();
    }
    
    private void loadTransactions() {
        tableModel.setRowCount(0);
        
        List<TransactionData.Transaction> transactions = 
            TransactionData.getTransactionsByType(currentType);
        
        for (TransactionData.Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.date,
                t.category,
                "₱" + String.format("%,.2f", t.amount),
                t.comment
            });
        }
        
        // Update balance
        double currentBalance = TransactionData.calculateCurrentBalance();
        balanceLabel.setText("Total Balance: ₱" + String.format("%,.2f", currentBalance));
    }
    
    private void handleAddTransaction() {
        TransactionDialog dialog = new TransactionDialog(parentFrame, currentType);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadTransactions();
        }
    }
    
    @Override
    public void onThemeChanged() {
        setBackground(UITheme.PRIMARY_GREEN);
        balanceLabel.setForeground(UITheme.TEXT_COLOR);
        updateToggleButtons();
        
        transactionTable.getTableHeader().setBackground(UITheme.BUTTON_BG);
        transactionTable.getTableHeader().setForeground(UITheme.BUTTON_TEXT);
        
        repaint();
    }
    
    public void cleanup() {
        UITheme.removeThemeChangeListener(this);
    }
}