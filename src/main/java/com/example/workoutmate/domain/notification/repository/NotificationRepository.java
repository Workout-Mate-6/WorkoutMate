package com.example.workoutmate.domain.notification.repository;

import com.example.workoutmate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
