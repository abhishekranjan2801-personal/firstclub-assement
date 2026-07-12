package com.firstclub.membership.service;

import com.firstclub.membership.domain.enums.PlanDuration;
import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserActivity;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserActivityRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.tier.TierEvaluationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SubscriptionService {

    private static final int STRIPE_COUNT = 1024;
    private final UserMembershipRepository membershipRepository;
    private final UserActivityRepository activityRepository;
    private final MembershipCatalogService catalogService;
    private final TierEvaluationService tierEvaluationService;
    private final ReentrantLock[] locks;

    public SubscriptionService(UserMembershipRepository membershipRepository,
                               UserActivityRepository activityRepository,
                               MembershipCatalogService catalogService,
                               TierEvaluationService tierEvaluationService) {
        this.membershipRepository = membershipRepository;
        this.activityRepository = activityRepository;
        this.catalogService = catalogService;
        this.tierEvaluationService = tierEvaluationService;
        this.locks = new ReentrantLock[STRIPE_COUNT];
        for (int i = 0; i < STRIPE_COUNT; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public UserMembership subscribe(String userId, String planId, String tierId) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            membershipRepository.findActiveByUserId(userId).ifPresent(existing -> {
                throw new MembershipException("User already has an active membership");
            });

            MembershipPlan plan = catalogService.getPlanById(planId);
            MembershipTier tier = tierEvaluationService.getTierById(tierId);
            validateTierEligibility(userId, tier);

            Instant now = Instant.now();
            UserMembership membership = UserMembership.builder()
                    .userId(userId)
                    .planId(plan.getId())
                    .tierId(tier.getId())
                    .status(SubscriptionStatus.ACTIVE)
                    .subscribedAt(now)
                    .expiresAt(calculateExpiry(now, plan.getDuration()))
                    .build();

            return membershipRepository.save(membership);
        } finally {
            lock.unlock();
        }
    }

    public UserMembership upgradeTier(String userId, String targetTierId) {
        return changeTier(userId, targetTierId, true);
    }

    public UserMembership downgradeTier(String userId, String targetTierId) {
        return changeTier(userId, targetTierId, false);
    }

    public UserMembership cancel(String userId) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            UserMembership active = getActiveMembershipOrThrow(userId);
            UserMembership cancelled = active.withStatus(SubscriptionStatus.CANCELLED, Instant.now());
            return membershipRepository.save(cancelled);
        } finally {
            lock.unlock();
        }
    }

    public UserMembership getCurrentMembership(String userId) {
        return membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership for user: " + userId));
    }

    public UserMembership evaluateAndApplyTierProgression(String userId) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            UserMembership active = getActiveMembershipOrThrow(userId);
            UserActivity activity = activityRepository.getOrCreate(userId);
            MembershipTier currentTier = tierEvaluationService.getTierById(active.getTierId());
            MembershipTier eligibleTier = tierEvaluationService.evaluateEligibleTier(activity);

            if (eligibleTier.getRank() > currentTier.getRank()) {
                return membershipRepository.save(active.withTier(eligibleTier.getId()));
            }
            return active;
        } finally {
            lock.unlock();
        }
    }

    public void recordOrder(String userId, BigDecimal orderValue) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            UserActivity activity = activityRepository.getOrCreate(userId);
            activity.recordOrder(orderValue);
            membershipRepository.findActiveByUserId(userId)
                    .ifPresent(m -> evaluateAndApplyTierProgression(userId));
        } finally {
            lock.unlock();
        }
    }

    public void assignCohort(String userId, String cohort) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            UserActivity activity = activityRepository.getOrCreate(userId);
            activity.addCohort(cohort);
            membershipRepository.findActiveByUserId(userId)
                    .ifPresent(m -> evaluateAndApplyTierProgression(userId));
        } finally {
            lock.unlock();
        }
    }

    private UserMembership changeTier(String userId, String targetTierId, boolean upgrade) {
        ReentrantLock lock = getLock(userId);
        lock.lock();
        try {
            UserMembership active = getActiveMembershipOrThrow(userId);
            MembershipTier currentTier = tierEvaluationService.getTierById(active.getTierId());
            MembershipTier targetTier = tierEvaluationService.getTierById(targetTierId);

            if (upgrade && targetTier.getRank() <= currentTier.getRank()) {
                throw new MembershipException("Target tier must be higher than current tier for upgrade");
            }
            if (!upgrade && targetTier.getRank() >= currentTier.getRank()) {
                throw new MembershipException("Target tier must be lower than current tier for downgrade");
            }

            validateTierEligibility(userId, targetTier);
            return membershipRepository.save(active.withTier(targetTier.getId()));
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLock(String userId) {
        int index = Math.abs(userId.hashCode()) % STRIPE_COUNT;
        return locks[index];
    }

    private void validateTierEligibility(String userId, MembershipTier tier) {
        UserActivity activity = activityRepository.getOrCreate(userId);
        if (!tierEvaluationService.qualifiesForTier(activity, tier)) {
            throw new MembershipException("User does not meet criteria for tier: " + tier.getName());
        }
    }

    private UserMembership getActiveMembershipOrThrow(String userId) {
        return membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership for user: " + userId));
    }

    private Instant calculateExpiry(Instant start, PlanDuration duration) {
        return start.atZone(java.time.ZoneOffset.UTC)
                .plus(duration.getUnits(), duration.getChronoUnit())
                .toInstant();
    }
}
