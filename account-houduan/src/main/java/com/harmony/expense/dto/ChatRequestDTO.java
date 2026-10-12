package com.harmony.expense.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {

    @NotBlank(message = "问题不能为空")
    private String question;

    private String context;
}