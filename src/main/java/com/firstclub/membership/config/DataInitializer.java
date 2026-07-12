package com.firstclub.membership.config;

import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.enums.TierName;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.TierCriteria;
import com.firstclub.membership.repository.inmemory.InMemoryMembershipPlanRepository;
import com.firstclub.membership.repository.inmemory.InMemoryMembershipTierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer {

    private final InMemoryMembershipPlanRepository planRepository;
    private final InMemoryMembershipTierRepository tierRepository;

    public DataInitializer(InMemoryMembershipPlanRepository planRepository,
                           InMemoryMembershipTierRepository tierRepository) {
        this.planRepository = planRepository;
        this.tierRepository = tierRepository;
    }

    @PostConstruct
    public void seed() {
        seedPlans();
        seedTiers();
    }

    private void seedPlans() {
        planRepository.register(new MembershipPlan(
                "plan-monthly", "Monthly", PlanDuration.MONTHLY,
                new BigDecimal("299.00"), "Billed every month — flexible commitment"));
        planRepository.register(new MembershipPlan(
                "plan-quarterly", "Quarterly", PlanDuration.QUARTERLY,
                new BigDecimal("799.00"), "Save ~11% with quarterly billing"));
        planRepository.register(new MembershipPlan(
                "plan-yearly", "Yearly", PlanDuration.YEARLY,
                new BigDecimal("2499.00"), "Best value — save ~30% annually"));
    }

    private void seedTiers() {
        tierRepository.register(new MembershipTier(
                "tier-silver", TierName.SILVER, 1,
                "Entry tier — core member benefits",
                TierCriteria.of(0, BigDecimal.ZERO),
                List.of(
                        Benefit.freeDelivery("b-silver-fd", 499),
                        Benefit.percentageDiscount("b-silver-disc", 5, "groceries"),
                        Benefit.exclusiveDeals("b-silver-deals")
                )));

        tierRepository.register(new MembershipTier(
                "tier-gold", TierName.GOLD, 2,
                "Frequent shoppers — enhanced perks",
                TierCriteria.of(5, new BigDecimal("2000.00")),
                List.of(
                        Benefit.freeDelivery("b-gold-fd", 299),
                        Benefit.percentageDiscount("b-gold-disc", 10, "groceries,electronics"),
                        Benefit.exclusiveDeals("b-gold-deals"),
                        Benefit.earlyAccess("b-gold-early", 12),
                        Benefit.fasterDelivery("b-gold-fast", 24)
                )));

        tierRepository.register(new MembershipTier(
                "tier-platinum", TierName.PLATINUM, 3,
                "Premium members — maximum benefits",
                TierCriteria.withCohort(15, new BigDecimal("5000.00"), "VIP"),
                List.of(
                        Benefit.freeDelivery("b-plat-fd", 0),
                        Benefit.percentageDiscount("b-plat-disc", 15, "all-categories"),
                        Benefit.exclusiveDeals("b-plat-deals"),
                        Benefit.earlyAccess("b-plat-early", 24),
                        Benefit.fasterDelivery("b-plat-fast", 12),
                        Benefit.prioritySupport("b-plat-support"),
                        Benefit.exclusiveCoupons("b-plat-coupon", "PLAT20", 20)
                )));
    }
}
