package com.firstclub.membership.domain.model;

import com.firstclub.membership.domain.enums.TierName;

import java.util.List;
import java.util.Objects;

public final class MembershipTier {

    private final String id;
    private final TierName name;
    private final int rank;
    private final String description;
    private final TierCriteria criteria;
    private final List<Benefit> benefits;

    public MembershipTier(String id, TierName name, int rank, String description,
                          TierCriteria criteria, List<Benefit> benefits) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.rank = rank;
        this.description = description;
        this.criteria = Objects.requireNonNull(criteria);
        this.benefits = benefits == null ? List.of() : List.copyOf(benefits);
    }

    public String getId() {
        return id;
    }

    public TierName getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public String getDescription() {
        return description;
    }

    public TierCriteria getCriteria() {
        return criteria;
    }

    public List<Benefit> getBenefits() {
        return benefits;
    }
}
