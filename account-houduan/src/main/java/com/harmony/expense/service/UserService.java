package com.harmony.expense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.harmony.expense.dto.LoginResponseDTO;
import com.harmony.expense.dto.RegisterDTO;
import com.harmony.expense.dto.UpdatePasswordDTO;
import com.harmony.expense.entity.User;

public interface UserService extends IService<User> {

    void register(RegisterDTO dto);

    LoginResponseDTO login(String username, String password);

    void updatePassword(Long userId, UpdatePasswordDTO dto);

    User getUserById(Long userId);
}