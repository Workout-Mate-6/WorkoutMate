package com.example.workoutmate.global.config;

import com.example.workoutmate.global.enums.CustomErrorCode;
import com.example.workoutmate.global.exception.CustomException;
import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    @Bean
    public SendGrid sendGrid() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new CustomException(CustomErrorCode.SENDGRID_API_KEY_MISSING);
        }
        if (!apiKey.startsWith("SG.")) {
            throw  new CustomException(CustomErrorCode.SENDGRID_API_KEY_INVALID);
        }
        return new SendGrid(apiKey);
    }
}
