package com.harmony.expense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harmony.expense.dto.CategoryStatisticsDTO;
import com.harmony.expense.dto.ExpenseDTO;
import com.harmony.expense.dto.ExpenseQueryDTO;
import com.harmony.expense.dto.MonthlyStatisticsDTO;
import com.harmony.expense.dto.PageResult;
import com.harmony.expense.entity.Expense;
import com.harmony.expense.mapper.ExpenseMapper;
import com.harmony.expense.service.ExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {

    @Override
    public void addExpense(Long userId, ExpenseDTO dto) {
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setType(dto.getType());
        expense.setAmount(dto.getAmount());
        expense.setCategoryId(dto.getCategoryId());
        expense.setExpenseDate(dto.getExpenseDate() != null ? dto.getExpenseDate() : LocalDate.now());
        expense.setRemark(dto.getRemark());
        save(expense);
    }

    @Override
    public void updateExpense(Long userId, Long expenseId, ExpenseDTO dto) {
        Expense expense = getOne(new LambdaQueryWrapper<Expense>()
                .eq(Expense::getId, expenseId)
                .eq(Expense::getUserId, userId));
        if (expense == null) {
            throw new IllegalArgumentException("记账记录不存在");
        }

        expense.setType(dto.getType());
        expense.setAmount(dto.getAmount());
        expense.setCategoryId(dto.getCategoryId());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setRemark(dto.getRemark());
        updateById(expense);
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = getOne(new LambdaQueryWrapper<Expense>()
                .eq(Expense::getId, expenseId)
                .eq(Expense::getUserId, userId));
        if (expense == null) {
            throw new IllegalArgumentException("记账记录不存在");
        }
        removeById(expenseId);
    }

    @Override
    public PageResult<Expense> getUserExpenses(Long userId, ExpenseQueryDTO dto) {
        LambdaQueryWrapper<Expense> wrapper = new LambdaQueryWrapper<Expense>()
                .eq(Expense::getUserId, userId)
                .eq(dto.getType() != null, Expense::getType, dto.getType())
                .eq(dto.getCategoryId() != null, Expense::getCategoryId, dto.getCategoryId())
                .ge(dto.getStartDate() != null, Expense::getExpenseDate, dto.getStartDate())
                .le(dto.getEndDate() != null, Expense::getExpenseDate, dto.getEndDate())
                .like(StringUtils.hasText(dto.getRemark()), Expense::getRemark, dto.getRemark())
                .orderByDesc(Expense::getExpenseDate, Expense::getCreateTime);

        Page<Expense> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<Expense> result = page(page, wrapper);

        return new PageResult<>(
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getRecords()
        );
    }

    @Override
    public List<CategoryStatisticsDTO> categoryStatistics(Long userId, Integer type,
                                                           LocalDate startDate, LocalDate endDate) {
        return baseMapper.categoryStatistics(userId, type, startDate, endDate);
    }

    @Override
    public List<MonthlyStatisticsDTO> monthlyStatistics(Long userId,
                                                         LocalDate startDate, LocalDate endDate) {
        return baseMapper.monthlyStatistics(userId, startDate, endDate);
    }
}