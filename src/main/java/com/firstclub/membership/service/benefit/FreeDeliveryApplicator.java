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
public class FreeDeliveryApplicator implements BenefitApplicator {

    @Override
    public BenefitType supportedType() {
        return BenefitType.FREE_DELIVERY;
    }

    @Override
    public AppliedBenefit apply(Benefit benefit, CheckoutContext context) {
        int minOrderValue = Integer.parseInt(benefit.getMetadata().getOrDefault("minOrderValue", "0"));
        if (context.getCartSubtotal().compareTo(BigDecimal.valueOf(minOrderValue)) >= 0) {
            return new AppliedBenefit(benefit.getId(), benefit.getType(), benefit.getDescription(),
                    context.getDeliveryFee(),
                    Map.of("applied", "true", "waivedDeliveryFee", context.getDeliveryFee().toPlainString()));
        }
        return AppliedBenefit.none(benefit.getId(), benefit.getType(),
                "Cart below minimum ₹" + minOrderValue + " for free delivery");
    }
}
