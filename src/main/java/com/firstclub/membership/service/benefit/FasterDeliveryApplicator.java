package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FasterDeliveryApplicator implements BenefitApplicator {

    @Override
    public BenefitType supportedType() {
        return BenefitType.FASTER_DELIVERY;
    }

    @Override
    public AppliedBenefit apply(Benefit benefit, CheckoutContext context) {
        String hours = benefit.getMetadata().getOrDefault("deliveryHours", "48");
        return new AppliedBenefit(benefit.getId(), benefit.getType(), benefit.getDescription(),
                java.math.BigDecimal.ZERO,
                Map.of("applied", "true", "deliveryHours", hours, "deliveryType", "express"));
    }
}
