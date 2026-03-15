package com.budgetbuddy.controller;

import com.budgetbuddy.dto.ExpenseRequest;
import com.budgetbuddy.dto.MessageResponse;
import com.budgetbuddy.model.Expense;
import com.budgetbuddy.model.User;
import com.budgetbuddy.repository.UserRepository;
import com.budgetbuddy.security.UserDetailsImpl;
import com.budgetbuddy.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tr-exp")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpensesByUserId(getCurrentUserId());
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        User user = userRepository.findById(getCurrentUserId()).get();

        Expense expense = Expense.builder()
                .description(expenseRequest.getDescription())
                .amount(expenseRequest.getAmount())
                .category(expenseRequest.getCategory())
                .date(expenseRequest.getDate())
                .user(user)
                .build();

        expenseService.addExpense(expense);
        return ResponseEntity.ok(new MessageResponse("Expense added successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id, getCurrentUserId());
        return ResponseEntity.ok(new MessageResponse("Expense deleted successfully"));
    }

    @GetMapping("/range")
    public List<Expense> getExpensesByRange(@RequestParam String startDate, @RequestParam String endDate) {
        return expenseService.getExpensesByDateRange(
                getCurrentUserId(),
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense expense = Expense.builder()
                .description(expenseRequest.getDescription())
                .amount(expenseRequest.getAmount())
                .category(expenseRequest.getCategory())
                .date(expenseRequest.getDate())
                .build();

        expenseService.updateExpense(id, expense, getCurrentUserId());
        return ResponseEntity.ok(new MessageResponse("Expense updated successfully"));
    }
}