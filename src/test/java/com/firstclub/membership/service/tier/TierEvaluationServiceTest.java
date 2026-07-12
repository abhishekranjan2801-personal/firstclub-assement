package com.firstclub.membership.service.tier;

import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TierEvaluationServiceTest {

    @Autowired
    private TierEvaluationService tierEvaluationService;

    @Test
    void evaluatesSilverForNewUser() {
        UserActivity activity = new UserActivity("new-user");
        MembershipTier tier = tierEvaluationService.evaluateEligibleTier(activity);
        assertEquals("tier-silver", tier.getId());
    }

    @Test
    void evaluatesGoldAfterOrderThresholds() {
        UserActivity activity = new UserActivity("gold-user");
        for (int i = 0; i < 5; i++) {
            activity.recordOrder(new BigDecimal("500.00"));
        }
        MembershipTier tier = tierEvaluationService.evaluateEligibleTier(activity);
        assertEquals("tier-gold", tier.getId());
    }

    @Test
    void evaluatesPlatinumWithCohort() {
        UserActivity activity = new UserActivity("plat-user");
        activity.addCohort("VIP");
        for (int i = 0; i < 15; i++) {
            activity.recordOrder(new BigDecimal("400.00"));
        }
        MembershipTier tier = tierEvaluationService.evaluateEligibleTier(activity);
        assertEquals("tier-platinum", tier.getId());
    }
}
