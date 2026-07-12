package com.firstclub.membership.api.dto;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.domain.enums.TierName;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.TierCriteria;
import com.firstclub.membership.domain.model.UserMembership;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static PlanResponse toPlanResponse(MembershipPlan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDuration(),
                plan.getPrice(),
                plan.getDescription()
        );
    }

    public static TierResponse toTierResponse(MembershipTier tier) {
        return new TierResponse(
                tier.getId(),
                tier.getName(),
                tier.getRank(),
                tier.getDescription(),
                toCriteriaResponse(tier.getCriteria()),
                tier.getBenefits().stream().map(DtoMapper::toBenefitResponse).toList()
        );
    }

    public static MembershipResponse toMembershipResponse(UserMembership membership,
                                                          MembershipPlan plan,
                                                          MembershipTier tier) {
        return new MembershipResponse(
                membership.getId(),
                membership.getUserId(),
                toPlanResponse(plan),
                toTierResponse(tier),
                membership.getStatus(),
                membership.getSubscribedAt(),
                membership.getExpiresAt(),
                membership.getCancelledAt(),
                membership.isAutoRenew()
        );
    }

    private static CriteriaResponse toCriteriaResponse(TierCriteria criteria) {
        return new CriteriaResponse(
                criteria.getMinOrderCount(),
                criteria.getMinMonthlyOrderValue(),
                criteria.getRequiredCohorts()
        );
    }

    private static BenefitResponse toBenefitResponse(Benefit benefit) {
        return new BenefitResponse(
                benefit.getId(),
                benefit.getType(),
                benefit.getDescription(),
                benefit.getMetadata()
        );
    }

    public record PlanResponse(
            String id,
            String name,
            PlanDuration duration,
            BigDecimal price,
            String description
    ) {
    }

    public record BenefitResponse(
            String id,
            BenefitType type,
            String description,
            Map<String, String> metadata
    ) {
    }

    public record CriteriaResponse(
            int minOrderCount,
            BigDecimal minMonthlyOrderValue,
            Set<String> requiredCohorts
    ) {
    }

    public record TierResponse(
            String id,
            TierName name,
            int rank,
            String description,
            CriteriaResponse criteria,
            List<BenefitResponse> benefits
    ) {
    }

    public record MembershipResponse(
            String id,
            String userId,
            PlanResponse plan,
            TierResponse tier,
            SubscriptionStatus status,
            Instant subscribedAt,
            Instant expiresAt,
            Instant cancelledAt,
            boolean autoRenew
    ) {
    }
}
