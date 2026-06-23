# LoanPro

Recovered modernized Android project for `net.kaltner.LoanPro`.

## Current State

- Single-module Android app under `:app`.
- Java source comes from the archived `LoanProLib` source, flattened into package `net.kaltner.LoanPro` to match the 2023 APK/AAB.
- UI resources are reconstructed from the 2023 `1.3.5` APK/AAB where available.
- Version is set to `13` / `1.3.5`.
- The app is treated as a free app. Paid/Lite wrapper projects and Google LVL licensing are not part of this rebuilt source tree.

## Notes

- This is a recovery candidate, not yet a polished rebuild of the original Android Studio project.
- The old `LoanPro`, `LoanProLite`, `LoanProLib`, and `LicenseVerificationLibrary` Eclipse/Ant project split was intentionally collapsed into a single modern app module.
- Original signing keys are not included. Add release signing locally when needed.
