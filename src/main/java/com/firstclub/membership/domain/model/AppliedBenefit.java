package com.firstclub.membership.domain.model;

import com.firstclub.membership.domain.enums.BenefitType;

import java.math.BigDecimal;
import java.util.Map;

public final class AppliedBenefit {

    private final String benefitId;
    private final BenefitType type;
    private final String description;
    private final BigDecimal savingsAmount;
    private final Map<String, String> appliedMetadata;

    public AppliedBenefit(String benefitId, BenefitType type, String description,
                          BigDecimal savingsAmount, Map<String, String> appliedMetadata) {
        this.benefitId = benefitId;
        this.type = type;
        this.description = description;
        this.savingsAmount = savingsAmount == null ? BigDecimal.ZERO : savingsAmount;
        this.appliedMetadata = appliedMetadata == null ? Map.of() : Map.copyOf(appliedMetadata);
    }

    public String getBenefitId() {
        return benefitId;
    }

    public BenefitType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getSavingsAmount() {
        return savingsAmount;
    }

    public Map<String, String> getAppliedMetadata() {
        return appliedMetadata;
    }

    public static AppliedBenefit none(String benefitId, BenefitType type, String reason) {
        return new AppliedBenefit(benefitId, type, reason, BigDecimal.ZERO, Map.of("applied", "false"));
    }
}
