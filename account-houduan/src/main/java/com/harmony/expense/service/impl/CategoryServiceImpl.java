package com.harmony.expense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harmony.expense.entity.Category;
import com.harmony.expense.mapper.CategoryMapper;
import com.harmony.expense.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listByType(Integer type) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, type)
                .orderByAsc(Category::getId));
    }
}