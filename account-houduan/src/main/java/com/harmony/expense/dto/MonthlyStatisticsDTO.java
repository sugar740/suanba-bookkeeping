package com.harmony.expense.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyStatisticsDTO {

    private String period;

    private BigDecimal income;

    private BigDecimal expense;
}