# Completeness Review: livocloud

**Review date:** 2026-07-18

## Assessment basis

Static inspection of project-owned source and configuration only; no dependency installation, build, database migration, external-service call, or runtime launch was performed. The scan considered 11316 project files (3458 source files), 4 manifest(s), 55 test-like file(s), and 0 CI workflow(s), excluding dependency/generated directories.

## Classification

**Functional but incomplete**

This is a substantive but unfinished application workflow application, not just an empty scaffold. Inspection found 3458 source files across `WebContent/`, `src/` using Next.js, Express, Rails, JVM; however, the checked-in workflow and delivery controls do not yet demonstrate a complete, production-operable product.

## Why it is not complete

- Mock, demo, sample, fixture, or placeholder behavior remains in executable/product paths.
- No checked-in CI workflow proves builds, tests, migrations, and security checks on every change.
- No environment template documents required configuration and secret boundaries.
- No clear deployment/container configuration demonstrates a reproducible production topology.

## Needed features

1. Define the primary user and acceptance criteria, then complete one end-to-end workflow against persistent data instead of demo fixtures.
2. Replace mocks, placeholders, and generic AI responses with validated domain services and explicit failure/retry behavior.
3. Implement secure identity, role/tenant boundaries, input validation, secrets handling, and auditable state changes.
4. Add representative automated tests, CI quality gates, environment documentation, migrations, observability, backup, and deployment configuration.
5. Add risk-based unit, integration, and end-to-end tests in CI, including migration and failure-path coverage.

## Risks or launch blockers

- Automation contains destructive process, filesystem, or database operations; do not run it on a shared machine without review.
- No CI evidence prevents broken or insecure changes from reaching a release.

## Evidence inspected

- `WebContent/WEB-INF/assets/global/img/flags/readme.txt`
- `WebContent/resources/adminpanel/global/plugins/jquery-inputmask/README.md:304`
- `WebContent/resources/adminpanel/global/plugins/jquery-inputmask/README.md:679`
- `WebContent/resources/adminpanel/global/plugins/angularjs/plugins/._angular-ui-router.min.js`
- `WebContent/resources/adminpanel/global/plugins/morris/spec/._lib`
- `pom.xml`

## Recommended next action

Choose one real application workflow journey, define acceptance criteria and external contracts, then close its persistence, permission, integration, failure, and test gaps before expanding features.

## Implementation progress (2026-07-20)

This increment implements one bounded journey for a prospective LivoCloud customer: start an account, complete the company/password profile, receive or retry a verification message, consume a one-time verification link, accept the terms, and authenticate as an active account. The earlier mixed-stack classification was caused by vendored frontend assets; the project-owned application is a Spring MVC WAR.

### Implemented

- Added a transactional `AccountRegistrationService` and Flyway `V1__account_registration.sql` schema for users, verification requests, account audit events, and database-backed registration throttling. Email addresses are normalized and unique, profile/resend authority comes from the server-side session user ID rather than submitted form identity, and registration attempts are limited by email/client digests in a 15-minute window.
- Passwords now use 210,000-round PBKDF2-SHA256 with a random per-password salt. Verification capabilities are 256-bit random values; only SHA-256 digests are stored. Profile update plus token issuance and token consumption plus account-state transition are atomic. Expired, replaced, used, and replayed tokens fail closed.
- Added server-side validation, synchronizer-token CSRF protection for state-changing MVC requests, security headers, UTF-8 request handling, session-ID rotation on login, generic authentication failure, explicit HTTP status codes, a database-backed `/health` endpoint, and auditable registration/verification events.
- Missing or failed mail delivery no longer masquerades as success or loses persisted work: the UI reports the failure and offers a session-bound POST retry, which invalidates the earlier active token before committing a replacement.
- Removed committed AWS, Mailgun, PayPal, and demo API credentials from the working tree and removed stale committed `build/classes` output that retained secrets. The unsafe AWS provisioning stub and PayPal/file-mutation path now return explicit HTTP 501 responses and make no infrastructure, charge, platform, or filesystem change.
- Added `.env.example`, environment-only database/mail configuration, `README.md`, operations and recovery documentation, executable PostgreSQL backup/restore helpers, a multi-stage non-root Tomcat `Dockerfile`, PostgreSQL `compose.yaml`, a health check, `.gitignore`/`.dockerignore`, and a GitHub Actions Maven verification workflow.
- Added 14 automated tests covering PBKDF2 behavior, CSRF, sessionless health probes, Spring web-context startup, Flyway migration, normalized duplicate identity, hashed persistence, verification success/replay/expiry, resend invalidation, throttling, authentication state/failure, and fail-closed external workflows.

### Verification evidence

