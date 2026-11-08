---
name: api-test-design
description: Produce traceable positive, negative, boundary, auth, contract, persistence and concurrency API test cases from confirmed API requirements or an OpenAPI contract. Use before implementing REST Assured automation.
---

# Goal
Produce an executable test design grounded in the API contract, not imaginary expected behavior.

## Workflow
1. Read endpoint, method, authentication, body/schema, response examples and existing tests. Read `AGENTS.md` if in a repository.
2. Build a coverage matrix for relevant equivalence classes: valid data, empty/missing/null, wrong types, length/limits, unknown resources, conflicting data, pagination/filtering, role-based access, expired/missing credentials, validation errors, response schema, persistence, idempotency and concurrency where applicable.
3. Separate positive, negative, boundary, contract and stateful checks. Include HTTP-level and business assertions; avoid status-only tests when body/state matters.
4. Derive every expected status, error code and message from a supplied specification or confirmed behavior. Mark unspecified assertions `NEEDS_CONFIRMATION`; never guess.
5. Define independent test data, fixtures/cleanup, environment prerequisites, prerequisites for non-idempotent calls, and risk of external service rate limits.
6. Prioritize P0 smoke, P1 regression, P2 additional scenarios; de-duplicate coverage with unit and integration levels.

## Output
Table with `TC ID | Requirement | Title | Preconditions | Request/input | Expected HTTP + body/state | Level | Priority | Automate | Notes`. Follow with unresolved questions and a recommended first 1-3 automation targets.

## Constraints
Design only: do not create or edit files unless requested. Do not execute tests, commit or push without explicit permission. Respond in Russian unless requested otherwise.
