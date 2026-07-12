package com.firstclub.membership.service;

import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipCatalogService {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;

    public MembershipCatalogService(MembershipPlanRepository planRepository,
                                    MembershipTierRepository tierRepository) {
        this.planRepository = planRepository;
        this.tierRepository = tierRepository;
    }

    public List<MembershipPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<MembershipTier> getAllTiers() {
        return tierRepository.findAll();
    }

    public MembershipPlan getPlanById(String planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new com.firstclub.membership.exception.ResourceNotFoundException(
                        "Plan not found: " + planId));
    }
}
