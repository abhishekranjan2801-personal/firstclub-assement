package com.firstclub.membership.domain.model;

import java.math.BigDecimal;
import java.util.List;

public final class CheckoutResult {

    private final BigDecimal originalSubtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal finalSubtotal;
    private final BigDecimal originalDeliveryFee;
    private final BigDecimal finalDeliveryFee;
    private final BigDecimal totalPayable;
    private final List<AppliedBenefit> appliedBenefits;
    private final String tierName;

    public CheckoutResult(BigDecimal originalSubtotal, BigDecimal discountAmount, BigDecimal finalSubtotal,
                          BigDecimal originalDeliveryFee, BigDecimal finalDeliveryFee, BigDecimal totalPayable,
                          List<AppliedBenefit> appliedBenefits, String tierName) {
        this.originalSubtotal = originalSubtotal;
        this.discountAmount = discountAmount;
        this.finalSubtotal = finalSubtotal;
        this.originalDeliveryFee = originalDeliveryFee;
        this.finalDeliveryFee = finalDeliveryFee;
        this.totalPayable = totalPayable;
        this.appliedBenefits = appliedBenefits == null ? List.of() : List.copyOf(appliedBenefits);
        this.tierName = tierName;
    }

    public BigDecimal getOriginalSubtotal() {
        return originalSubtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getFinalSubtotal() {
        return finalSubtotal;
    }

    public BigDecimal getOriginalDeliveryFee() {
        return originalDeliveryFee;
    }

    public BigDecimal getFinalDeliveryFee() {
        return finalDeliveryFee;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public List<AppliedBenefit> getAppliedBenefits() {
        return appliedBenefits;
    }

    public String getTierName() {
        return tierName;
    }
}
