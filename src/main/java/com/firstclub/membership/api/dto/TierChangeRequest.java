package com.firstclub.membership.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierChangeRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String targetTierId;
}