- `mvn --batch-mode --no-transfer-progress clean verify` passed using a disposable JDK/Maven toolchain: 45 production sources and 5 test sources compiled, all 14 tests passed, the Spring web context migrated and wired successfully, and `target/livocloud-0.0.1-SNAPSHOT.war` was produced.
- A disposable Tomcat 9 plus PostgreSQL smoke run deployed the generated WAR and exercised the real JSP/session/CSRF path: health, home, signup, and profile responses were HTTP 200; PostgreSQL retained `runtime@example.invalid` despite an attacker-controlled submitted email, account state was `UNVERIFIED`, the password column was PBKDF2-encoded, the active verification column was a 64-character digest, the mail-retry screen rendered, and `/createInstance` returned HTTP 501.
- A separate disposable PostgreSQL drill applied the production migration with `ON_ERROR_STOP`, created and validated a custom-format `pg_dump`, restored it into a new database with `pg_restore --exit-on-error`, and recovered the sample account. Both temporary database clusters and all test data were removed.
- `gitleaks detect --source . --no-git --redact` reported no working-tree leaks. A raw Git-history audit identified seven findings in the original commit: AWS, Mailgun/PayPal-adjacent, and vendored demo credentials. Their exact fingerprints are acknowledged in `.gitleaksignore` so the CI history scan fails on every new finding while the exposed values await rotation/history remediation. XML parsing, shell syntax, `docker compose config`, and registry-manifest checks for all three pinned images passed.

### Remaining risks and external blockers

- The project remains **functional but incomplete** beyond the implemented account journey. Cloud provisioning and payment are intentionally unavailable (HTTP 501) until owner-scoped, idempotent, audited provider contracts and failure compensation are designed and tested. Other legacy DAOs/assets still need product decisions, migrations, authorization review, and dependency retirement.
- All exposed historical credentials must be revoked/rotated immediately. They remain recoverable from commit `2d1522d91159`; any history rewrite requires coordination with every clone and deployment. Existing plaintext-password rows intentionally cannot authenticate and need a verified reset/migration plan.
- No real Mailgun message was sent because no authorized provider credentials/domain were supplied. Production TLS/public-base configuration, provider acceptance/bounce behavior, proxy/session topology, and a real recipient verification run remain deployment gates.
- The Docker CLI is installed and the Compose topology plus image manifests validate, but the local Docker/Colima daemon is unavailable, so a full image build and container-to-container smoke run could not be executed here. The checked-in CI workflow has likewise not run on GitHub yet.
- The bounded workflow has account/session authorization but does not establish complete role/tenant authorization for future cloud resources. The legacy Spring/Jersey-era stack and vendored UI need a supported-version and supply-chain upgrade before production launch.

## Runtime and login acceptance — 2026-07-20

- **Status:** BLOCKED
- **Startup safety:** no root `start.sh` exists. The supported runtime is a WAR deployed to a Servlet 3.1 container with PostgreSQL; the checked-in Compose path was inspected and would build images and create persistent container state.
- **Startup:** not rerun in this pass. The earlier disposable Tomcat/PostgreSQL smoke recorded above succeeded, but its temporary servlet toolchain and WAR were cleaned up afterward.
- **Readiness:** the prior `/LivoCloud/health` result was `200`; no current listener is claimed.
- **Login:** the prior real JSP/session/CSRF account journey succeeded. A fresh `start.sh`-based login cannot be claimed because no Maven executable, built WAR, local Tomcat installation, or running Docker daemon is currently available.
- **Primary journey:** prior disposable signup/profile persistence evidence remains valid but is not substituted for the requested current root-launcher pass.
- **Browser/server evidence:** prior Tomcat HTTP/session evidence exists; current browser and server evidence are unavailable without the runtime toolchain.
- **Cleanup:** the previous disposable Tomcat/PostgreSQL environment was removed; no current project listener or database remains.
- **Residual issue:** provide a runnable Docker daemon or an approved local Maven/Tomcat toolchain, add a non-destructive root `start.sh`, and rerun readiness plus the session journey.

## Blocked-case audit follow-up — 2026-07-20

- **Classification:** remains **functional but incomplete / BLOCKED**, not `NOT_APPLICABLE`: this is a real Spring MVC WAR application, but its supported Servlet/PostgreSQL runtime cannot currently be launched from this host.
- **Current boundary evidence:** no working Java runtime, Maven executable, built WAR, local Tomcat installation, or running Docker daemon is available. The previously recorded disposable Tomcat/PostgreSQL account journey remains historical evidence only.
- **Supported offline checks:** XML validation for the Maven, web, and Spring descriptors; shell syntax checks for backup/restore; `docker compose config --quiet` with a validation-only database password; and `git diff --check` all passed.
- **Ports:** the assigned PostgreSQL/API/UI ports `55713`/`6220`/`6221` were intentionally not consumed because no honest supported runtime attempt was possible.
