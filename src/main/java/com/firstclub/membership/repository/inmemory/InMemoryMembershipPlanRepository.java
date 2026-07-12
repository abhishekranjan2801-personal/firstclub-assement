package com.firstclub.membership.repository.inmemory;

import com.firstclub.membership.domain.model.MembershipPlan;
import com.firstclub.membership.repository.MembershipPlanRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMembershipPlanRepository implements MembershipPlanRepository {

    private final Map<String, MembershipPlan> plans = new ConcurrentHashMap<>();

    public void register(MembershipPlan plan) {
        plans.put(plan.getId(), plan);
    }

    @Override
    public List<MembershipPlan> findAll() {
        return new ArrayList<>(plans.values());
    }

    @Override
    public Optional<MembershipPlan> findById(String id) {
        return Optional.ofNullable(plans.get(id));
    }
}
