package com.budgetbuddy.service;

import com.budgetbuddy.model.Budget;
import com.budgetbuddy.model.User;
import com.budgetbuddy.repository.BudgetRepository;
import com.budgetbuddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Transactional
    public Budget saveOrUpdateBudget(Budget budgetRequest) {
        Long userId = budgetRequest.getUser().getId();
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategory(userId, budgetRequest.getCategory());
        
        if (existingBudget.isPresent()) {
            Budget budgetToUpdate = existingBudget.get();
            budgetToUpdate.setMonthlyLimit(budgetRequest.getMonthlyLimit());
            return budgetRepository.save(budgetToUpdate);
        } else {
            return budgetRepository.save(budgetRequest);
        }
    }

    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this budget");
        }
        
        budgetRepository.delete(budget);
    }
}
