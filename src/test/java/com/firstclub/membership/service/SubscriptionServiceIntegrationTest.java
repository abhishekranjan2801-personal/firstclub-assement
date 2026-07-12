package com.firstclub.membership.service;

import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.service.tier.TierEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SubscriptionServiceIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private TierEvaluationService tierEvaluationService;

    @BeforeEach
    void setUp() {
        // Each test uses a unique user id to avoid cross-test state
    }

    @Test
    void subscribeAndAutoUpgradeToGold() {
        String userId = "user-1";
        UserMembership membership = subscriptionService.subscribe(userId, "plan-monthly", "tier-silver");
        assertEquals("tier-silver", membership.getTierId());

        for (int i = 0; i < 5; i++) {
            subscriptionService.recordOrder(userId, new BigDecimal("500.00"));
        }

        UserMembership upgraded = subscriptionService.getCurrentMembership(userId);
        assertEquals("tier-gold", upgraded.getTierId());
    }

    @Test
    void manualDowngradeTier() {
        String userId = "user-4";
        subscriptionService.subscribe(userId, "plan-monthly", "tier-silver");
        for (int i = 0; i < 5; i++) {
            subscriptionService.recordOrder(userId, new BigDecimal("500.00"));
        }
        UserMembership downgraded = subscriptionService.downgradeTier(userId, "tier-silver");
        assertEquals("tier-silver", downgraded.getTierId());
    }

    @Test
    void autoTierProgressionOnOrders() {
        String userId = "user-2";
        subscriptionService.subscribe(userId, "plan-yearly", "tier-silver");

        for (int i = 0; i < 5; i++) {
            subscriptionService.recordOrder(userId, new BigDecimal("500.00"));
        }

        UserMembership membership = subscriptionService.getCurrentMembership(userId);
        assertEquals("tier-gold", membership.getTierId());
    }

    @Test
    void cancelMembership() {
        String userId = "user-3";
        subscriptionService.subscribe(userId, "plan-quarterly", "tier-silver");
        UserMembership cancelled = subscriptionService.cancel(userId);

        assertEquals(com.firstclub.membership.domain.enums.SubscriptionStatus.CANCELLED, cancelled.getStatus());
    }
}
