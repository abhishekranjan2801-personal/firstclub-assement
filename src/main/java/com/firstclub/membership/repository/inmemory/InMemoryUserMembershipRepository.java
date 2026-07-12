package com.firstclub.membership.repository.inmemory;

import com.firstclub.membership.domain.enums.SubscriptionStatus;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.repository.UserMembershipRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class InMemoryUserMembershipRepository implements UserMembershipRepository {

    private final Map<String, UserMembership> membershipsById = new ConcurrentHashMap<>();
    private final Map<String, String> activeMembershipIdByUser = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Optional<UserMembership> findActiveByUserId(String userId) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(activeMembershipIdByUser.get(userId))
                    .map(membershipsById::get)
                    .filter(m -> m.getStatus() == SubscriptionStatus.ACTIVE && m.getExpiresAt().isAfter(Instant.now()));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public UserMembership save(UserMembership membership) {
        lock.writeLock().lock();
        try {
            membershipsById.put(membership.getId(), membership);
            if (membership.getStatus() == SubscriptionStatus.ACTIVE) {
                activeMembershipIdByUser.put(membership.getUserId(), membership.getId());
            } else {
                activeMembershipIdByUser.remove(membership.getUserId(), membership.getId());
            }
            return membership;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<UserMembership> findById(String id) {
        return Optional.ofNullable(membershipsById.get(id));
    }
}
