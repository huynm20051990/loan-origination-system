# Ralph Loop Instructions - Java Project

You are working in an autonomous loop. Each iteration:
1. Read current state
2. Implement ONE task only
3. Validate your work
4. Commit if successful
5. STOP — do not proceed to a second task regardless of remaining work

## Orientation

- Read scripts/ralph/AGENTS.md for project patterns.
- Read specs/* for specifications.
- Read specs/003-ai-chat-box/tasks.md for the plan.
- Read scripts/ralph/progress.txt for current progress.
- Verify you’re on a feature branch.

## Task Selection

Load specs/003-ai-chat-box/tasks.md and select the ONE highest-priority incomplete item.

## Before Implementing

CRITICAL: Search before implementing.
Do NOT assume code doesn’t exist.

## Implementation Rules

- Follow existing patterns and Java conventions.
- Write Javadoc for all public classes and methods.
- Use Optional instead of returning null.
- Prefer records for DTOs (Java 16+).
- FULL implementations only — NO throw new UnsupportedOperationException().
- Follow the existing build system (Maven or Gradle).

## Quality Checks (Maven)

```bash
# Compile
mvn compile -q

# Tests
mvn test

# Checkstyle/SpotBugs (if configured)
mvn verify -DskipTests
```

## Quality Checks (Gradle)

```bash
# Compile
./gradlew compileJava

# Tests
./gradlew test

# Static analysis (if configured)
./gradlew check
```

If ANY check fails, fix and re-run.

## Commit Sequence

After ALL checks pass:
1. Update specs/003-ai-chat-box/tasks.md mark as complete [x].
2. Append to scripts/ralph/progress.txt:
   ```
   [YYYY-MM-DD HH:MM] <task-id>: <one-line summary of what was done>
   ```
3. git add -A
4. git commit -m "feat: <description>"
5. git push

## Completion

- All tasks done: output exactly `<promise>COMPLETE</promise>` and stop.
- Items remain: output a one-line status summary and STOP immediately. Do NOT start a second task. The loop will spawn a fresh instance for the next task.

## Critical Rules

1. ONE task per iteration.
2. SEARCH before implementing.
3. NO UnsupportedOperationException() stubs.
4. Javadoc on all public APIs.
5. ALL checks must pass before commit.

FULL IMPLEMENTATIONS ONLY.