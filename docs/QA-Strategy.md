# QA Strategy, Test Strategy & Quality Gates – Template

> A proposal for a software vendor that delivers the **same product as SaaS (cloud) and as on-prem installation**,
> in a regulated domain (energy billing / market communication, healthcare). Tech stack: Java, JavaScript, GitLab, Jira Xray.
> Placeholders are written as `<...>`.

---

## 1. QA Strategy (the *why* – organisation-wide, changes rarely)

| Topic | Proposal |
|---|---|
| **Quality goal** | No wrong invoices, no failed market communication, no data-protection incident. Fast feedback for developers. |
| **Principle** | *Quality is a team responsibility* – QA enables, coaches and builds the safety net; devs own unit tests. |
| **Shift left** | QA joins refinement: acceptance criteria (Given/When/Then) are written **before** development starts. |
| **Shift right** | Cloud: monitoring, synthetic smoke tests in production, error-rate based canary rollout. |
| **Automation first** | Every regression test case that is executed more than twice is a candidate for automation. |
| **Risk based** | Test depth follows business risk (billing, regulatory deadlines, data migration = highest). |
| **Traceability** | Requirement (Jira) → Test (Xray) → Execution (CI) → Defect. Audit-ready (ISO 27001, customer audits). |
| **One codebase, two delivery models** | The same automated suite runs against cloud staging **and** an on-prem reference installation. |

## 2. Test Strategy (the *how* – per product, reviewed every quarter)

### 2.1 Test levels (test pyramid)

| Level | Who | Tooling | Scope | Runs |
|---|---|---|---|---|
| Unit | Dev | JUnit 5, AssertJ, Mockito | Business rules (tariffs, rounding, plausibility) | every push |
| Integration / API | Dev + QA | JUnit 5, REST Assured, Testcontainers (DB) | Services, DB, interfaces (e.g. EDIFACT/market communication formats) | every push |
| UI / E2E | QA | Selenium (Java), TestCafe (JS) | Critical user journeys only (≈ 10–20 % of tests) | main branch, nightly |
| Non-functional | QA + Ops | JMeter/Gatling, OWASP ZAP, dependency check | Performance of billing runs, security | nightly / before release |
| Exploratory & UAT | QA + PO + key users | Xray test sessions | New features, usability | per sprint |

### 2.2 Cloud vs. on-prem – what is different

| Aspect | Cloud (SaaS) | On-prem |
|---|---|---|
| Release cadence | Continuous / per sprint | Bundled releases (e.g. quarterly) + hotfixes |
| Environments | One version, many tenants | Many versions in the field, customer specific setups |
| Extra tests | Multi-tenancy isolation, scaling, zero-downtime deployment, backup/restore | **Installation & upgrade path** (N-1 → N, N-2 → N), supported DB/OS matrix, customer customising |
| Data migration | Tested in pipeline per tenant | Migration scripts tested on anonymised customer-like data |
| Feedback | Monitoring, logs, feature flags | Support tickets – so the release gate must be stricter |
| Rollback | Automatic (canary / blue-green) | Documented rollback + DB backup in upgrade guide |

### 2.3 Risk-based prioritisation

| Risk class | Example | Required coverage |
|---|---|---|
| A – critical | Invoice calculation, VAT, supplier switching deadlines, migration | Unit + API + E2E, 100 % of acceptance criteria automated, 4-eyes review of tests |
| B – high | Customer portal, meter reading import | Unit + API, E2E for happy path |
| C – normal | Reports, UI layout | Unit, exploratory |

### 2.4 Test data & environments
- Synthetic test data generators per domain (customer, contract, meter, tariff); **no production personal data** (GDPR).
- Environments: `dev` → `cloud-staging` + `onprem-reference` → `pre-prod` → `prod`.
- Test environments are created from code (Docker/Helm) so they are reproducible.

### 2.5 Test management in Jira Xray
- Story → *Test* issues (manual or automated: `@Requirement("BILL-101")` in JUnit).
- *Test Plan* per release, *Test Execution* per pipeline run (JUnit XML imported automatically from GitLab).
- Defects linked to the failed test run; coverage report per requirement is the release evidence.

### 2.6 Definition of Done (QA part)
- [ ] Acceptance criteria tested, automated tests merged and green
- [ ] Coverage of new code ≥ 80 %, no new critical static-analysis findings
- [ ] Test cases linked to the story in Xray
- [ ] Tested on cloud staging **and** on-prem reference (if the feature is shipped on-prem)
- [ ] No open bugs of severity Blocker/Critical for the story

## 3. Quality Gates

| Gate | When | Criteria (all must be green) | Owner |
|---|---|---|---|
| **QG1 – Commit** | every push | Build OK, all unit tests green, line coverage ≥ 80 % | Dev |
| **QG2 – Merge** | merge request → main | QG1 + code review approved, SonarQube gate passed, no CVE ≥ 7.0 | Dev + reviewer |
| **QG3 – Staging** | after deploy to staging | Smoke + E2E green on cloud **and** on-prem reference, upgrade N-1 → N works | QA |
| **QG4 – Release** | release candidate tag | Xray test plan 100 % executed, 0 failed (or accepted waiver by PO), 0 open Blocker/Critical, performance within baseline, release notes ready | QA lead + PO |
| **QG5 – Production** (cloud) | rollout | Canary: error rate & latency within SLO for `<x>` min, synthetic smoke green | Ops + QA |

> Waivers are allowed only documented in Jira, with PO approval and a fix version.

## 4. Metrics (reported per sprint)
- Automation rate of regression suite (target: `<70 %>` after 6 months)
- Pipeline duration up to QG2 (target: < 15 min) and flaky test rate (target: < 2 %)
- Defect leakage: bugs found in production / on customer systems vs. found before release
- Requirement coverage in Xray per release
- Mean time to detect / to fix critical bugs

## 5. Roadmap – first 90 days as Senior QA

| Phase | Focus |
|---|---|
| **Days 1–30 – understand** | Analyse products, current test process, existing tests, top defect sources; interviews with POs/devs/support; quick wins (e.g. JUnit reports in GitLab MRs). |
| **Days 31–60 – foundation** | Agree on test strategy and quality gates; set up Xray ↔ GitLab integration; first automated smoke suite for the most critical journey; test data concept. |
| **Days 61–90 – scale** | Extend regression automation by risk class; on-prem upgrade test in pipeline; metrics dashboard; coach team (pairing, test reviews, guidelines). |
