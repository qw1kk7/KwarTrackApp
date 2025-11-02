package com.mycompany.labopr.data;

import com.mycompany.labopr.data.TransactionData;
import java.io.*;
import java.util.*;

public class SettingsData {
    private static final String SETTINGS_FILE = "settings.txt";
    
    // Setting keys
    public static final String DISPLAY_NAME = "display_name";
    public static final String EMAIL = "email";
    public static final String CURRENCY = "currency";
    public static final String DATE_FORMAT = "date_format";
    public static final String THEME_MODE = "theme_mode";
    public static final String ACCENT_COLOR = "accent_color";
    
    // Default values
    private static final Map<String, String> DEFAULT_SETTINGS = new HashMap<>();
    static {
        DEFAULT_SETTINGS.put(DISPLAY_NAME, "User");
        DEFAULT_SETTINGS.put(EMAIL, "user@example.com");
        DEFAULT_SETTINGS.put(CURRENCY, "PHP (₱)");
        DEFAULT_SETTINGS.put(DATE_FORMAT, "YYYY-MM-DD");
        DEFAULT_SETTINGS.put(THEME_MODE, "light");
        DEFAULT_SETTINGS.put(ACCENT_COLOR, "Green");
    }
    
    // Get a setting value
    public static String getSetting(String key) {
        Map<String, String> settings = getAllSettings();
        return settings.getOrDefault(key, DEFAULT_SETTINGS.get(key));
    }
    
    // Set a setting value
    public static void setSetting(String key, String value) {
        Map<String, String> settings = getAllSettings();
        settings.put(key, value);
        saveAllSettings(settings);
    }
    
    // Get all settings
    public static Map<String, String> getAllSettings() {
        Map<String, String> settings = new HashMap<>(DEFAULT_SETTINGS);
        
        try (BufferedReader br = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    settings.put(parts[0], parts[1]);
                }
            }
        } catch (IOException ignored) {
        }
        
        return settings;
    }
    
    // Save all settings
    private static void saveAllSettings(Map<String, String> settings) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                bw.write(entry.getKey() + "=" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Reset all data (transactions, budgets, balance)
    public static boolean resetAllData() {
        try {
            // Delete data files
            new File("balance.txt").delete();
            new File("transactions.txt").delete();
            new File("budgets.txt").delete();
            new File("categories.txt").delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Export data to CSV
    public static boolean exportData(String filepath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            // Export balance
            Double balance = TransactionData.getBalance();
            if (balance != null) {
                bw.write("BALANCE," + balance);
                bw.newLine();
            }
            
            // Export transactions
            bw.write("TRANSACTIONS");
            bw.newLine();
            bw.write("Type,Date,Category,Amount,Comment");
            bw.newLine();
            
            List<TransactionData.Transaction> transactions = TransactionData.getAllTransactions();
            for (TransactionData.Transaction t : transactions) {
                bw.write(String.format("%s,%s,%s,%.2f,\"%s\"", 
                    t.type, t.date, t.category, t.amount, t.comment));
                bw.newLine();
            }
            
            // Export budgets
            bw.newLine();
            bw.write("BUDGETS");
            bw.newLine();
            bw.write("Category,Month,Goal");
            bw.newLine();
            
            List<BudgetData.BudgetGoal> budgets = BudgetData.getAllBudgetGoals();
            for (BudgetData.BudgetGoal b : budgets) {
                bw.write(String.format("%s,%s,%.2f", b.category, b.month, b.goal));
                bw.newLine();
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Import data from CSV (simplified - assumes correct format)
    public static boolean importData(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            String section = "";
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                if (line.startsWith("BALANCE,")) {
                    double balance = Double.parseDouble(line.split(",")[1]);
                    TransactionData.setBalance(balance);
                } else if (line.equals("TRANSACTIONS")) {
                    section = "TRANSACTIONS";
                    br.readLine(); // Skip header
                } else if (line.equals("BUDGETS")) {
                    section = "BUDGETS";
                    br.readLine(); // Skip header
                } else if (section.equals("TRANSACTIONS")) {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 5);
                    if (parts.length == 5) {
                        String comment = parts[4].replaceAll("\"", "");
                        TransactionData.Transaction t = new TransactionData.Transaction(
                            parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), comment
                        );
                        TransactionData.saveTransaction(t);
                    }
                } else if (section.equals("BUDGETS")) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        BudgetData.BudgetGoal b = new BudgetData.BudgetGoal(
                            parts[0], parts[1], Double.parseDouble(parts[2])
                        );
                        BudgetData.saveBudgetGoal(b);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}