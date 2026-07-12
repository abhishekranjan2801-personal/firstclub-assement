package com.firstclub.membership.domain.model;

import com.firstclub.membership.domain.enums.BenefitType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class Benefit {

    private final String id;
    private final BenefitType type;
    private final String description;
    private final Map<String, String> metadata;

    public Benefit(String id, BenefitType type, String description, Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.description = description;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public String getId() {
        return id;
    }

    public BenefitType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public static Benefit freeDelivery(String id, int minOrderValue) {
        return new Benefit(id, BenefitType.FREE_DELIVERY, "Free delivery on eligible orders",
                Map.of("minOrderValue", String.valueOf(minOrderValue)));
    }

    public static Benefit percentageDiscount(String id, int discountPercent, String categories) {
        return new Benefit(id, BenefitType.PERCENTAGE_DISCOUNT,
                discountPercent + "% discount on " + categories,
                Map.of("discountPercent", String.valueOf(discountPercent), "categories", categories));
    }

    public static Benefit exclusiveDeals(String id) {
        return new Benefit(id, BenefitType.EXCLUSIVE_DEALS, "Access to exclusive member-only deals", Map.of());
    }

    public static Benefit earlyAccess(String id, int hoursEarly) {
        return new Benefit(id, BenefitType.EARLY_ACCESS_SALES,
                "Early access to sales by " + hoursEarly + " hours",
                Map.of("hoursEarly", String.valueOf(hoursEarly)));
    }

    public static Benefit prioritySupport(String id) {
        return new Benefit(id, BenefitType.PRIORITY_SUPPORT, "Priority customer support", Map.of());
    }

    public static Benefit fasterDelivery(String id, int deliveryHours) {
        return new Benefit(id, BenefitType.FASTER_DELIVERY,
                "Faster delivery within " + deliveryHours + " hours",
                Map.of("deliveryHours", String.valueOf(deliveryHours)));
    }

    public static Benefit exclusiveCoupons(String id, String couponCode, int discountPercent) {
        return new Benefit(id, BenefitType.EXCLUSIVE_COUPONS,
                "Exclusive coupon " + couponCode + " — " + discountPercent + "% off",
                Map.of("couponCode", couponCode, "discountPercent", String.valueOf(discountPercent)));
    }
}
