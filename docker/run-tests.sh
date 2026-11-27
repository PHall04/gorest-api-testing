#!/bin/sh
# Runs the Cucumber suite with the given tag filter and copies reports to /reports.
set -u

echo "Running tests with tags: ${TAGS}"
mvn -B clean test -Dcucumber.filter.tags="${TAGS}" "$@"
status=$?

mkdir -p /reports
rm -rf /reports/cucumber-report.html /reports/surefire-reports
[ -f target/cucumber-report.html ] && cp target/cucumber-report.html /reports/
[ -d target/surefire-reports ] && cp -r target/surefire-reports /reports/
echo "Reports copied to /reports (exit code ${status})"

exit "${status}"
