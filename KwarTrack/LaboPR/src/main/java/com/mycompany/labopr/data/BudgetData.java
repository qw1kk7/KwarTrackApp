package com.mycompany.labopr.data;

import com.mycompany.labopr.data.TransactionData;
import java.io.*;
import java.util.*;

public class BudgetData {
    private static final String BUDGETS_FILE = "budgets.txt";
    
    // Budget goal class
    public static class BudgetGoal {
        public String category;
        public String month; // Format: YYYY-MM
        public double goal;
        
        public BudgetGoal(String category, String month, double goal) {
            this.category = category;
            this.month = month;
            this.goal = goal;
        }
        
        public String toFileString() {
            return category + "|" + month + "|" + goal;
        }
        
        public static BudgetGoal fromFileString(String line) {
            String[] parts = line.split("\\|", 3);
            if (parts.length == 3) {
                try {
                    return new BudgetGoal(parts[0], parts[1], Double.parseDouble(parts[2]));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }
    
    // Save a budget goal
    public static void saveBudgetGoal(BudgetGoal goal) {
        List<BudgetGoal> goals = getAllBudgetGoals();
        
        // Remove existing goal for same category and month
        goals.removeIf(g -> g.category.equals(goal.category) && g.month.equals(goal.month));
        
        // Add new goal
        goals.add(goal);
        
        // Write all goals back to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BUDGETS_FILE))) {
            for (BudgetGoal g : goals) {
                bw.write(g.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Save multiple budget goals at once
    public static void saveBudgetGoals(List<BudgetGoal> newGoals) {
        if (newGoals.isEmpty()) return;
        
        List<BudgetGoal> allGoals = getAllBudgetGoals();
        
        // Remove existing goals for same categories and months
        for (BudgetGoal newGoal : newGoals) {
            allGoals.removeIf(g -> g.category.equals(newGoal.category) && g.month.equals(newGoal.month));
        }
        
        // Add new goals
        allGoals.addAll(newGoals);
        
        // Write all goals back to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BUDGETS_FILE))) {
            for (BudgetGoal g : allGoals) {
                bw.write(g.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Get all budget goals
    public static List<BudgetGoal> getAllBudgetGoals() {
        List<BudgetGoal> goals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(BUDGETS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                BudgetGoal goal = BudgetGoal.fromFileString(line);
                if (goal != null) goals.add(goal);
            }
        } catch (IOException ignored) {
        }
        return goals;
    }
    
    // Get budget goals for a specific month
    public static Map<String, Double> getBudgetGoalsForMonth(String month) {
        Map<String, Double> goals = new HashMap<>();
        for (BudgetGoal goal : getAllBudgetGoals()) {
            if (goal.month.equals(month)) {
                goals.put(goal.category, goal.goal);
            }
        }
        return goals;
    }
    
    // Get budget goal for a specific category and month
    public static Double getBudgetGoal(String category, String month) {
        for (BudgetGoal goal : getAllBudgetGoals()) {
            if (goal.category.equals(category) && goal.month.equals(month)) {
                return goal.goal;
            }
        }
        return null;
    }
    
    // Calculate spending for a category in a specific month
    public static double getSpentForCategoryAndMonth(String category, String month) {
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionsByType("Expenses");
        double spent = 0.0;
        
        for (TransactionData.Transaction t : transactions) {
            // Extract YYYY-MM from transaction date
            String transactionMonth = t.date.length() >= 7 ? t.date.substring(0, 7) : "";
            
            if (t.category.equals(category) && transactionMonth.equals(month)) {
                spent += t.amount;
            }
        }
        
        return spent;
    }
    
    // Get all expense categories that have either goals or transactions for a month
    public static Set<String> getAllRelevantCategories(String month) {
        Set<String> categories = new HashSet<>();
        
        // Add categories with budget goals
        for (BudgetGoal goal : getAllBudgetGoals()) {
            if (goal.month.equals(month)) {
                categories.add(goal.category);
            }
        }
        
        // Add categories with transactions in this month
        List<TransactionData.Transaction> transactions = TransactionData.getTransactionsByType("Expenses");
        for (TransactionData.Transaction t : transactions) {
            String transactionMonth = t.date.length() >= 7 ? t.date.substring(0, 7) : "";
            if (transactionMonth.equals(month)) {
                categories.add(t.category);
            }
        }
        
        return categories;
    }
    
    // Budget status enum
    public enum BudgetStatus {
        UNDER_BUDGET,
        NEARING_LIMIT,
        OVERSPENT
    }
    
    // Calculate budget status
    public static BudgetStatus calculateStatus(double goal, double spent) {
        if (goal == 0) return BudgetStatus.UNDER_BUDGET;
        
        double percentage = (spent / goal) * 100;
        
        if (percentage >= 100) {
            return BudgetStatus.OVERSPENT;
        } else if (percentage >= 80) {
            return BudgetStatus.NEARING_LIMIT;
        } else {
            return BudgetStatus.UNDER_BUDGET;
        }
    }
}