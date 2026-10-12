package com.harmony.expense.controller;

import com.harmony.expense.common.Result;
import com.harmony.expense.dto.CategoryStatisticsDTO;
import com.harmony.expense.dto.ExpenseDTO;
import com.harmony.expense.dto.ExpenseQueryDTO;
import com.harmony.expense.dto.MonthlyStatisticsDTO;
import com.harmony.expense.dto.PageResult;
import com.harmony.expense.entity.Expense;
import com.harmony.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "记账管理", description = "记账记录的增删改查与统计")
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "添加记账")
    @PostMapping
    public Result<Void> addExpense(HttpServletRequest request, @Valid @RequestBody ExpenseDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        expenseService.addExpense(userId, dto);
        return Result.success();
    }

    @Operation(summary = "修改记账")
    @PutMapping("/{id}")
    public Result<Void> updateExpense(HttpServletRequest request,
                                       @Parameter(description = "记账ID") @PathVariable Long id,
                                       @Valid @RequestBody ExpenseDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        expenseService.updateExpense(userId, id, dto);
        return Result.success();
    }

    @Operation(summary = "删除记账")
    @DeleteMapping("/{id}")
    public Result<Void> deleteExpense(HttpServletRequest request,
                                       @Parameter(description = "记账ID") @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        expenseService.deleteExpense(userId, id);
        return Result.success();
    }

    @Operation(summary = "分页查询记账列表")
    @GetMapping("/list")
    public Result<PageResult<Expense>> listExpenses(HttpServletRequest request, ExpenseQueryDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<Expense> result = expenseService.getUserExpenses(userId, dto);
        return Result.success(result);
    }

    @Operation(summary = "获取单条记账详情")
    @GetMapping("/{id}")
    public Result<Expense> getExpense(HttpServletRequest request,
                                       @Parameter(description = "记账ID") @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        Expense expense = expenseService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Expense>()
                        .eq(Expense::getId, id)
                        .eq(Expense::getUserId, userId));
        if (expense == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(expense);
    }

    @Operation(summary = "分类统计（饼图数据）")
    @GetMapping("/statistics/category")
    public Result<List<CategoryStatisticsDTO>> categoryStatistics(
            HttpServletRequest request,
            @Parameter(description = "收支类型 0=支出 1=收入") @RequestParam Integer type,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(expenseService.categoryStatistics(userId, type, startDate, endDate));
    }

    @Operation(summary = "月度趋势（柱状图数据）")
    @GetMapping("/statistics/monthly")
    public Result<List<MonthlyStatisticsDTO>> monthlyStatistics(
            HttpServletRequest request,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(expenseService.monthlyStatistics(userId, startDate, endDate));
    }
}