package com.firstclub.membership.service;

import com.firstclub.membership.domain.model.UserActivity;
import com.firstclub.membership.domain.model.UserMembership;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.repository.UserActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SubscriptionConcurrencyTest {

    private static final int THREADS = 200;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserActivityRepository activityRepository;

    @Test
    void concurrentOrdersAreAllCountedAndTierIsConsistent() throws InterruptedException {
        String userId = "concurrent-orders-user";
        subscriptionService.subscribe(userId, "plan-monthly", "tier-silver");

        runConcurrently(THREADS, () ->
                subscriptionService.recordOrder(userId, new BigDecimal("100.00")));

        UserActivity activity = activityRepository.findByUserId(userId).orElseThrow();
        assertEquals(THREADS, activity.getTotalOrderCount(), "every order must be counted exactly once");
        assertEquals(0, new BigDecimal("100.00").multiply(BigDecimal.valueOf(THREADS))
                .compareTo(activity.getMonthlyOrderValue()), "monthly value must sum without lost updates");

        // 200 orders * 100 = 20,000 monthly value, well past Platinum's 5,000 / 15-order bar,
        // but Platinum also needs the VIP cohort — so the highest reachable tier here is Gold.
        UserMembership membership = subscriptionService.getCurrentMembership(userId);
        assertEquals("tier-gold", membership.getTierId());
    }

    @Test
    void concurrentSubscribeAllowsExactlyOne() throws InterruptedException {
        String userId = "concurrent-subscribe-user";
        AtomicInteger success = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        runConcurrently(THREADS, () -> {
            try {
                subscriptionService.subscribe(userId, "plan-monthly", "tier-silver");
                success.incrementAndGet();
            } catch (MembershipException e) {
                rejected.incrementAndGet();
            }
        });

        assertEquals(1, success.get(), "exactly one subscription must win the race");
        assertEquals(THREADS - 1, rejected.get(), "all other attempts must be rejected");
        assertEquals("tier-silver",
                subscriptionService.getCurrentMembership(userId).getTierId());
    }

    private void runConcurrently(int threads, Runnable action) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        try {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    try {
                        start.await();       // release all threads at once for max contention
                        action.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            assertTrue(done.await(30, TimeUnit.SECONDS), "all threads must finish in time");
        } finally {
            pool.shutdownNow();
        }
    }
}
