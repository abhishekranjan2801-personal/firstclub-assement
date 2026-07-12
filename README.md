# FirstClub Membership Program
## Architecture

```
api/          → REST controllers
domain/       → Entities, enums (Plan, Tier, Benefit, Subscription)
repository/   → Repository interfaces + in-memory implementations
service/      → Business logic (catalog, subscription, tier evaluation)
config/       → Seed data initializer
```

### Design Highlights

- **Configurable benefits** — Each tier carries a list of `Benefit` objects with type-specific metadata (discount %, categories, delivery thresholds).
- **Pluggable tier criteria** — `TierQualificationStrategy` implementations (order count, monthly spend, cohort) compose via AND logic.
- **Thread-safe in-memory storage** — `ConcurrentHashMap`, atomic counters, and read/write locks for subscription mutations.
- **Immutable domain objects** — `UserMembership` uses a builder with `with*` methods for state transitions.

## Tiers (seeded)

| Tier     | Criteria                                      | Key Benefits                          |
|----------|-----------------------------------------------|---------------------------------------|
| Silver   | Default (0 orders)                            | 5% discount, free delivery ≥ ₹499     |
| Gold     | ≥ 5 orders AND ≥ ₹2000/monthly spend          | 10% discount, 12h early access        |
| Platinum | ≥ 15 orders AND ≥ ₹5000/month AND VIP cohort  | 15% discount, priority support        |

## Plans (seeded)

| Plan       | Price   | Duration  |
|------------|---------|-----------|
| Monthly    | ₹299    | 1 month   |
| Quarterly  | ₹799    | 3 months  |
| Yearly     | ₹2499   | 12 months |

## Run

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`.

## API Reference

### Catalog

```bash
# List plans
curl http://localhost:8080/api/membership/plans

# List tiers with benefits and criteria
curl http://localhost:8080/api/membership/tiers
```

### Subscription lifecycle

```bash
# Subscribe (plan + tier)
curl -X POST http://localhost:8080/api/membership/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","planId":"plan-monthly","tierId":"tier-silver"}'

# Get current membership
curl http://localhost:8080/api/membership/users/alice

# Upgrade tier
curl -X POST http://localhost:8080/api/membership/upgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","targetTierId":"tier-gold"}'

# Downgrade tier
curl -X POST http://localhost:8080/api/membership/downgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","targetTierId":"tier-silver"}'

# Cancel
curl -X POST http://localhost:8080/api/membership/cancel \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice"}'
```

### Tier progression (order activity)

```bash
# Record an order (auto-evaluates tier progression)
curl -X POST http://localhost:8080/api/membership/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","orderValue":500.00}'

# Assign user to a cohort (e.g. VIP for Platinum)
curl -X POST http://localhost:8080/api/membership/cohorts \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","cohort":"VIP"}'

# Manually trigger tier re-evaluation
curl -X POST http://localhost:8080/api/membership/evaluate-tier/alice
```

## Demo Flow

```bash
# 1. Subscribe alice to monthly Silver
curl -s -X POST http://localhost:8080/api/membership/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId":"alice","planId":"plan-monthly","tierId":"tier-silver"}' | jq

# 2. Place 5 orders → auto-upgrade to Gold
for i in 1 2 3 4 5; do
  curl -s -X POST http://localhost:8080/api/membership/orders \
    -H "Content-Type: application/json" \
    -d '{"userId":"alice","orderValue":500}' > /dev/null
done

# 3. Check membership — should show Gold tier
curl -s http://localhost:8080/api/membership/users/alice | jq '.tier.name'
```

## Tests

```bash
mvn test
```

## Extending

- **Add a new benefit type** — Extend `BenefitType` enum and add a factory on `Benefit`.
- **Add tier criteria** — Implement `TierQualificationStrategy` and register it in `TierEvaluationService`.
- **Swap storage** — Replace in-memory repositories with JPA/Redis implementations behind the same interfaces.
