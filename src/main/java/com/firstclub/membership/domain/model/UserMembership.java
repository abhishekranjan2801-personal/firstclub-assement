package com.firstclub.membership.domain.model;

import com.firstclub.membership.domain.enums.SubscriptionStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class UserMembership {

    private final String id;
    private final String userId;
    private final String planId;
    private final String tierId;
    private final SubscriptionStatus status;
    private final Instant subscribedAt;
    private final Instant expiresAt;
    private final Instant cancelledAt;
    private final boolean autoRenew;

    private UserMembership(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.planId = builder.planId;
        this.tierId = builder.tierId;
        this.status = builder.status;
        this.subscribedAt = builder.subscribedAt;
        this.expiresAt = builder.expiresAt;
        this.cancelledAt = builder.cancelledAt;
        this.autoRenew = builder.autoRenew;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlanId() {
        return planId;
    }

    public String getTierId() {
        return tierId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getSubscribedAt() {
        return subscribedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public boolean isActive(Instant now) {
        return status == SubscriptionStatus.ACTIVE && expiresAt.isAfter(now);
    }

    public UserMembership withTier(String newTierId) {
        return builderFrom(this).tierId(newTierId).build();
    }

    public UserMembership withStatus(SubscriptionStatus newStatus, Instant cancelledAt) {
        return builderFrom(this).status(newStatus).cancelledAt(cancelledAt).build();
    }

    public UserMembership withPlan(String newPlanId, Instant newExpiresAt) {
        return builderFrom(this).planId(newPlanId).expiresAt(newExpiresAt).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static Builder builderFrom(UserMembership membership) {
        return new Builder()
                .id(membership.id)
                .userId(membership.userId)
                .planId(membership.planId)
                .tierId(membership.tierId)
                .status(membership.status)
                .subscribedAt(membership.subscribedAt)
                .expiresAt(membership.expiresAt)
                .cancelledAt(membership.cancelledAt)
                .autoRenew(membership.autoRenew);
    }

    public static final class Builder {
        private String id = UUID.randomUUID().toString();
        private String userId;
        private String planId;
        private String tierId;
        private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        private Instant subscribedAt = Instant.now();
        private Instant expiresAt;
        private Instant cancelledAt;
        private boolean autoRenew = true;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder planId(String planId) {
            this.planId = planId;
            return this;
        }

        public Builder tierId(String tierId) {
            this.tierId = tierId;
            return this;
        }

        public Builder status(SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        public Builder subscribedAt(Instant subscribedAt) {
            this.subscribedAt = subscribedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder cancelledAt(Instant cancelledAt) {
            this.cancelledAt = cancelledAt;
            return this;
        }

        public Builder autoRenew(boolean autoRenew) {
            this.autoRenew = autoRenew;
            return this;
        }

        public UserMembership build() {
            Objects.requireNonNull(userId, "userId");
            Objects.requireNonNull(planId, "planId");
            Objects.requireNonNull(tierId, "tierId");
            Objects.requireNonNull(expiresAt, "expiresAt");
            return new UserMembership(this);
        }
    }
}
