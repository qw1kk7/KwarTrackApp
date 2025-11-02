package com.mycompany.labopr.data;

import java.io.*;
import java.util.*;

public class TransactionData {
    private static final String BALANCE_FILE = "balance.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String CATEGORIES_FILE = "categories.txt";
    
    // Default categories
    private static final Set<String> DEFAULT_EXPENSE_CATEGORIES = new HashSet<>(Arrays.asList(
        "Health", "Leisure", "Home", "Food", "Education", "Gifts", 
        "Groceries", "Family", "Workout", "Transportation", "Other"
    ));
    
    private static final Set<String> DEFAULT_INCOME_CATEGORIES = new HashSet<>(Arrays.asList(
        "Paycheck", "Gift", "Interest", "Other"
    ));
    
    // Get or set starting balance
    public static Double getBalance() {
        try (BufferedReader br = new BufferedReader(new FileReader(BALANCE_FILE))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Double.parseDouble(line.trim());
            }
        } catch (IOException | NumberFormatException ignored) {
        }
        return null;
    }
    
    public static void setBalance(double balance) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BALANCE_FILE))) {
            bw.write(String.valueOf(balance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Calculate current balance based on transactions
    public static double calculateCurrentBalance() {
        Double startBalance = getBalance();
        if (startBalance == null) return 0.0;
        
        double current = startBalance;
        List<Transaction> transactions = getAllTransactions();
        
        for (Transaction t : transactions) {
            if (t.type.equals("Income")) {
                current += t.amount;
            } else {
                current -= t.amount;
            }
        }
        
        return current;
    }
    
    // Save a transaction
    public static void saveTransaction(Transaction transaction) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            bw.write(transaction.toFileString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Get all transactions
    public static List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transaction t = Transaction.fromFileString(line);
                if (t != null) transactions.add(t);
            }
        } catch (IOException ignored) {
        }
        return transactions;
    }
    
    // Get transactions by type
    public static List<Transaction> getTransactionsByType(String type) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : getAllTransactions()) {
            if (t.type.equals(type)) {
                filtered.add(t);
            }
        }
        return filtered;
    }
    
    // Category management
    public static Set<String> getCategories(String type) {
        Set<String> categories = new HashSet<>();
        if (type.equals("Expenses")) {
            categories.addAll(DEFAULT_EXPENSE_CATEGORIES);
        } else {
            categories.addAll(DEFAULT_INCOME_CATEGORIES);
        }
        
        // Add custom categories
        categories.addAll(getCustomCategories(type));
        
        return categories;
    }
    
    public static void addCustomCategory(String type, String category) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CATEGORIES_FILE, true))) {
            bw.write(type + ":" + category);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Set<String> getCustomCategories(String type) {
        Set<String> categories = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && parts[0].equals(type)) {
                    categories.add(parts[1]);
                }
            }
        } catch (IOException ignored) {
        }
        return categories;
    }
    
    // Transaction class
    public static class Transaction {
        public String type; // "Expenses" or "Income"
        public String date;
        public String category;
        public double amount;
        public String comment;
        
        public Transaction(String type, String date, String category, double amount, String comment) {
            this.type = type;
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.comment = comment;
        }
        
        public String toFileString() {
            return type + "|" + date + "|" + category + "|" + amount + "|" + comment;
        }
        
        public static Transaction fromFileString(String line) {
            String[] parts = line.split("\\|", 5);
            if (parts.length == 5) {
                try {
                    return new Transaction(
                        parts[0],
                        parts[1],
                        parts[2],
                        Double.parseDouble(parts[3]),
                        parts[4]
                    );
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }
}