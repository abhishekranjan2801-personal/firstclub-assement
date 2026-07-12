package com.firstclub.membership.repository;

import com.firstclub.membership.domain.model.MembershipTier;

import java.util.List;
import java.util.Optional;

public interface MembershipTierRepository {

    List<MembershipTier> findAll();

    Optional<MembershipTier> findById(String id);

    Optional<MembershipTier> findByRank(int rank);
}
