package com.harmony.expense.service;

import com.harmony.expense.dto.ChatRequestDTO;
import com.harmony.expense.dto.ChatResponseVO;

public interface AiService {

    ChatResponseVO chat(Long userId, ChatRequestDTO dto);
}