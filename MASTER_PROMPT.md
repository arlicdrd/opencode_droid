# Master Prompt — OpenCode Droid

You are an expert Android engineer. Implement and extend the OpenCode Droid app in this repository.

## Context
- App: native Android client for OpenCode server (remote). UI should be Claude-like.
- Stack: Kotlin, Jetpack Compose (Material 3), Ktor client, kotlinx.serialization, Coroutines/Flow, Navigation Compose.
- Min/Target/Compile SDK: 26/37/37.
- Server: OpenCode HTTP API, base `/api`, Basic auth (username default `opencode`, password = `OPENCODE_SERVER_PASSWORD`).
- Contract source: https://github.com/anomalyco/opencode `packages/protocol/openapi.json` (139 endpoints).
- Repo builds on GitHub Actions (`.github/workflows/android.yml`).

## Rules (no mistakes)
1. Never invent endpoint shapes. Derive from `openapi.json`. Prefer `GET /api/info` for health.
2. All network DTOs live in `data/model` and `data/network`; map to domain. Use `ignoreUnknownKeys = true`.
3. Never swallow errors silently; surface via `StateFlow<String?> error`.
4. No blocking on main thread; all IO in `viewModelScope`/`Dispatchers.IO`.
5. Credentials must never be logged. Use `Credentials.basic` and redact in logs.
6. SSE `/api/event` must reconnect with exponential backoff; never crash on disconnect.
7. Keep Compose state hoisted in ViewModels; screens are stateless.
8. CI must pass (`./gradlew :app:assembleDebug`) before any PR.

## Current scaffold
- Package: `com.opencode.droid`
- Files: `MainActivity`, `SessionViewModel`, `OpenCodeApiClient`, screens `ChatScreen`, `SettingsScreen`, `WorkspaceScreen`.
- API client currently covers: info, session list/messages/prompt, fs list. Extend per phases.

## Phases to implement (in order)
1. **Connection**: Settings screen with URL + username + password; test via `/api/info`; persist in DataStore.
2. **Sessions**: list/create/delete/select sessions; session list screen.
3. **Chat**: prompt + message list + SSE streaming; markdown + code blocks; interrupt/compact/revert.
4. **Permissions**: list pending permission requests; allow/deny cards.
5. **Files/VCS**: file viewer, diff viewer, branch list.
6. **Terminals**: PTY create + WebSocket stream + input.
7. **Polish**: theming, caching, tests, release signing.

## Implementation guidance
- Add modules only when needed; start in `:app`.
- For markdown, use a Compose markdown library or custom; code blocks with copy.
- For SSE, use Ktor `ktor-client-sse` or manual `EventSource` over OkHttp; map bus events to a `SharedFlow`.
- For Basic auth, store username+password; default username `opencode`.
- OpenCode prompt body: `{ "parts": [{"type":"text","text":...}], "agent": "build" }`.
- Message list endpoint: `GET /api/session/{id}/message`.
- Events: `GET /api/event` (SSE). First event `server.connected`.

## Build
Local: `./gradlew :app:assembleDebug`
CI: GitHub Actions (`.github/workflows/android.yml`).

## Output expectations
- Working APK from CI.
- No compile warnings treated as errors? Keep clean.
- Update PLAN.md checkboxes as phases complete.
