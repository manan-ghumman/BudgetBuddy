package com.budgetbuddy.controller;

import com.budgetbuddy.security.UserDetailsImpl;
import com.budgetbuddy.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private com.budgetbuddy.service.BudgetService budgetService;

    @Autowired
    private com.budgetbuddy.service.IncomeService incomeService;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats(@RequestParam(required = false) String month) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        
        if (month != null) {
            // handle custom month if needed, but for now current month
        }

        List<Object[]> categoryTotals = expenseService.getCategoryTotals(userId, startDate, endDate);
        
        Map<String, Double> spendingByCategory = categoryTotals.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> ((Number) obj[1]).doubleValue()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("spendingByCategory", spendingByCategory);
        double totalSpending = spendingByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        stats.put("totalMonthlySpending", totalSpending);
        
        double totalMonthlyIncome = incomeService.getIncomesByDateRange(userId, startDate, endDate).stream()
                .mapToDouble(i -> i.getAmount().doubleValue())
                .sum();
        stats.put("totalMonthlyIncome", totalMonthlyIncome);
        
        double totalBudgetLimit = budgetService.getBudgetsByUserId(userId).stream()
                .mapToDouble(b -> b.getMonthlyLimit().doubleValue())
                .sum();
        stats.put("totalBudgetLimit", totalBudgetLimit);
        
        long totalExpensesCount = expenseService.getAllExpensesByUserId(userId).size();
        stats.put("totalExpensesCount", totalExpensesCount);
        
        return stats;
    }
}
