package com.firstclub.membership.repository.inmemory;

import com.firstclub.membership.domain.model.MembershipTier;
import com.firstclub.membership.repository.MembershipTierRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMembershipTierRepository implements MembershipTierRepository {

    private final Map<String, MembershipTier> tiers = new ConcurrentHashMap<>();

    public void register(MembershipTier tier) {
        tiers.put(tier.getId(), tier);
    }

    @Override
    public List<MembershipTier> findAll() {
        return tiers.values().stream()
                .sorted(Comparator.comparingInt(MembershipTier::getRank))
                .toList();
    }

    @Override
    public Optional<MembershipTier> findById(String id) {
        return Optional.ofNullable(tiers.get(id));
    }

    @Override
    public Optional<MembershipTier> findByRank(int rank) {
        return tiers.values().stream()
                .filter(tier -> tier.getRank() == rank)
                .findFirst();
    }
}
