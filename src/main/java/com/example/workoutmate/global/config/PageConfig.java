package com.example.workoutmate.global.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

// 페이징 응답(Page 객체)을 JSON으로 직렬화할 때,
// 안정적인 구조(VIA_DTO)로 반환되도록 설정하는 Config 클래스
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class PageConfig {
}
