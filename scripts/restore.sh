#!/usr/bin/env bash
set -euo pipefail

: "${LIVOCLOUD_DATABASE_URL:?Set LIVOCLOUD_DATABASE_URL to the target libpq connection URI}"

archive="${1:-}"
if [[ -z "$archive" || ! -f "$archive" ]]; then
  printf 'Usage: LIVOCLOUD_DATABASE_URL=... %s PATH_TO_BACKUP.dump\n' "$0" >&2
  exit 64
fi

pg_restore --list "$archive" >/dev/null
pg_restore --dbname="$LIVOCLOUD_DATABASE_URL" --clean --if-exists --no-owner --no-acl --exit-on-error "$archive"
printf 'Restore completed from: %s\n' "$archive"
