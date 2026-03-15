package com.budgetbuddy.service;

import com.budgetbuddy.model.Income;
import com.budgetbuddy.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> getAllIncomesByUserId(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional
    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    @Transactional
    public void deleteIncome(Long incomeId, Long userId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        
        if (!income.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        
        incomeRepository.delete(income);
    }

    public List<Income> getIncomesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }
}
