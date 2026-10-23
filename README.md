# OpenCode Droid

Android client for [OpenCode](https://github.com/anomalyco/opencode) — the open source AI coding agent.

OpenCode Droid connects to a running OpenCode server (`opencode serve` / `opencode web`) and provides a mobile, Claude-like chat experience for sessions, permissions, files, diffs, and terminals.

> This project is not built by the OpenCode team and is not affiliated with them.

## Status

Early scaffolding. See [PLAN.md](PLAN.md).

## Build

GitHub Actions builds the debug APK on every push to `main`. See `.github/workflows/android.yml`.

Local build:

```bash
./gradlew :app:assembleDebug
```

## Connect to a server

1. Run `OPENCODE_SERVER_PASSWORD=... opencode serve --hostname 0.0.0.0 --port 4096` on your machine.
2. In the app, enter the server URL and credentials.
