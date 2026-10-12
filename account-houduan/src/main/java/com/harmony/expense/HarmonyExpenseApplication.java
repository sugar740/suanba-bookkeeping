package com.harmony.expense;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.harmony.expense.mapper")
public class HarmonyExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarmonyExpenseApplication.class, args);
    }
}