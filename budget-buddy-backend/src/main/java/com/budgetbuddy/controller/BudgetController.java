package com.budgetbuddy.controller;

import com.budgetbuddy.dto.BudgetRequest;
import com.budgetbuddy.dto.MessageResponse;
import com.budgetbuddy.model.Budget;
import com.budgetbuddy.model.User;
import com.budgetbuddy.repository.UserRepository;
import com.budgetbuddy.security.UserDetailsImpl;
import com.budgetbuddy.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Budget> getUserBudgets() {
        return budgetService.getBudgetsByUserId(getCurrentUserId());
    }

    @PostMapping
    public ResponseEntity<?> setBudget(@Valid @RequestBody BudgetRequest budgetRequest) {
        User user = userRepository.findById(getCurrentUserId()).get();

        Budget budget = Budget.builder()
                .category(budgetRequest.getCategory())
                .monthlyLimit(budgetRequest.getMonthlyLimit())
                .user(user)
                .build();

        budgetService.saveOrUpdateBudget(budget);
        return ResponseEntity.ok(new MessageResponse("Budget set successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id, getCurrentUserId());
        return ResponseEntity.ok(new MessageResponse("Budget deleted successfully"));
    }
}
