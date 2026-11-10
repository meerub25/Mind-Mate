#!/usr/bin/env bash

echo "========================================================"
echo "  Antigravity Android App - APK Compiler & Build Tool"
echo "========================================================"
echo ""

if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
        echo "[INFO] Detected Android SDK at: $ANDROID_HOME"
    elif [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
        echo "[INFO] Detected Android SDK at: $ANDROID_HOME"
    fi
fi

chmod +x gradlew
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================================"
    echo "  [SUCCESS] APK Successfully Compiled!"
    echo "========================================================"
    echo "Output: app/build/outputs/apk/debug/app-debug.apk"
    echo "Install via: adb install -r app/build/outputs/apk/debug/app-debug.apk"
    echo "========================================================"
fi
