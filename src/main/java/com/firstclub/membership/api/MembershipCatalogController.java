package com.firstclub.membership.api;

import com.firstclub.membership.api.dto.DtoMapper;
import com.firstclub.membership.api.dto.DtoMapper.PlanResponse;
import com.firstclub.membership.api.dto.DtoMapper.TierResponse;
import com.firstclub.membership.service.MembershipCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
public class MembershipCatalogController {

    private final MembershipCatalogService catalogService;

    public MembershipCatalogController(MembershipCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/plans")
    public List<PlanResponse> getPlans() {
        return catalogService.getAllPlans().stream()
                .map(DtoMapper::toPlanResponse)
                .toList();
    }

    @GetMapping("/tiers")
    public List<TierResponse> getTiers() {
        return catalogService.getAllTiers().stream()
                .map(DtoMapper::toTierResponse)
                .toList();
    }
}
