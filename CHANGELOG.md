# Changelog

## [Unreleased]

## [88.5-1.16.5] - 2026-07-26

- Added github actions for CI
- Fixed MockK-based tests (e.g. `PromotionManagerIntegrationTest`) failing on JDK 25 with `class redefinition failed: attempted to change the schema` by disabling Kover instrumentation for the test tasks (Kover's coverage agent conflicts with MockK inline mocking)
- Merged change to fix file names appearing weird when zooming
- Updated build to target the latest JetBrains IDEs (IntelliJ Platform 2026.2) on JetBrains Runtime 25
- Raised minimum supported IDE to 2026.2 (build 262) and switched the build to the Java 25 toolchain
- Fixed the sticker overlay to compile against 2026.x after JCEF moved into a separate bundled plugin
- Bumped build tooling and libraries (IntelliJ Platform Gradle Plugin 2.18.1, Gradle 9.6.1, Kover 0.9.8, commons-io 2.22.0, Javassist 3.32.0-GA, AssertJ 3.27.7, MockK 1.14.11)
- Build fixes and upstream merges
- Renamed plugin to Doki Theme v2
- Fixed to work with new IDE versions
- Lowest supported version is now 2025.1
- Compiles to the 2025.1 build
- Lowest supported version is now 2024.3
- Compiles to the 2024.3 build

## [88.5-1.16.3]

- Build fixes and upstream merges
- Renamed plugin to Doki Theme v2
- Fixed to work with new IDE versions

## [88.5-1.16.0] [2025.1 Build Support]

- Lowest supported version is now 2025.1
- Compiles to the 2025.1 build

## [88.5-1.15.0] [2024.3 Build Support]

- Lowest supported version is now 2024.3
- Compiles to the 2024.3 build

[Unreleased]: https://github.com/kit-nya/doki-theme-jetbrains//compare/88.5-1.16.5...HEAD
[88.5-1.16.5]: https://github.com/kit-nya/doki-theme-jetbrains//compare/88.5-1.16.3...88.5-1.16.5
[88.5-1.16.3]: https://github.com/kit-nya/doki-theme-jetbrains//compare/88.5-1.16.0...88.5-1.16.3
[88.5-1.16.0]: https://github.com/kit-nya/doki-theme-jetbrains//compare/88.5-1.15.0...88.5-1.16.0
[88.5-1.15.0]: https://github.com/kit-nya/doki-theme-jetbrains//commits/88.5-1.15.0
