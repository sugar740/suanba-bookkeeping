package com.harmony.expense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.harmony.expense.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> listByType(Integer type);
}