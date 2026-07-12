package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.Benefit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ShippingBenefitProcessor implements BenefitProcessor {
    @Override
    public BenefitType getSupportedType() {
        return BenefitType.FREE_DELIVERY;
    }

    @Override
    public boolean isEligible(Benefit benefit, Map<String, Object> context) {
        BigDecimal orderValue = (BigDecimal) context.get("orderValue");
        if (orderValue == null) return false;
        
        String minValStr = benefit.getMetadata().get("minOrderValue");
        if (minValStr == null) return true;
        
        BigDecimal minVal = new BigDecimal(minValStr);
        return orderValue.compareTo(minVal) >= 0;
    }
}
