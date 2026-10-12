package com.harmony.expense.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String baseUrl = "https://api.deepseek.com";

    private String model = "deepseek-chat";

    private String apiKey;

    private int maxTokens = 2048;

    private double temperature = 0.7;
}
