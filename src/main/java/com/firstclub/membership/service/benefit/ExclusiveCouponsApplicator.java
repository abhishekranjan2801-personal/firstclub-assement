package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ExclusiveCouponsApplicator implements BenefitApplicator {

    @Override
    public BenefitType supportedType() {
        return BenefitType.EXCLUSIVE_COUPONS;
    }

    @Override
    public AppliedBenefit apply(Benefit benefit, CheckoutContext context) {
        String couponCode = benefit.getMetadata().getOrDefault("couponCode", "");
        int discountPercent = Integer.parseInt(benefit.getMetadata().getOrDefault("discountPercent", "0"));

        BigDecimal savings = context.getCartSubtotal()
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new AppliedBenefit(benefit.getId(), benefit.getType(), benefit.getDescription(), savings,
                Map.of("applied", "true", "couponCode", couponCode, "discountPercent", String.valueOf(discountPercent)));
    }
}
