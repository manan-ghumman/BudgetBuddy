package com.budgetbuddy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {
    @NotBlank
    private String category;

    @NotNull
    private BigDecimal monthlyLimit;
}
