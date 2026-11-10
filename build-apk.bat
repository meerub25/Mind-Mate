@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo   Mindmate Wellness - Student Mental Health Platform
echo   Android APK Compiler & Build Tool
echo ========================================================
echo.

:: 1. Check & Configure Java Home
if not defined JAVA_HOME (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        echo [INFO] Using Android Studio JBR as JAVA_HOME: !JAVA_HOME!
    ) else (
        echo [INFO] Using default system Java.
    )
)

:: 2. Check & Configure Android SDK
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
        set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
        echo [INFO] Detected Android SDK at: !ANDROID_HOME!
    ) else (
        echo [WARNING] Android SDK not detected at %LOCALAPPDATA%\Android\Sdk
        echo [INFO] Please open Android Studio once to complete SDK component setup.
    )
)

echo.
echo [BUILD] Compiling Debug APK for Mindmate Wellness...
echo.

call gradlew.bat assembleDebug --stacktrace

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================================
    echo   [SUCCESS] Mindmate Wellness APK Successfully Built!
    echo ========================================================
    echo Output APK location:
    echo %~dp0app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on your connected device/emulator:
    echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
    echo ========================================================
) else (
    echo.
    echo ========================================================
    echo   [NOTICE] Build failed or missing SDK dependencies.
    echo ========================================================
    echo To compile via Android Studio GUI:
    echo 1. Open Android Studio ("C:\Program Files\Android\Android Studio\bin\studio64.exe")
    echo 2. Open this folder: %~dp0
    echo 3. Click: Build -> Build Bundle(s) / APK(s) -> Build APK(s)
    echo ========================================================
)

endlocal
