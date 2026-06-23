# LoanPro

LoanPro is a fast Android loan calculator for mortgage, auto, credit, and personal loan scenarios.

Enter any three of loan amount, payment, term, and interest rate, then tap the missing value to solve it. Additional mortgage-oriented views support taxes, insurance, down payment, LTV, PMI, and bi-weekly calculations.

## Project

- Single-module Android app under `:app`.
- Package/application ID: `net.kaltner.LoanPro`.
- Current release: `1.0.0` (`versionCode` `10000`).
- License: Apache-2.0.
- No network permissions, analytics, ads, Play Services, Firebase, or crash-reporting SDKs.

## Build

```bash
ANDROID_HOME=$HOME/Android/Sdk ./gradlew --no-daemon :app:assembleDebug
ANDROID_HOME=$HOME/Android/Sdk ./gradlew --no-daemon testDebugUnitTest
```

## Release Notes

- Fastlane/F-Droid metadata lives under `fastlane/metadata/android/en-US/`.
- `play_store_icon.png` and `fastlane/metadata/android/en-US/images/icon.png` are the 512px listing icon.
- Real device screenshots still need to be captured before store/F-Droid submission.
- Original signing keys are not included. Add release signing locally when needed.
