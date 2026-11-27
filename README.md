# Running the tests in Docker

The test suite can run inside a Docker container, so every run uses the same
Java and Maven versions as CI (Temurin 21), whatever is installed locally.

## Prerequisites

- Docker Desktop, running
- A GoREST access token with the `users:read` and `users:write` scopes

The token is never stored in the image. It is passed in when the container starts.

## 1. Build the image

Run from the project root:

```bash
docker build -t gorest-tests .
```

Rebuild after changing any test code or `pom.xml`. Dependencies are cached,
so rebuilds after code-only changes are quicker.

## 2. Set your token

PowerShell:

```powershell
$env:GOREST_TOKEN = "your-token"
```

Git Bash:

```bash
export GOREST_TOKEN="your-token"
```

This only lasts for the current terminal session.

## 3. Run the tests

By default the container runs every scenario except known defects
(`not @known-defect`), matching the full CI run.

PowerShell:

```powershell
docker run --rm -e GOREST_TOKEN -v "${PWD}/reports:/reports" gorest-tests
```

Git Bash:

```bash
MSYS_NO_PATHCONV=1 docker run --rm -e GOREST_TOKEN -v "$(pwd -W)/reports:/reports" gorest-tests
```

`-e GOREST_TOKEN` with no value passes the token through from your session,
so it never appears in the command itself.

### Choosing which tests run

Override the tag filter with `TAGS`:

| Run | Add to the command |
|---|---|
| Smoke tests only (matches CI on branches) | `-e TAGS="@smoke and not @known-defect"` |
| Known defects only | `-e TAGS="@known-defect"` |
| Every scenario | `-e TAGS="@smoke or not @smoke"` |

For example, in PowerShell:

```powershell
docker run --rm -e GOREST_TOKEN -e TAGS="@smoke and not @known-defect" -v "${PWD}/reports:/reports" gorest-tests
```

## 4. Read the reports

After each run, reports are copied to the `reports/` folder in the project root:

- `reports/cucumber-report.html`: open in a browser
- `reports/surefire-reports/`: JUnit XML results

Each run replaces the previous reports. The folder is ignored by Git.

The container exits with Maven's exit code, so a failing test fails the command.

## Troubleshooting

| Problem | Fix |
|---|---|
| `GOREST_TOKEN is not set` | Set the token in the same terminal before running (step 2) |
| `reports/` is empty, or appears in the wrong place in Git Bash | Use the Git Bash command exactly as shown, including `MSYS_NO_PATHCONV=1` |
| Code changes aren't picked up | Rebuild the image (step 1) |

## Cleaning up

`--rm` removes each container after it finishes, but the image stays.
To remove it:

```bash
docker rmi gorest-tests
```