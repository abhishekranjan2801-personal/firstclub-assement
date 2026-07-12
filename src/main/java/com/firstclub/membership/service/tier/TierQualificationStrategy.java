package com.firstclub.membership.service.tier;

import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.TierCriteria;
import com.firstclub.membership.domain.model.UserActivity;

public interface TierQualificationStrategy {

    boolean qualifies(UserActivity activity, TierCriteria criteria);
}
