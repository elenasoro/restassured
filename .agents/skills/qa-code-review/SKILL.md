---
name: qa-code-review
description: Review Java REST Assured TestNG AssertJ API test changes for correctness, maintainability, flaky tests, test isolation, security and requirement coverage. Use when reviewing generated tests, a diff or a PR.
---

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
