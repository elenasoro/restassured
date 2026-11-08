---
name: test-pyramid
description: Design risk-based test coverage across unit, component, integration, API and end-to-end levels for a user story or change. Use when deciding what to automate where, and when reviewing gaps or duplicated tests.
---

# Goal
Choose the cheapest reliable level that detects each important defect and explain the trade-off.

## Method
1. Read the requirements, architecture and known dependencies; distinguish actual facts from guesses.
2. Identify business invariants, contract rules, persistence boundaries and customer-visible flows.
3. Assign **primary** test level to each risk: Unit (pure logic); Component (one service with mocked dependencies); Integration (real components, persistence/queue); API (HTTP contract and business behavior at interface); E2E (critical real user journey). API tests may themselves be integration tests; do not pretend these categories are mutually exclusive.
4. Recommend additional coverage at a second level only when it catches a distinct failure (e.g. validation algorithm at Unit and mapped error at API).
5. Identify owner (dev/QA/team agreement), setup cost, speed, determinism and priority. Favor few meaningful E2E tests, no fixed coverage percentages.
6. Flag missing information about architecture, access to test data, environments and ownership.

## Output
Table: `Requirement/Risk | Test scenario | Primary level | Secondary level if useful | Rationale | Owner | Priority | Automated?`. Then list duplicates to remove, key gaps, smoke subset, and residual risk. Explain at least 2 important trade-offs.

## Constraints
Do not automatically implement tests. Do not assume all HTTP requests are isolated unit tests or that all backend tests are API tests. Do not invent architecture or endpoints. No changes, commands, commits or pushes without explicit approval. Respond in Russian unless requested otherwise.
