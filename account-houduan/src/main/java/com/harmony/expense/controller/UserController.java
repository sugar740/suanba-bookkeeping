package com.harmony.expense.controller;

import com.harmony.expense.common.Result;
import com.harmony.expense.dto.LoginDTO;
import com.harmony.expense.dto.LoginResponseDTO;
import com.harmony.expense.dto.RegisterDTO;
import com.harmony.expense.dto.UpdatePasswordDTO;
import com.harmony.expense.entity.User;
import com.harmony.expense.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户注册、登录、信息查询、密码修改")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        LoginResponseDTO result = userService.login(dto.getUsername(), dto.getPassword());
        return Result.success(result);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getUserById(userId));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request,
                                        @Valid @RequestBody UpdatePasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, dto);
        return Result.success();
    }
}