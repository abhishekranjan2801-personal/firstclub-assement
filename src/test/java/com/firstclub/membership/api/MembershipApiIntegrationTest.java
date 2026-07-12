package com.firstclub.membership.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.api.dto.AssignCohortRequest;
import com.firstclub.membership.api.dto.CheckoutRequest;
import com.firstclub.membership.api.dto.RecordOrderRequest;
import com.firstclub.membership.api.dto.SubscribeRequest;
import com.firstclub.membership.api.dto.TierChangeRequest;
import com.firstclub.membership.api.dto.UserActionRequest;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MembershipApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetPlans() throws Exception {
        mockMvc.perform(get("/api/membership/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("plan-monthly"));
    }

    @Test
    void testGetTiers() throws Exception {
        mockMvc.perform(get("/api/membership/tiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("tier-silver"));
    }

    @Test
    void testCompleteUserJourney() throws Exception {
        String userId = "test-user-api";

        // 1. Subscribe to Silver
        SubscribeRequest subscribeRequest = new SubscribeRequest(userId, "plan-monthly", "tier-silver");
        mockMvc.perform(post("/api/membership/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscribeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tier.name").value("SILVER"));

        // 2. Check current membership
        mockMvc.perform(get("/api/membership/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("SILVER"));

        // 3. Record orders to qualify for Gold (Requires 5 orders, 2000 value)
        for (int i = 0; i < 5; i++) {
            RecordOrderRequest orderRequest = new RecordOrderRequest(userId, new BigDecimal("500.00"));
            mockMvc.perform(post("/api/membership/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isNoContent());
        }

        // 4. Verify auto-upgrade to Gold
        mockMvc.perform(get("/api/membership/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("GOLD"));

        // 5. Assign cohort to qualify for Platinum (Requires "VIP" cohort + 15 orders + 5000 value)
        // Add more orders first
        for (int i = 0; i < 10; i++) {
            RecordOrderRequest orderRequest = new RecordOrderRequest(userId, new BigDecimal("300.00"));
            mockMvc.perform(post("/api/membership/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isNoContent());
        }
        
        AssignCohortRequest cohortRequest = new AssignCohortRequest(userId, "VIP");
        mockMvc.perform(post("/api/membership/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cohortRequest)))
                .andExpect(status().isNoContent());

        // 6. Verify auto-upgrade to Platinum
        mockMvc.perform(get("/api/membership/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("PLATINUM"));

        // 7. Check Benefits
        // Free delivery: Silver (min 499), Gold (min 299), Platinum (min 0)
        mockMvc.perform(get("/api/membership/benefits/free-delivery")
                        .param("userId", userId)
                        .param("orderValue", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true)); // Platinum has min 0

        // Discount: Platinum has 15% on "all-categories"
        mockMvc.perform(get("/api/membership/benefits/discount")
                        .param("userId", userId)
                        .param("category", "any")
                        .param("price", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(85.00));

        // 8. Test Checkout API
        CheckoutRequest checkoutRequest = new CheckoutRequest(userId, new BigDecimal("1000.00"), new BigDecimal("50.00"), List.of("electronics"));
        mockMvc.perform(post("/api/membership/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalDeliveryFee").value(0)) // Platinum has free delivery
                .andExpect(jsonPath("$.discountAmount").value(350.00)) // 15% discount (150) + 20% coupon (200) = 350
                .andExpect(jsonPath("$.appliedBenefits").isArray())
                .andExpect(jsonPath("$.appliedBenefits[?(@.type=='EXCLUSIVE_COUPONS')].appliedMetadata.couponCode").value("PLAT20"));

        // 9. Manual Downgrade to Gold
        TierChangeRequest downgradeRequest = new TierChangeRequest(userId, "tier-gold");
        mockMvc.perform(post("/api/membership/downgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(downgradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("GOLD"));

        // 9. Manual Upgrade to Platinum (should work because user is eligible)
        TierChangeRequest upgradeRequest = new TierChangeRequest(userId, "tier-platinum");
        mockMvc.perform(post("/api/membership/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upgradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("PLATINUM"));

        // 10. Cancel Membership
        UserActionRequest cancelRequest = new UserActionRequest(userId);
        mockMvc.perform(post("/api/membership/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SubscriptionStatus.CANCELLED.name()));
    }
}
