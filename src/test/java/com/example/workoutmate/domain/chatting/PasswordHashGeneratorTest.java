package com.example.workoutmate.domain.chatting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootTest
public class PasswordHashGeneratorTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 채팅 테스트를 위해 유저 100명의 리스트를 생성하는 코드
    @Test
    void generateHashedCsv() throws IOException {
        int userCount = 100;
        String filePath = "users_hashed.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // CSV 헤더
            writer.write("email,password,is_deleted,is_email_verified,created_at,id,modified_at,name,gender,role");
            writer.newLine();

            for (int i = 1; i <= userCount; i++) {
                String email = "user" + i + "@example.com";
                String rawPassword = "Password12!";
                String gender = "Female";
                boolean is_deleted = false;
                boolean is_email_verified = true;
                LocalDateTime created_at = LocalDateTime.now();
                LocalDateTime modified_at = LocalDateTime.now();
                String name = "이현하" + i;
                String role = "GUEST";

                String encodedPassword = passwordEncoder.encode(rawPassword);

                writer.write(email + "," + encodedPassword + "," + is_deleted + "," + is_email_verified + "," +
                        created_at + "," + i + "," + modified_at + "," + name + "," + gender + "," + role);
                writer.newLine();
            }

            System.out.println("success : " + filePath + " 파일 생성 완료");
        }
    }
}
