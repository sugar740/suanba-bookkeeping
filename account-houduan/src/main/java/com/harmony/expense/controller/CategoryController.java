package com.harmony.expense.controller;

import com.harmony.expense.common.Result;
import com.harmony.expense.entity.Category;
import com.harmony.expense.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "收支分类列表查询")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list(@Parameter(description = "收支类型 0=支出 1=收入，不传返回全部")
                                        @RequestParam(required = false) Integer type) {
        if (type != null) {
            return Result.success(categoryService.listByType(type));
        }
        return Result.success(categoryService.list());
    }
}