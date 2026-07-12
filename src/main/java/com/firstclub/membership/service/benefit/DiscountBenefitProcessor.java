package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.Benefit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class DiscountBenefitProcessor implements BenefitProcessor {
    @Override
    public BenefitType getSupportedType() {
        return BenefitType.PERCENTAGE_DISCOUNT;
    }

    @Override
    public BigDecimal applyPriceBenefit(BigDecimal originalPrice, Benefit benefit, Map<String, Object> context) {
        String category = (String) context.get("category");
        if (isCategoryEligible(benefit, category)) {
            String discountStr = benefit.getMetadata().get("discountPercent");
            if (discountStr != null) {
                BigDecimal discountPercent = new BigDecimal(discountStr);
                BigDecimal discountAmount = originalPrice.multiply(discountPercent)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                return originalPrice.subtract(discountAmount);
            }
        }
        return originalPrice;
    }

    private boolean isCategoryEligible(Benefit benefit, String category) {
        String eligibleCategories = benefit.getMetadata().get("categories");
        if (eligibleCategories == null || eligibleCategories.equals("all-categories")) {
            return true;
        }
        if (category == null) return false;
        String[] categories = eligibleCategories.split(",");
        for (String c : categories) {
            if (c.trim().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }
}
