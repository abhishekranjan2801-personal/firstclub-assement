package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;
import com.firstclub.membership.domain.model.CheckoutResult;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.tier.TierEvaluationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutBenefitService {

    private final UserMembershipRepository membershipRepository;
    private final TierEvaluationService tierEvaluationService;
    private final BenefitApplicatorRegistry applicatorRegistry;

    public CheckoutBenefitService(UserMembershipRepository membershipRepository,
                                  TierEvaluationService tierEvaluationService,
                                  BenefitApplicatorRegistry applicatorRegistry) {
        this.membershipRepository = membershipRepository;
        this.tierEvaluationService = tierEvaluationService;
        this.applicatorRegistry = applicatorRegistry;
    }

    public CheckoutResult applyBenefits(CheckoutContext context) {
        UserMembership membership = membershipRepository.findActiveByUserId(context.getUserId())
                .filter(m -> m.isActive(Instant.now()))
                .orElseThrow(() -> new MembershipException("No active membership for checkout"));

        MembershipTier tier = tierEvaluationService.getTierById(membership.getTierId());
        List<AppliedBenefit> applied = new ArrayList<>();

        BigDecimal subtotalDiscount = BigDecimal.ZERO;
        BigDecimal deliverySavings = BigDecimal.ZERO;

        for (Benefit benefit : tier.getBenefits()) {
            BenefitApplicator applicator = applicatorRegistry.getApplicator(benefit.getType());
            AppliedBenefit result = applicator.apply(benefit, context);
            applied.add(result);

            if ("true".equals(result.getAppliedMetadata().get("applied"))) {
                if (benefit.getType() == BenefitType.FREE_DELIVERY) {
                    deliverySavings = deliverySavings.add(result.getSavingsAmount());
                } else if (benefit.getType() == BenefitType.PERCENTAGE_DISCOUNT
                        || benefit.getType() == BenefitType.EXCLUSIVE_COUPONS) {
                    subtotalDiscount = subtotalDiscount.add(result.getSavingsAmount());
                }
            }
        }

        BigDecimal finalSubtotal = context.getCartSubtotal().subtract(subtotalDiscount).max(BigDecimal.ZERO);
        BigDecimal finalDelivery = context.getDeliveryFee().subtract(deliverySavings).max(BigDecimal.ZERO);

        return new CheckoutResult(
                context.getCartSubtotal(),
                subtotalDiscount,
                finalSubtotal,
                context.getDeliveryFee(),
                finalDelivery,
                finalSubtotal.add(finalDelivery),
                applied,
                tier.getName().name()
        );
    }
}
