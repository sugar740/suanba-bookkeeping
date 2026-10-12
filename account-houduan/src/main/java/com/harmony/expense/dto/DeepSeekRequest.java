package com.harmony.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    private ResponseFormat responseFormat;

    public DeepSeekRequest(String model, List<Message> messages, Double temperature,
                           Integer maxTokens, String responseFormatType) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        if (responseFormatType != null) {
            this.responseFormat = new ResponseFormat(responseFormatType);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type;
    }
}
