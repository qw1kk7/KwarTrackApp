package com.mycompany.labopr.data;

import com.mycompany.labopr.data.TransactionData;
import java.util.*;

public class AnalyticsData {
    
    // Get total income for a specific month
    public static double getTotalIncome(String month) {
        List<TransactionData.Transaction> incomeTransactions = 
            TransactionData.getTransactionsByType("Income");
        
        double total = 0.0;
        for (TransactionData.Transaction t : incomeTransactions) {
            String transactionMonth = t.date.length() >= 7 ? t.date.substring(0, 7) : "";
            if (transactionMonth.equals(month)) {
                total += t.amount;
            }
        }
        return total;
    }
    
    // Get total expenses for a specific month
    public static double getTotalExpenses(String month) {
        List<TransactionData.Transaction> expenseTransactions = 
            TransactionData.getTransactionsByType("Expenses");
        
        double total = 0.0;
        for (TransactionData.Transaction t : expenseTransactions) {
            String transactionMonth = t.date.length() >= 7 ? t.date.substring(0, 7) : "";
            if (transactionMonth.equals(month)) {
                total += t.amount;
            }
        }
        return total;
    }
    
    // Calculate net savings
    public static double getNetSavings(String month) {
        return getTotalIncome(month) - getTotalExpenses(month);
    }
    
    // Calculate savings rate as percentage
    public static double getSavingsRate(String month) {
        double income = getTotalIncome(month);
        if (income == 0) return 0.0;
        
        double savings = getNetSavings(month);
        return (savings / income) * 100;
    }
    
    // Get spending by category for a month
    public static Map<String, Double> getSpendingByCategory(String month) {
        List<TransactionData.Transaction> expenseTransactions = 
            TransactionData.getTransactionsByType("Expenses");
        
        Map<String, Double> categorySpending = new HashMap<>();
        
        for (TransactionData.Transaction t : expenseTransactions) {
            String transactionMonth = t.date.length() >= 7 ? t.date.substring(0, 7) : "";
            if (transactionMonth.equals(month)) {
                categorySpending.put(
                    t.category, 
                    categorySpending.getOrDefault(t.category, 0.0) + t.amount
                );
            }
        }
        
        return categorySpending;
    }
    
    // Get top N expense categories
    public static List<CategorySpending> getTopExpenseCategories(String month, int topN) {
        Map<String, Double> spending = getSpendingByCategory(month);
        
        List<CategorySpending> categories = new ArrayList<>();
        for (Map.Entry<String, Double> entry : spending.entrySet()) {
            categories.add(new CategorySpending(entry.getKey(), entry.getValue()));
        }
        
        // Sort by amount descending
        categories.sort((a, b) -> Double.compare(b.amount, a.amount));
        
        // Return top N
        return categories.subList(0, Math.min(topN, categories.size()));
    }
    
    // Get income vs expenses trend for multiple months
    public static Map<String, MonthlyData> getIncomeExpensesTrend(List<String> months) {
        Map<String, MonthlyData> trend = new LinkedHashMap<>();
        
        for (String month : months) {
            double income = getTotalIncome(month);
            double expenses = getTotalExpenses(month);
            trend.put(month, new MonthlyData(income, expenses));
        }
        
        return trend;
    }
    
    // Helper class for category spending
    public static class CategorySpending {
        public String category;
        public double amount;
        
        public CategorySpending(String category, double amount) {
            this.category = category;
            this.amount = amount;
        }
    }
    
    // Helper class for monthly data
    public static class MonthlyData {
        public double income;
        public double expenses;
        
        public MonthlyData(double income, double expenses) {
            this.income = income;
            this.expenses = expenses;
        }
    }
}