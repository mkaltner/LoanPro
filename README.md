# LoanPro

Recovered modernized Android project for `net.kaltner.LoanPro`.

## Current State

- Single-module Android app under `:app`.
- Java source comes from the archived `LoanProLib` source, flattened into package `net.kaltner.LoanPro` to match the 2023 APK/AAB.
- UI resources are reconstructed from the 2023 `1.3.5` APK/AAB where available.
- Version is set to `13` / `1.3.5`.

## Notes

- This is a recovery candidate, not yet a polished rebuild of the original Android Studio project.
- Old Google LVL licensing and the historical `LoanProLib` module split were intentionally not restored in this first pass.
- Original signing keys are not included. Add release signing locally when needed.
