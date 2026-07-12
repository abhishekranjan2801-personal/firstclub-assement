package com.firstclub.membership.service.tier;

import com.firstclub.membership.domain.model.TierCriteria;
import com.firstclub.membership.domain.model.UserActivity;
import org.springframework.stereotype.Component;

@Component
public class CohortQualificationStrategy implements TierQualificationStrategy {

    @Override
    public boolean qualifies(UserActivity activity, TierCriteria criteria) {
        if (criteria.getRequiredCohorts().isEmpty()) {
            return true;
        }
        return criteria.getRequiredCohorts().stream()
                .anyMatch(cohort -> activity.getCohorts().contains(cohort));
    }
}
