package com.harmony.expense.controller;

import com.harmony.expense.common.Result;
import com.harmony.expense.dto.ChatRequestDTO;
import com.harmony.expense.dto.ChatResponseVO;
import com.harmony.expense.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 对话", description = "基于 DeepSeek 的智能财务问答")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "AI 财务问答")
    @PostMapping("/chat")
    public Result<ChatResponseVO> chat(HttpServletRequest request,
                                        @Valid @RequestBody ChatRequestDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        ChatResponseVO result = aiService.chat(userId, dto);
        return Result.success(result);
    }
}