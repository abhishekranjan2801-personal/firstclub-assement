package com.firstclub.membership.domain.model;

import com.firstclub.membership.domain.enums.PlanDuration;

import java.math.BigDecimal;
import java.util.Objects;

public final class MembershipPlan {

    private final String id;
    private final String name;
    private final PlanDuration duration;
    private final BigDecimal price;
    private final String description;

    public MembershipPlan(String id, String name, PlanDuration duration, BigDecimal price, String description) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.duration = Objects.requireNonNull(duration);
        this.price = Objects.requireNonNull(price);
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlanDuration getDuration() {
        return duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
