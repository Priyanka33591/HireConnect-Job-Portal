package com.hireconnect.subscription.repo;

import com.hireconnect.subscription.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdOrderByRenewedAtDesc(Long userId);
    java.util.Optional<Subscription> findByUserIdAndUserRole(Long userId, String userRole);
}

