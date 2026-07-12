package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.Benefit;

import java.math.BigDecimal;
import java.util.Map;

public interface BenefitProcessor {
    BenefitType getSupportedType();
    
    default BigDecimal applyPriceBenefit(BigDecimal originalPrice, Benefit benefit, Map<String, Object> context) {
        return originalPrice;
    }
    
    default boolean isEligible(Benefit benefit, Map<String, Object> context) {
        return true;
    }
}
