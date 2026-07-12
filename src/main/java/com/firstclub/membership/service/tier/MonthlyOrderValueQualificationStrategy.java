package com.firstclub.membership.service.tier;

import com.firstclub.membership.domain.model.TierCriteria;
import com.firstclub.membership.domain.model.UserActivity;
import org.springframework.stereotype.Component;

@Component
public class MonthlyOrderValueQualificationStrategy implements TierQualificationStrategy {

    @Override
    public boolean qualifies(UserActivity activity, TierCriteria criteria) {
        return activity.getMonthlyOrderValue().compareTo(criteria.getMinMonthlyOrderValue()) >= 0;
    }
}
