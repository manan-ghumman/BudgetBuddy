package com.budgetbuddy.controller;

import com.budgetbuddy.dto.IncomeRequest;
import com.budgetbuddy.dto.MessageResponse;
import com.budgetbuddy.model.Income;
import com.budgetbuddy.model.User;
import com.budgetbuddy.repository.UserRepository;
import com.budgetbuddy.security.UserDetailsImpl;
import com.budgetbuddy.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Income> getAllIncomes() {
        return incomeService.getAllIncomesByUserId(getCurrentUserId());
    }

    @PostMapping
    public ResponseEntity<?> addIncome(@Valid @RequestBody IncomeRequest incomeRequest) {
        User user = userRepository.findById(getCurrentUserId()).get();

        Income income = Income.builder()
                .source(incomeRequest.getSource())
                .amount(incomeRequest.getAmount())
                .date(incomeRequest.getDate())
                .user(user)
                .build();

        incomeService.addIncome(income);
        return ResponseEntity.ok(new MessageResponse("Income added successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id, getCurrentUserId());
        return ResponseEntity.ok(new MessageResponse("Income deleted successfully"));
    }
}
