# LivoCloud operations

## Startup and migrations

Startup is fail-closed: Flyway applies `src/db/migration` before account DAOs
serve traffic. Review every migration and take a verified backup before deploying
a schema change. Do not edit an applied migration; add a new version.

After deployment, check:

```sh
curl --fail --silent --show-error https://your-host.example/LivoCloud/health
```

A 503 means the process is reachable but PostgreSQL is unavailable. Application
logs go to container stdout/stderr. Alert on repeated health failures, mail
delivery failures, authentication error spikes, and unexpected migration errors.

## Backup and restore

The helper scripts require PostgreSQL client tools and `LIVOCLOUD_DATABASE_URL`
in libpq URI form. A backup is not complete until a restore drill succeeds.

```sh
export LIVOCLOUD_DATABASE_URL='postgresql://user:password@host:5432/livocloud'
./scripts/backup.sh ./backups
createdb livocloud_restore_test
LIVOCLOUD_DATABASE_URL='postgresql://user:password@host:5432/livocloud_restore_test' \
  ./scripts/restore.sh ./backups/livocloud-YYYYmmddTHHMMSSZ.dump
```

The backup script writes PostgreSQL custom format and asks `pg_restore` to list
the archive before reporting success. Store archives encrypted outside the
application host, enforce retention, and restrict access because account email
addresses are personal data.

`restore.sh` is destructive for objects represented in the selected archive.
Run it only against the intended empty/recovery database and verify the URL first.

## Registration failure recovery

- Database failure: the transaction rolls back; the customer can safely retry.
- Mail failure: account/profile/token remain committed and the UI exposes Retry.
- Retry: all earlier active tokens become passive before the new token commits.
- Expired/used token: verification returns a generic error and does not change
  account state. A session-bound retry creates a new token.
- Migration failure: application startup stops. Restore the prior release,
  diagnose the migration, and add a corrective migration; never mutate an
  already-applied production migration.

Purge `registration_attempts` records older than the operational retention window
during routine database maintenance; only digests are stored, and records older
than 15 minutes do not influence throttling.

## Release checklist

1. `mvn clean verify` passes on the proposed commit.
2. A secret scan of the working tree passes; the historical AWS, Mailgun, PayPal,
   and demo API credentials have been rotated/revoked.
3. `docker compose config` validates the topology if Compose is used.
4. A current backup and restore-drill result exist.
5. Deploy, confirm `/health`, then exercise start/profile/mail/verify/login with a
   non-production mail recipient.
6. Confirm `account_audit` contains the expected events and no raw token or
   password appears in logs or database columns.
