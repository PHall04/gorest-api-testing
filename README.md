# Running the tests in Docker

This project's Cucumber API test suite can be run inside a Docker container.
The container uses Java 21 and Maven to match CI, so the tests run the same
way regardless of what versions you have installed locally.

The tests run against the live [GoREST v3 API](https://gorest.co.in). Due to
this you will need your own GoREST access token.

## Contents

1. [Prerequisites](#prerequisites)
2. [Clone the repository](#1-clone-the-repository)
3. [Add your token](#2-add-your-token)
4. [Build the image](#3-build-the-image)
5. [Run the tests](#4-run-the-tests)
6. [Choose which tests run](#5-choose-which-tests-run)
7. [Read the reports](#6-read-the-reports)
8. [Troubleshooting](#troubleshooting)
9. [Cleaning up](#cleaning-up)
10. [Security notes](#security-notes)
11. [Reporting issues](#reporting-issues)

## Prerequisites

- [Git](https://git-scm.com/downloads)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/), installed
  and running (you can check this with `docker run hello-world`)
- A GoREST access token with the `users:read` and `users:write` scopes, created
  on your GoREST account

You do NOT need Java or Maven installed to run the tests through Docker.

## 1. Clone the repository

```bash
git clone https://github.com/PHall04/gorest-api-testing.git
cd gorest-api-testing
```

If the Docker setup has not been merged into `main` yet, switch to its branch:

```bash
git checkout feature/docker
```

## 2. Add your token

Create a file called `.env` in the project root containing:

```text
GOREST_TOKEN=your-token
```

- Do NOT put quotes around the token, otherwise `--env-file` will not read it
  correctly.
- `.env` is ignored by Git and excluded from the Docker image. It should never
  be committed.

## 3. Build the image

```bash
docker build -t gorest-tests .
```

The first build downloads all dependencies so it can take a few minutes. Later
builds are faster. The image holds a copy of the code from when it was built,
so you MUST rebuild after any change to the tests or `pom.xml`.

## 4. Run the tests

Use the command that matches the shell you are in. The PowerShell and Git Bash
commands are NOT interchangeable.

### PowerShell

```powershell
docker run --rm --env-file .env -v "${PWD}/reports:/reports" gorest-tests
```

### Git Bash

```bash
MSYS_NO_PATHCONV=1 docker run --rm --env-file .env -v "$(pwd -W)/reports:/reports" gorest-tests
```

`MSYS_NO_PATHCONV=1` and `pwd -W` stop Git Bash from rewriting the folder
paths. Without them the reports can end up in the wrong place.

### macOS or Linux

```bash
docker run --rm --env-file .env -v "$(pwd)/reports:/reports" gorest-tests
```

The token is passed in when the container starts. This ensures it is never
stored in the image.

## 5. Choose which tests run

By default every scenario runs except known defects (i.e. `not @known-defect`),
which matches the full CI run on `main`. To change this, add `-e TAGS="..."` to
the `docker run` command before `gorest-tests`:

| Run | Add to the command |
|---|---|
| Smoke tests only (matches CI on branches) | `-e TAGS="@smoke and not @known-defect"` |
| Known defects only | `-e TAGS="@known-defect"` |

The known defects run IS expected to fail. These scenarios reproduce open
defects in the GoREST API where it does not behave as its documentation says.

For example, in PowerShell:

```powershell
docker run --rm --env-file .env -e TAGS="@smoke and not @known-defect" -v "${PWD}/reports:/reports" gorest-tests
```

## 6. Read the reports

After each run the reports are copied to the `reports/` folder in the project
root:

- `reports/cucumber-report.html` - open this in a browser
- `reports/surefire-reports/` - JUnit XML and text results

Each run replaces the previous reports. The folder is also ignored by Git.

The container exits with Maven's exit code, so the command will fail if any
test fails. This makes it safe to use in scripts.

## Troubleshooting

| Problem | Likely cause | Fix |
|---|---|---|
| `GOREST_TOKEN is not set` | The token did not reach the container | Check `.env` is in the project root and contains `GOREST_TOKEN=...`. Also check the command includes `--env-file .env` |
| Lots of failures ending `but was <401>` | The token has expired or been revoked | Create a new token and update `.env` |
| Failures ending `but was <403>` | The token is missing a scope | Check it has `users:read` and `users:write` |
| Every test fails with `but was <502>` | The GoREST API is down | Wait and try again. You can check by opening `https://gorest.co.in/public/v3/users` in a browser |
| `MSYS_NO_PATHCONV=1` or `-W` not recognised | A Git Bash command was run in PowerShell | Use the PowerShell command instead |
| `reports/` is empty or in the wrong place | The path format does not match your shell | Use the command for your shell exactly as shown |
| Code changes are not picked up | The image still holds an older copy of the code | Rebuild the image (step 3) |
| `Cannot connect to the Docker daemon` | Docker Desktop is not running | Start Docker Desktop and wait for it to finish loading |

To check your token works separately from the tests:

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X POST \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' https://gorest.co.in/public/v3/users
```

A `422` means the token works, `401` means it has expired and `403` means it is
missing a scope. This sends an empty user which GoREST rejects, so nothing is
created.

## Cleaning up

`--rm` removes each container after it finishes but the image stays on your
machine. To remove it:

```bash
docker rmi gorest-tests
```

## Security notes

- The token is only present while a container is running. However, anyone with
  Docker access on your machine could read it from a running container, so do
  NOT leave containers running unattended.
- Use a token with only the scopes listed above and a short expiry.
  Additionally, revoke it once you no longer need it.

## Reporting issues

If something does not work as expected you can report it through GitHub
Issues. Please choose the type of issue that best matches your problem, as
each one is handled differently.

### Which type of issue is it?

| Type | What it covers | Example |
|---|---|---|
| README issue | The instructions in this README are wrong, unclear or missing a step | A command does not work as written, or a step is missing |
| Test suite issue | A test behaves incorrectly when the API is working as documented | A test fails even though the API response matches the GoREST documentation |
| GoREST API issue | The API itself does not behave as its documentation says | An endpoint returns a different status code than documented |

GoREST API issues should NOT be reported here, as I cannot fix the API
itself. Instead, raise these with GoREST directly. If you think a test should
be tagged as a known defect because of an API issue, that can be reported here
as a test suite issue.

### Before reporting

- Check the [Troubleshooting](#troubleshooting) section, as many common
  problems are covered there.
- Check the existing [issues](https://github.com/PHall04/gorest-api-testing/issues)
  to see if it has already been reported.
- Make sure Docker Desktop is running and your token is valid.

### How to report an issue

1. Go to the [Issues](https://github.com/PHall04/gorest-api-testing/issues)
   tab of this repository.
2. Select **New issue**.
3. Choose **README issue** or **Test suite issue**.
4. Fill in each section of the form that appears.
5. Select **Submit new issue**.

You will need a GitHub account to report an issue.

### Do NOT include your token

Never paste your GoREST token into an issue, including in screenshots, logs or
command output. Issues on this repository are public so anyone would be able
to see it.

If you accidentally include your token, revoke it on your GoREST account
straight away and create a new one. Editing or deleting the issue
afterwards is NOT enough, as the token may already have been seen.