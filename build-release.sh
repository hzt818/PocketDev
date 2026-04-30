#!/bin/bash

# PocketDev Release Build Script
# Usage: ./build-release.sh [keystore_path] [keystore_password] [key_alias] [key_password]

set -e

cd "$(dirname "$0")"

# Default values - update these for your environment
KEYSTORE_PATH="${1:-}"
KEYSTORE_PASSWORD="${2:-}"
KEY_ALIAS="${3:-}"
KEY_PASSWORD="${4:-}"

# Detect if we have signing credentials
HAS_SIGNING=false
if [[ -n "$KEYSTORE_PATH" && -n "$KEYSTORE_PASSWORD" && -n "$KEY_ALIAS" && -n "$KEY_PASSWORD" ]]; then
    HAS_SIGNING=true
fi

echo "========================================"
echo "  PocketDev Release Build"
echo "========================================"
echo ""

# Clean previous builds
echo "[1/4] Cleaning previous builds..."
./gradlew clean

# Run lint
echo ""
echo "[2/4] Running lint checks..."
./gradlew lint

# Build release APK
echo ""
echo "[3/4] Building release APK..."
if [ "$HAS_SIGNING" = true ]; then
    echo "Using signing configuration from arguments"
    ./gradlew assembleRelease \
        -PkeystorePath="$KEYSTORE_PATH" \
        -PkeystorePassword="$KEYSTORE_PASSWORD" \
        -PkeyAlias="$KEY_ALIAS" \
        -PkeyPassword="$KEY_PASSWORD" \
        -Psigning=true
else
    echo "No signing credentials provided - building unsigned APK"
    ./gradlew assembleRelease
fi

# Verify output
echo ""
echo "[4/4] Verifying output..."
OUTPUT_APK="app/build/outputs/apk/release/app-release-unsigned.apk"
if [ -f "$OUTPUT_APK" ]; then
    APK_SIZE=$(du -h "$OUTPUT_APK" | cut -f1)
    echo ""
    echo "========================================"
    echo "  Build Successful!"
    echo "========================================"
    echo "APK: $OUTPUT_APK"
    echo "Size: $APK_SIZE"
    echo ""
    echo "Next steps:"
    echo "  - Sign the APK: jarsigner -keystore <keystore> $OUTPUT_APK <key_alias>"
    echo "  - Or align: zipalign -v 4 $OUTPUT_APK app-release.apk"
else
    echo "ERROR: Release APK not found at $OUTPUT_APK"
    exit 1
fi
