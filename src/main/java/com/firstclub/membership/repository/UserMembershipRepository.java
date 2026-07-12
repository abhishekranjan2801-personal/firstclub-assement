package com.firstclub.membership.repository;

import com.firstclub.membership.domain.model.UserMembership;

import java.util.Optional;

public interface UserMembershipRepository {

    Optional<UserMembership> findActiveByUserId(String userId);

    UserMembership save(UserMembership membership);

    Optional<UserMembership> findById(String id);
}
