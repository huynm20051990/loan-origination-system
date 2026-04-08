# Ralph Loop Instructions - Java Project

You are working in an autonomous loop. Each iteration:
1. Read current state
2. Implement ONE task only
3. Validate your work
4. Commit if successful

## Orientation

- Read AGENTS.md for project patterns.
- Read specs/* for specifications.
- Read specs/003-ai-chat-box/tasks.md for the plan.
- Read progress.txt for current progress.
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

## Quality Checks (Maven)

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
2. git add -A
3. git commit -m "feat: <description>"
4. git push

## Completion

All done: <promise> COMPLETE </promise>
Items remain: Exit normally (loop will spawn fresh instance).

## Critical Rules

1. ONE task per iteration.
2. SEARCH before implementing.
3. NO UnsupportedOperationException() stubs.
4. Javadoc on all public APIs.
5. ALL checks must pass before commit.

FULL IMPLEMENTATIONS ONLY.