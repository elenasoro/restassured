---
name: api-automation
description: Implement or refine agreed API automated tests in an existing Java 25 REST Assured TestNG AssertJ Maven project, using DTO/Jackson/Lombok where appropriate. Use when asked to turn approved test cases into code.
---

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
