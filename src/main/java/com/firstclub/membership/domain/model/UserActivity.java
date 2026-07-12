package com.firstclub.membership.domain.model;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class UserActivity {

    private final String userId;
    private final Clock clock;
    private final AtomicInteger totalOrderCount = new AtomicInteger(0);
    private final AtomicReference<BigDecimal> monthlyOrderValue = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<YearMonth> trackedMonth;
    private final Set<String> cohorts = ConcurrentHashMap.newKeySet();

    public UserActivity(String userId) {
        this(userId, Clock.systemUTC());
    }

    public UserActivity(String userId, Clock clock) {
        this.userId = Objects.requireNonNull(userId);
        this.clock = Objects.requireNonNull(clock);
        this.trackedMonth = new AtomicReference<>(YearMonth.now(clock));
    }

    public String getUserId() {
        return userId;
    }

    public int getTotalOrderCount() {
        return totalOrderCount.get();
    }

    public BigDecimal getMonthlyOrderValue() {
        rollOverMonthIfNeeded();
        return monthlyOrderValue.get();
    }

    public Set<String> getCohorts() {
        return Set.copyOf(cohorts);
    }

    public void recordOrder(BigDecimal orderValue) {
        Objects.requireNonNull(orderValue);
        totalOrderCount.incrementAndGet();
        rollOverMonthIfNeeded();
        monthlyOrderValue.updateAndGet(current -> current.add(orderValue));
    }

    public void addCohort(String cohort) {
        cohorts.add(cohort);
    }

    private void rollOverMonthIfNeeded() {
        YearMonth current = YearMonth.now(clock);
        trackedMonth.updateAndGet(stored -> {
            if (!stored.equals(current)) {
                monthlyOrderValue.set(BigDecimal.ZERO);
                return current;
            }
            return stored;
        });
    }
}