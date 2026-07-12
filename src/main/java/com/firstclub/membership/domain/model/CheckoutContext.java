package com.firstclub.membership.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class CheckoutContext {

    private final String userId;
    private final BigDecimal cartSubtotal;
    private final BigDecimal deliveryFee;
    private final List<String> cartCategories;

    public CheckoutContext(String userId, BigDecimal cartSubtotal, BigDecimal deliveryFee,
                           List<String> cartCategories) {
        this.userId = Objects.requireNonNull(userId);
        this.cartSubtotal = cartSubtotal == null ? BigDecimal.ZERO : cartSubtotal;
        this.deliveryFee = deliveryFee == null ? BigDecimal.ZERO : deliveryFee;
        this.cartCategories = cartCategories == null ? List.of() : List.copyOf(cartCategories);
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getCartSubtotal() {
        return cartSubtotal;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public List<String> getCartCategories() {
        return cartCategories;
    }
}
