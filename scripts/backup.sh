#!/usr/bin/env bash
set -euo pipefail

: "${LIVOCLOUD_DATABASE_URL:?Set LIVOCLOUD_DATABASE_URL to the libpq connection URI}"

destination="${1:-./backups}"
mkdir -p "$destination"
timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
archive="$destination/livocloud-$timestamp.dump"

pg_dump --dbname="$LIVOCLOUD_DATABASE_URL" --format=custom --no-owner --no-acl --file="$archive"
pg_restore --list "$archive" >/dev/null
printf 'Verified backup: %s\n' "$archive"
