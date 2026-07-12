package com.firstclub.membership.domain.enums;

import java.time.temporal.ChronoUnit;

public enum PlanDuration {
    MONTHLY(1, ChronoUnit.MONTHS),
    QUARTERLY(3, ChronoUnit.MONTHS),
    YEARLY(12, ChronoUnit.MONTHS);

    private final int units;
    private final ChronoUnit chronoUnit;

    PlanDuration(int units, ChronoUnit chronoUnit) {
        this.units = units;
        this.chronoUnit = chronoUnit;
    }

    public int getUnits() {
        return units;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }
}
