package com.firstclub.membership.service;

import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.domain.model.UserMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MembershipBenefitServiceTest {

    @Autowired
    private MembershipBenefitService benefitService;

    @Autowired
    private SubscriptionService subscriptionService;

    private final String userId = "test-user-benefits";

    @BeforeEach
    void setUp() {
        try {
            subscriptionService.cancel(userId);
        } catch (Exception ignored) {}
    }

    @Test
    void testSilverBenefits() {
        subscriptionService.subscribe(userId, "plan-monthly", "tier-silver");

        // Silver: 5% discount on groceries, Free delivery on orders >= 499
        assertTrue(benefitService.isFreeDeliveryEligible(userId, new BigDecimal("500")));
        assertFalse(benefitService.isFreeDeliveryEligible(userId, new BigDecimal("300")));

        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal expectedPrice = new BigDecimal("95.00");
        assertEquals(expectedPrice, benefitService.calculateDiscountedPrice(userId, "groceries", originalPrice).setScale(2, RoundingMode.HALF_UP));
        
        // No discount on electronics for silver
        assertEquals(originalPrice, benefitService.calculateDiscountedPrice(userId, "electronics", originalPrice));
    }

    @Test
    void testPlatinumBenefits() {
        // To qualify for Platinum, need VIP cohort + 15 orders + 5000 value
        subscriptionService.assignCohort(userId, "VIP");
        for (int i = 0; i < 15; i++) {
            subscriptionService.recordOrder(userId, new BigDecimal("400"));
        }
        
        subscriptionService.subscribe(userId, "plan-yearly", "tier-platinum");

        // Platinum: 15% discount on all-categories, Free delivery on all orders (min 0)
        assertTrue(benefitService.isFreeDeliveryEligible(userId, new BigDecimal("10")));
        assertTrue(benefitService.isFreeDeliveryEligible(userId, new BigDecimal("1000")));

        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal expectedPrice = new BigDecimal("85.00");
        assertEquals(expectedPrice, benefitService.calculateDiscountedPrice(userId, "electronics", originalPrice).setScale(2, RoundingMode.HALF_UP));
        assertEquals(expectedPrice, benefitService.calculateDiscountedPrice(userId, "any", originalPrice).setScale(2, RoundingMode.HALF_UP));

        assertTrue(benefitService.hasPrioritySupport(userId));
    }
}
