package com.firstclub.membership.service.benefit;

import com.firstclub.membership.domain.enums.BenefitType;
import com.firstclub.membership.domain.model.AppliedBenefit;
import com.firstclub.membership.domain.model.Benefit;
import com.firstclub.membership.domain.model.CheckoutContext;

public interface BenefitApplicator {

    BenefitType supportedType();

    AppliedBenefit apply(Benefit benefit, CheckoutContext context);
}
