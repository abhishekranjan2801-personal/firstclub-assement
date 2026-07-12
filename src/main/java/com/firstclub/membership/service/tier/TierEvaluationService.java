package com.firstclub.membership.service.tier;

import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserActivity;
import com.firstclub.membership.repository.MembershipTierRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TierEvaluationService {

    private final MembershipTierRepository tierRepository;
    private final List<TierQualificationStrategy> strategies;

    public TierEvaluationService(MembershipTierRepository tierRepository,
                                 List<TierQualificationStrategy> strategies) {
        this.tierRepository = tierRepository;
        this.strategies = strategies;
    }

    public MembershipTier evaluateEligibleTier(UserActivity activity) {
        return tierRepository.findAll().stream()
                .filter(tier -> qualifiesForTier(activity, tier))
                .max(Comparator.comparingInt(MembershipTier::getRank))
                .orElseGet(() -> tierRepository.findByRank(1)
                        .orElseThrow(() -> new IllegalStateException("No base tier configured")));
    }

    public boolean qualifiesForTier(UserActivity activity, MembershipTier tier) {
        return strategies.stream()
                .allMatch(strategy -> strategy.qualifies(activity, tier.getCriteria()));
    }

    public MembershipTier getTierById(String tierId) {
        return tierRepository.findById(tierId)
                .orElseThrow(() -> new com.firstclub.membership.exception.ResourceNotFoundException(
                        "Tier not found: " + tierId));
    }

    public List<MembershipTier> getAllTiers() {
        return tierRepository.findAll();
    }
}
