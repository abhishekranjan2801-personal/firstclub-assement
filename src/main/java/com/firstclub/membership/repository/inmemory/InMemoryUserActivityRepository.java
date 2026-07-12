package com.firstclub.membership.repository.inmemory;

import com.firstclub.membership.domain.model.UserActivity;
import com.firstclub.membership.repository.UserActivityRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserActivityRepository implements UserActivityRepository {

    private final Map<String, UserActivity> activities = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryUserActivityRepository(Clock clock) {
        this.clock = clock;
    }

    @Override
    public UserActivity getOrCreate(String userId) {
        return activities.computeIfAbsent(userId, id -> new UserActivity(id, clock));
    }

    @Override
    public Optional<UserActivity> findByUserId(String userId) {
        return Optional.ofNullable(activities.get(userId));
    }
}