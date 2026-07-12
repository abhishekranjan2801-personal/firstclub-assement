package com.firstclub.membership.api;

import com.firstclub.membership.api.dto.AssignCohortRequest;
import com.firstclub.membership.api.dto.CheckoutRequest;
import com.firstclub.membership.api.dto.DtoMapper;
import com.firstclub.membership.api.dto.DtoMapper.MembershipResponse;
import com.firstclub.membership.api.dto.RecordOrderRequest;
import com.firstclub.membership.api.dto.SubscribeRequest;
import com.firstclub.membership.api.dto.TierChangeRequest;
import com.firstclub.membership.api.dto.UserActionRequest;
import com.firstclub.membership.domain.model.CheckoutContext;
import com.firstclub.membership.domain.model.CheckoutResult;
import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.service.MembershipCatalogService;
import com.firstclub.membership.service.MembershipBenefitService;
import com.firstclub.membership.service.SubscriptionService;
import com.firstclub.membership.service.benefit.CheckoutBenefitService;
import com.firstclub.membership.service.tier.TierEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/membership")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final MembershipCatalogService catalogService;
    private final TierEvaluationService tierEvaluationService;
    private final MembershipBenefitService benefitService;
    private final CheckoutBenefitService checkoutBenefitService;

    public SubscriptionController(SubscriptionService subscriptionService,
                                  MembershipCatalogService catalogService,
                                  TierEvaluationService tierEvaluationService,
                                  MembershipBenefitService benefitService,
                                  CheckoutBenefitService checkoutBenefitService) {
        this.subscriptionService = subscriptionService;
        this.catalogService = catalogService;
        this.tierEvaluationService = tierEvaluationService;
        this.benefitService = benefitService;
        this.checkoutBenefitService = checkoutBenefitService;
    }

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipResponse subscribe(@Valid @RequestBody SubscribeRequest request) {
        UserMembership membership = subscriptionService.subscribe(
                request.getUserId(), request.getPlanId(), request.getTierId());
        return toResponse(membership);
    }

    @PostMapping("/upgrade")
    public MembershipResponse upgrade(@Valid @RequestBody TierChangeRequest request) {
        UserMembership membership = subscriptionService.upgradeTier(
                request.getUserId(), request.getTargetTierId());
        return toResponse(membership);
    }

    @PostMapping("/downgrade")
    public MembershipResponse downgrade(@Valid @RequestBody TierChangeRequest request) {
        UserMembership membership = subscriptionService.downgradeTier(
                request.getUserId(), request.getTargetTierId());
        return toResponse(membership);
    }

    @PostMapping("/cancel")
    public MembershipResponse cancel(@Valid @RequestBody UserActionRequest request) {
        UserMembership membership = subscriptionService.cancel(request.getUserId());
        return toResponse(membership);
    }

    @GetMapping("/users/{userId}")
    public MembershipResponse getMembership(@PathVariable String userId) {
        UserMembership membership = subscriptionService.getCurrentMembership(userId);
        return toResponse(membership);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recordOrder(@Valid @RequestBody RecordOrderRequest request) {
        subscriptionService.recordOrder(request.getUserId(), request.getOrderValue());
    }

    @PostMapping("/cohorts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignCohort(@Valid @RequestBody AssignCohortRequest request) {
        subscriptionService.assignCohort(request.getUserId(), request.getCohort());
    }

    @PostMapping("/evaluate-tier/{userId}")
    public MembershipResponse evaluateTier(@PathVariable String userId) {
        UserMembership membership = subscriptionService.evaluateAndApplyTierProgression(userId);
        return toResponse(membership);
    }

    @GetMapping("/benefits/free-delivery")
    public boolean checkFreeDelivery(@RequestParam String userId, @RequestParam BigDecimal orderValue) {
        return benefitService.isFreeDeliveryEligible(userId, orderValue);
    }

    @GetMapping("/benefits/discount")
    public BigDecimal calculateDiscount(@RequestParam String userId,
                                        @RequestParam String category,
                                        @RequestParam BigDecimal price) {
        return benefitService.calculateDiscountedPrice(userId, category, price);
    }

    @PostMapping("/checkout")
    public CheckoutResult checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutContext context = new CheckoutContext(
                request.getUserId(),
                request.getCartSubtotal(),
                request.getDeliveryFee(),
                request.getCartCategories()
        );
        return checkoutBenefitService.applyBenefits(context);
    }

    private MembershipResponse toResponse(UserMembership membership) {
        MembershipPlan plan = catalogService.getPlanById(membership.getPlanId());
        MembershipTier tier = tierEvaluationService.getTierById(membership.getTierId());
        return DtoMapper.toMembershipResponse(membership, plan, tier);
    }
}
