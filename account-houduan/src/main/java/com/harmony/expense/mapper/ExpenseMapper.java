package com.harmony.expense.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harmony.expense.dto.CategoryStatisticsDTO;
import com.harmony.expense.dto.MonthlyStatisticsDTO;
import com.harmony.expense.entity.Expense;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseMapper extends BaseMapper<Expense> {

    @Select("SELECT r.category_id, c.name AS category_name, " +
            "SUM(r.amount) AS total_amount, COUNT(*) AS count " +
            "FROM expense_record r " +
            "JOIN category c ON c.id = r.category_id " +
            "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
            "AND r.type = #{type} " +
            "AND r.expense_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY r.category_id, c.name " +
            "ORDER BY total_amount DESC")
    List<CategoryStatisticsDTO> categoryStatistics(@Param("userId") Long userId,
                                                    @Param("type") Integer type,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    @Select("SELECT DATE_FORMAT(r.expense_date, '%Y-%m') AS period, " +
            "SUM(CASE WHEN r.type = 1 THEN r.amount ELSE 0 END) AS income, " +
            "SUM(CASE WHEN r.type = 0 THEN r.amount ELSE 0 END) AS expense " +
            "FROM expense_record r " +
            "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
            "AND r.expense_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY period " +
            "ORDER BY period")
    List<MonthlyStatisticsDTO> monthlyStatistics(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}