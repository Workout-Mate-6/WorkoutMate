package com.example.workoutmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WorkoutMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkoutMateApplication.class, args);
    }

}
