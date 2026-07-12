package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PercentageDiscountApplicator implements BenefitApplicator {

    @Override
    public BenefitType supportedType() {
        return BenefitType.PERCENTAGE_DISCOUNT;
    }

    @Override
    public AppliedBenefit apply(Benefit benefit, CheckoutContext context) {
        String categories = benefit.getMetadata().getOrDefault("categories", "");
        int discountPercent = Integer.parseInt(benefit.getMetadata().getOrDefault("discountPercent", "0"));
        Set<String> eligible = Arrays.stream(categories.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        boolean eligibleCart = eligible.contains("all-categories")
                || context.getCartCategories().stream().anyMatch(eligible::contains);

        if (!eligibleCart || discountPercent == 0) {
            return AppliedBenefit.none(benefit.getId(), benefit.getType(),
                    "No eligible categories in cart for discount");
        }

        BigDecimal savings = context.getCartSubtotal()
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new AppliedBenefit(benefit.getId(), benefit.getType(), benefit.getDescription(), savings,
                Map.of("applied", "true", "discountPercent", String.valueOf(discountPercent)));
    }
}
