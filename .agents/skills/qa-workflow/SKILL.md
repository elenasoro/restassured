---
name: qa-workflow
description: Guided five-stage QA automation workflow covering task analysis, test pyramid, API test case design, Java REST Assured implementation, and code review; use when user requests a complete end-to-end QA task workflow.
---

# QA automation workflow (five stages)

Use this single skill when the user wants an end-to-end guided workflow instead of separate named skills. Work one stage at a time and get user approval before moving to code.

## Stage 1: analyze-task

# Goal
Turn a task into verifiable requirements without inventing product behavior.

## Workflow
1. Read the user-supplied story and acceptance criteria. If this is a repository task, inspect relevant specifications and `AGENTS.md`; never assume access to Jira.
2. Extract facts as numbered requirements (`REQ-01` etc.). Separate explicit requirements, inferred hypotheses and unknowns.
3. Find missing behavior: invalid input, boundaries, missing and null fields, auth/permissions, conflict states, repeated/concurrent requests, failure dependencies, timeouts, and data consistency **only where relevant**.
4. Rank risks by business impact and likelihood; name the risky user journeys and affected components.
5. Draft concise questions for PM/PO/dev, specifying which test expectation each answer would resolve.
6. Produce a traceable matrix with `Requirement | Expected behavior | Evidence | Unknowns | Risk | Suggested next check`.

## Output
Provide: task summary; confirmed requirements; ambiguities and questions; edge cases; P0/P1/P2 risk list; testability blockers; proposed next step (`test-pyramid`).

## Safety and collaboration
Do not write tests or change files. Do not claim contracts or HTTP statuses without supporting evidence. If a source is unavailable, state it plainly. Do not execute commands, commit, or push without explicit user permission. Respond in Russian unless requested otherwise.

## Stage 2: test-pyramid

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

## Stage 3: api-test-design

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

## Stage 4: api-automation

# Goal
Implement small, maintainable, correctly asserted API tests while leaving execution and Git actions under user control.

## Before editing
1. Inspect `AGENTS.md`, `pom.xml`, `src/test`, existing naming, specs, API client and DTO patterns. Report what exists versus what is assumed.
2. Identify approved `TC IDs`, source of expected assertions, target files and environmental requirements.
3. Propose minimal modifications, and **ask for permission before changing files**. Prefer one test or a small cohesive group at a time.

## Implementation rules
- Use project-defined Java/tool versions; default only for a new project: Java 25, Maven, REST Assured, TestNG and AssertJ; Jackson/Lombok when they materially simplify DTO use.
- Keep Arrange/Act/Assert obvious. Prefer deterministic fixtures and isolation. Use appropriate `RequestSpecification` reuse rather than repeated scattered base URIs if project supports it.
- Assert meaningful status, schema/fields and persistent business effects if required. Ground assertions in requirements; do not silently convert known errors to happy-path assertions.
- Avoid `Thread.sleep`, global mutable state, hardcoded secrets, order-dependent tests, production changes and catch-all exception swallowing.
- If public API requires auth, read key from environment (never echo it). Do not assume the behavior of ReqRes, payments or other external providers without their current documented contract.
- Be concise with abstractions: do not add a DTO/client/framework layer for a single trivial test unless consistent with repository patterns.

## After editing
Summarize changed files and explain each critical assertion. Give exact local Maven or IDE command for user to run, but **do not execute tests** until explicitly requested. Never commit, push, create PR or rewrite unrelated code without explicit approval.

## Output
`Plan → Proposed files → (approval) → Code changes → Assertions explained → Run instructions → Uncovered risks`. Respond in Russian unless requested otherwise.

## Stage 5: qa-code-review

# Goal
Give evidence-based QA automation review, not vague style advice.

## Workflow
1. Read requirements, relevant test code, changed files or supplied diff and `AGENTS.md`. If diff not available, review only provided files and explicitly state scope.
2. Verify source of expected results; focus on assertions that could pass despite a product defect, missing state checks and misread response shape.
3. Check deterministic setup/teardown, shared state, data collision, concurrent execution, timeouts, async polling, cleanup and dependencies on external service data.
4. Check security (secret values in code/logs), readability, unnecessary abstraction, fragile JSON paths, DTO mappings, error diagnostics and CI suitability.
5. Map scenarios to requirements, list high-risk missing tests and evaluate level choice using Testing Pyramid.
6. Label verified findings `Critical`, `Major` or `Minor`; include file/line when known, precise failure mode, evidence and narrow fix. Distinguish speculative risks from demonstrated issues.

## Output
First: verdict and scope. Then table `Severity | File:line | Finding | Why it matters | Minimal fix`; finally missing coverage, strengths, suggested follow-up checks. If no findings, say so and mention review limitations.

## Restrictions
Review only by default: do not change files, run commands, tests, commits, pushes or PR actions without explicit permission. Never claim tests were run without actual results. Respond in Russian unless requested otherwise.

