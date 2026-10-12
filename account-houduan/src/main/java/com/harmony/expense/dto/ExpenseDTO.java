package com.harmony.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseDTO {

    @NotNull(message = "类型不能为空")
    private Integer type;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private LocalDate expenseDate;

    private String remark;
}