# Plan: Port OpenCode to Android

## Goal

Build a native Android app (`opencode_droid`) that acts as a remote client to an OpenCode server, with a Claude-like chat UI, building on GitHub Actions.

## Non-goals (v1)

- Running the OpenCode server *inside* Android. The app is a client only.
- Full parity with the desktop TUI on day one. Ship chat + sessions + permissions first.

## Architecture

- **Language/UI:** Kotlin + Jetpack Compose (Material 3).
- **Min/Target SDK:** minSdk 26, targetSdk 37, compileSdk 37.
- **Networking:** Ktor Client (OkHttp engine) + kotlinx.serialization; SSE for `/api/event`.
- **DI:** Hilt.
- **State:** ViewModel + StateFlow, unidirectional data flow.
- **Modules:** `:app`, `:core:network`, `:core:model`, `:core:data`, `:feature:chat`, `:feature:sessions`, `:feature:settings`.
- **Server contract:** OpenCode HTTP API (`packages/protocol/openapi.json`), base path `/api`, Basic auth via `OPENCODE_SERVER_PASSWORD`.

## Phases

### Phase 0 — Repo & CI (done scaffold)
- Gradle project, Compose, Hilt, Ktor.
- GitHub Actions workflow building debug APK + uploading artifact.
- README, PLAN, MASTER_PROMPT.

### Phase 1 — Connection & health
- Settings screen: base URL, username, password (stored in DataStore, encrypted where possible).
- `GET /api/info` health check.
- Connection test + error states.

### Phase 2 — Sessions
- `GET /api/session` list, `POST /api/session` create, `GET /api/session/{id}`, `DELETE`.
- Session list screen with search/sort.

### Phase 3 — Chat (core)
- `POST /api/session/{id}/prompt`, `GET /api/session/{id}/message` list.
- SSE `/api/event` subscription mapped to UI state.
- Message bubbles: user/assistant, markdown, code blocks, tool calls, thinking.
- Streaming append + scroll-to-bottom.
- Interrupt (`POST /api/session/{id}/interrupt`), compact, revert.

### Phase 4 — Permissions & inbox
- `GET /api/session/{id}/permission`, reply endpoints.
- Permission request cards with Allow/Deny.
- Inbox steer/queue.

### Phase 5 — Files & VCS
- `GET /api/fs/read`, `list`, `find`.
- `GET /api/vcs`, `status`, `diff`, `branch`.
- File viewer + diff viewer.

### Phase 6 — Terminals
- PTY create/connect WebSocket, shell endpoints.
- Terminal screen (read-only first, then input).

### Phase 7 — Polish
- Theming (dark/light), offline cache, error handling, tests, release signing.

## Risks / "no mistakes" guardrails

- Do not guess endpoint shapes; always derive from `openapi.json` or generated client.
- Keep network DTOs in `:core:model` only; map to domain models.
- All long-running work off main thread; use coroutines.
- Handle SSE reconnect with backoff; never block UI.
- Basic auth credentials never logged.
- CI must pass before merging any PR.

## Definition of done (v1)

- App connects to a server, lists/creates sessions, sends prompts, streams replies, renders markdown/code, handles permissions, builds in CI.
