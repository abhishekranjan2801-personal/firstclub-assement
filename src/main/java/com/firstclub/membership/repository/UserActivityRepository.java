package com.firstclub.membership.repository;

import com.firstclub.membership.domain.model.UserActivity;

import java.util.Optional;

public interface UserActivityRepository {

    UserActivity getOrCreate(String userId);

    Optional<UserActivity> findByUserId(String userId);
}
