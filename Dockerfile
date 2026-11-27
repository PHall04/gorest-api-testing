# Matches CI (actions/setup-java: temurin 21)
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# Resolve dependencies first so they are cached between builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy test sources and compile them (no tests run at build time, no token needed)
COPY src ./src
RUN mvn -B -q test-compile

COPY docker/run-tests.sh /usr/local/bin/run-tests.sh
RUN sed -i 's/\r$//' /usr/local/bin/run-tests.sh && chmod +x /usr/local/bin/run-tests.sh

# Default tag filter; override with: docker run -e TAGS="..." ...
# GOREST_TOKEN is intentionally NOT set here - pass it at runtime with -e GOREST_TOKEN
ENV TAGS="not @known-defect"

ENTRYPOINT ["/usr/local/bin/run-tests.sh"]
