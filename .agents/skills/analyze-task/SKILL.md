---
name: analyze-task
description: Analyze Jira stories, acceptance criteria, API specifications and feature requests for testability, assumptions, risks, edge cases and questions before test implementation. Use when receiving a task or reviewing requirements.
---

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
