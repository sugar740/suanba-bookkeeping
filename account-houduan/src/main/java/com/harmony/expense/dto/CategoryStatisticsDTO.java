package com.harmony.expense.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryStatisticsDTO {

    private Long categoryId;

    private String categoryName;

    private BigDecimal totalAmount;

    private int count;
}