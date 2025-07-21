package com.example.workoutmate.global.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomMapper {

    private CustomMapper() {}

    public static <T> Map<String, Object> responseToMap(T body, boolean flag) {
        Map<String,Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("message", flag ? "성공" : "실패");
        map.put("data",body);
        map.put("timestamp", LocalDateTime.now());
        return map;
    }

    public static <T, U> T toDto(U entity, Class<T> dtoClass) {
        try{
            return dtoClass.getConstructor(entity.getClass()).newInstance(entity);
        } catch (Exception e){
            throw new RuntimeException("Entity -> Dto 변환 실패");
        }
    }
}
