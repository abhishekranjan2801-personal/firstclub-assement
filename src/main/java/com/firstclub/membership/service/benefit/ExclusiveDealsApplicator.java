package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExclusiveDealsApplicator implements BenefitApplicator {

    @Override
    public BenefitType supportedType() {
        return BenefitType.EXCLUSIVE_DEALS;
    }

    @Override
    public AppliedBenefit apply(Benefit benefit, CheckoutContext context) {
        return new AppliedBenefit(benefit.getId(), benefit.getType(), benefit.getDescription(),
                java.math.BigDecimal.ZERO,
                Map.of("applied", "true", "access", "exclusive-deals-unlocked"));
    }
}
