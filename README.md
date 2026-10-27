# Energy Billing – QA Demo

A small demo of how test automation and quality gates could look for a utility billing product that is
delivered both **in the cloud (SaaS)** and **on-prem**. Domain: yearly invoice, monthly instalment (Abschlag) and
meter reading plausibility check.

| Part | Where | Tooling |
|---|---|---|
| Business logic | `src/main/java/.../billing` | Java 21 |
| Unit tests linked to requirements | `src/test/java/.../billing` | JUnit 5, AssertJ, `@Requirement("BILL-101")` |
| UI test with Page Object | `src/test/java/.../billing/ui` | Selenium 4 |
| JS E2E test | `e2e-testcafe/` | TestCafe |
| Customer portal page under test | `src/main/resources/portal/abschlagsrechner.html` | HTML/JS |
| CI/CD with quality gates QG1–QG4 | `.gitlab-ci.yml` | GitLab CI, JaCoCo, Sonar, Xray import |
| QA strategy / test strategy / gates template | `docs/QA-Strategy.md` | – |

## Run

```bash
mvn verify                                        # unit tests + coverage gate (>= 80 %)
mvn test -Dgroups=ui -Dexcluded.groups= -Djacoco.skip=true   # Selenium UI tests (needs Chrome)
cd e2e-testcafe && npm install && npm test        # TestCafe (needs Node >= 18)
```

Both UI suites accept a target URL (`-Dportal.url=...` / `PORTAL_URL=...`), so the same tests run against cloud staging
and an on-prem reference installation.
