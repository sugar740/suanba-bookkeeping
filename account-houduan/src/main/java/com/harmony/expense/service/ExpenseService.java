package com.harmony.expense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.harmony.expense.dto.CategoryStatisticsDTO;
import com.harmony.expense.dto.ExpenseDTO;
import com.harmony.expense.dto.ExpenseQueryDTO;
import com.harmony.expense.dto.MonthlyStatisticsDTO;
import com.harmony.expense.dto.PageResult;
import com.harmony.expense.entity.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService extends IService<Expense> {

    void addExpense(Long userId, ExpenseDTO dto);

    void updateExpense(Long userId, Long expenseId, ExpenseDTO dto);

    void deleteExpense(Long userId, Long expenseId);

    PageResult<Expense> getUserExpenses(Long userId, ExpenseQueryDTO dto);

    List<CategoryStatisticsDTO> categoryStatistics(Long userId, Integer type,
                                                   LocalDate startDate, LocalDate endDate);

    List<MonthlyStatisticsDTO> monthlyStatistics(Long userId,
                                                  LocalDate startDate, LocalDate endDate);
}