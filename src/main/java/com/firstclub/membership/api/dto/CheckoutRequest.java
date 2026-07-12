package com.firstclub.membership.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank
    private String userId;

    @NotNull
    @PositiveOrZero
    private BigDecimal cartSubtotal;

    @NotNull
    @PositiveOrZero
    private BigDecimal deliveryFee;

    private List<String> cartCategories;
}