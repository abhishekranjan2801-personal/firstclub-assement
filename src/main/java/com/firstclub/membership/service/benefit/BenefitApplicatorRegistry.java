package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BenefitApplicatorRegistry {

    private final Map<BenefitType, BenefitApplicator> applicators;

    public BenefitApplicatorRegistry(List<BenefitApplicator> applicatorList) {
        this.applicators = new EnumMap<>(BenefitType.class);
        for (BenefitApplicator applicator : applicatorList) {
            applicators.put(applicator.supportedType(), applicator);
        }
    }

    public BenefitApplicator getApplicator(BenefitType type) {
        BenefitApplicator applicator = applicators.get(type);
        if (applicator == null) {
            throw new IllegalStateException("No applicator registered for benefit type: " + type);
        }
        return applicator;
    }
}
