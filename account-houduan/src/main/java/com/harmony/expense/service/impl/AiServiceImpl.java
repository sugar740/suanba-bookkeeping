package com.harmony.expense.service.impl;

import com.harmony.expense.config.AiProperties;
import com.harmony.expense.dto.*;
import com.harmony.expense.service.AiService;
import com.harmony.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ExpenseService expenseService;

    @Override
    public ChatResponseVO chat(Long userId, ChatRequestDTO dto) {
        String context = buildChatContext(userId);
        String userPrompt = context + "\n\n用户问题：" + dto.getQuestion();
        String responseText = callDeepSeek(userPrompt);
        return new ChatResponseVO(responseText);
    }

    private String callDeepSeek(String userPrompt) {
        String url = aiProperties.getBaseUrl() + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        List<DeepSeekRequest.Message> messages = new ArrayList<>();
        messages.add(new DeepSeekRequest.Message("system",
                "你是一个专业的个人财务助手。请根据用户提供的财务数据，用友好、口语化的中文回答用户的问题。回答要简洁、精练。"));
        messages.add(new DeepSeekRequest.Message("user", userPrompt));

        DeepSeekRequest request = new DeepSeekRequest(
                aiProperties.getModel(),
                messages,
                aiProperties.getTemperature(),
                aiProperties.getMaxTokens(),
                null
        );

        HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<DeepSeekResponse> response = restTemplate.postForEntity(
                    url, entity, DeepSeekResponse.class);

            DeepSeekResponse body = response.getBody();
            if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
                log.warn("DeepSeek returned empty response");
                return "抱歉，AI 暂时无法回答，请稍后再试。";
            }

            return body.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("DeepSeek API call failed", e);
            return "AI 服务暂时不可用，请稍后再试。";
        }
    }

    private String buildChatContext(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("以下是用户的财务数据上下文，请根据这些数据回答用户的问题：\n\n");

        int year = YearMonth.now().getYear();
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        List<CategoryStatisticsDTO> expenseCats = expenseService.categoryStatistics(userId, 0, yearStart, yearEnd);
        List<CategoryStatisticsDTO> incomeCats = expenseService.categoryStatistics(userId, 1, yearStart, yearEnd);
        List<MonthlyStatisticsDTO> monthlyStats = expenseService.monthlyStatistics(userId, yearStart, yearEnd);

        double totalExpense = expenseCats.stream().mapToDouble(c -> c.getTotalAmount().doubleValue()).sum();
        double totalIncome = incomeCats.stream().mapToDouble(c -> c.getTotalAmount().doubleValue()).sum();

        sb.append("今年至今（").append(year).append("年）总支出: ¥")
                .append(String.format("%.2f", totalExpense)).append("\n");
        sb.append("今年至今总收入: ¥").append(String.format("%.2f", totalIncome)).append("\n\n");

        if (!expenseCats.isEmpty()) {
            sb.append("支出分类排行（从高到低）：\n");
            expenseCats.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
            for (CategoryStatisticsDTO c : expenseCats) {
                sb.append("- ").append(c.getCategoryName())
                        .append(": ¥").append(String.format("%.2f", c.getTotalAmount())).append("\n");
            }
            sb.append("\n");
        }

        if (!incomeCats.isEmpty()) {
            sb.append("收入分类：\n");
            for (CategoryStatisticsDTO c : incomeCats) {
                sb.append("- ").append(c.getCategoryName())
                        .append(": ¥").append(String.format("%.2f", c.getTotalAmount())).append("\n");
            }
            sb.append("\n");
        }

        if (!monthlyStats.isEmpty()) {
            sb.append("各月收支情况：\n");
            for (MonthlyStatisticsDTO m : monthlyStats) {
                sb.append("- ").append(m.getPeriod())
                        .append(": 收入 ¥").append(String.format("%.2f", m.getIncome()))
                        .append(", 支出 ¥").append(String.format("%.2f", m.getExpense())).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}