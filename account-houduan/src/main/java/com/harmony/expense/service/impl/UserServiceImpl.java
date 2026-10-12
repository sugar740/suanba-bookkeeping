package com.harmony.expense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harmony.expense.dto.LoginResponseDTO;
import com.harmony.expense.dto.RegisterDTO;
import com.harmony.expense.dto.UpdatePasswordDTO;
import com.harmony.expense.entity.User;
import com.harmony.expense.mapper.UserMapper;
import com.harmony.expense.service.UserService;
import com.harmony.expense.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO dto) {
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encryptPassword(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        save(user);
    }

    @Override
    public LoginResponseDTO login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (!user.getPassword().equals(encryptPassword(password))) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponseDTO(token, user.getNickname(), user.getAvatar());
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!user.getPassword().equals(encryptPassword(dto.getOldPassword()))) {
            throw new IllegalArgumentException("旧密码错误");
        }
        user.setPassword(encryptPassword(dto.getNewPassword()));
        updateById(user);
    }

    @Override
    public User getUserById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + "harmony_salt").getBytes());
    }
}