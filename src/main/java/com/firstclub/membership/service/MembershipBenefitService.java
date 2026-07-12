package com.firstclub.membership.service;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.service.benefit.BenefitProcessor;
import com.firstclub.membership.service.tier.TierEvaluationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MembershipBenefitService {

    private final SubscriptionService subscriptionService;
    private final TierEvaluationService tierEvaluationService;
    private final Map<BenefitType, BenefitProcessor> processors;

    public MembershipBenefitService(SubscriptionService subscriptionService,
                                   TierEvaluationService tierEvaluationService,
                                   List<BenefitProcessor> processorList) {
        this.subscriptionService = subscriptionService;
        this.tierEvaluationService = tierEvaluationService;
        this.processors = processorList.stream()
                .collect(Collectors.toMap(BenefitProcessor::getSupportedType, Function.identity()));
    }

    public boolean isFreeDeliveryEligible(String userId, BigDecimal orderValue) {
        return getActiveTier(userId)
                .map(tier -> {
                    Map<String, Object> context = Map.of("orderValue", orderValue);
                    return tier.getBenefits().stream()
                            .filter(b -> b.getType() == BenefitType.FREE_DELIVERY)
                            .anyMatch(b -> Optional.ofNullable(processors.get(BenefitType.FREE_DELIVERY))
                                    .map(p -> p.isEligible(b, context))
                                    .orElse(false));
                })
                .orElse(false);
    }

    public BigDecimal calculateDiscountedPrice(String userId, String category, BigDecimal originalPrice) {
        return getActiveTier(userId)
                .map(tier -> {
                    Map<String, Object> context = Map.of("category", category);
                    BigDecimal finalPrice = originalPrice;
                    for (Benefit benefit : tier.getBenefits()) {
                        BenefitProcessor processor = processors.get(benefit.getType());
                        if (processor != null) {
                            finalPrice = processor.applyPriceBenefit(finalPrice, benefit, context);
                        }
                    }
                    return finalPrice;
                })
                .orElse(originalPrice);
    }

    public boolean hasPrioritySupport(String userId) {
        return getActiveTier(userId)
                .map(tier -> tier.getBenefits().stream()
                        .anyMatch(b -> b.getType() == BenefitType.PRIORITY_SUPPORT))
                .orElse(false);
    }

    private Optional<MembershipTier> getActiveTier(String userId) {
        try {
            UserMembership membership = subscriptionService.getCurrentMembership(userId);
            return Optional.of(tierEvaluationService.getTierById(membership.getTierId()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
