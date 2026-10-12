package com.harmony.expense.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseQueryDTO {

    private Integer type;

    private Long categoryId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String remark;

    private int page = 1;

    private int size = 10;
}