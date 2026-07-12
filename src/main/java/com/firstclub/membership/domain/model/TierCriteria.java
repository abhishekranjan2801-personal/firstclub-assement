package com.firstclub.membership.domain.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class TierCriteria {

    public static final String MIN_ORDER_COUNT = "minOrderCount";
    public static final String MIN_MONTHLY_ORDER_VALUE = "minMonthlyOrderValue";
    public static final String REQUIRED_COHORTS = "requiredCohorts";

    private final Map<String, Object> requirements;

    public TierCriteria(Map<String, Object> requirements) {
        this.requirements = requirements == null ? Map.of() : Map.copyOf(requirements);
    }

    public int getMinOrderCount() {
        return (Integer) requirements.getOrDefault(MIN_ORDER_COUNT, 0);
    }

    public BigDecimal getMinMonthlyOrderValue() {
        return (BigDecimal) requirements.getOrDefault(MIN_MONTHLY_ORDER_VALUE, BigDecimal.ZERO);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRequiredCohorts() {
        return (Set<String>) requirements.getOrDefault(REQUIRED_COHORTS, Set.of());
    }

    public Optional<String> getAnyRequiredCohort() {
        return getRequiredCohorts().stream().findFirst();
    }

    public Map<String, Object> getRequirements() {
        return requirements;
    }

    public static TierCriteria of(int minOrderCount, BigDecimal minMonthlyOrderValue) {
        Map<String, Object> reqs = new HashMap<>();
        reqs.put(MIN_ORDER_COUNT, minOrderCount);
        reqs.put(MIN_MONTHLY_ORDER_VALUE, minMonthlyOrderValue);
        return new TierCriteria(reqs);
    }

    public static TierCriteria withCohort(int minOrderCount, BigDecimal minMonthlyOrderValue, String cohort) {
        Map<String, Object> reqs = new HashMap<>();
        reqs.put(MIN_ORDER_COUNT, minOrderCount);
        reqs.put(MIN_MONTHLY_ORDER_VALUE, minMonthlyOrderValue);
        reqs.put(REQUIRED_COHORTS, Set.of(cohort));
        return new TierCriteria(reqs);
    }
}
