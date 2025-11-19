# JUnit5 Flaky Test Detection

A Maven-based project demonstrating automatic detection and reporting of flaky tests using JUnit5 extensions and test execution listeners.

## Key Concepts

### Java Service Loader

The [Service Loader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) mechanism enables automatic discovery and loading of service implementations at runtime. This project uses it to auto-register JUnit5 extensions without explicit configuration.

**How it works:**
- Service providers are declared in `META-INF/services/` directory
- File name matches the fully qualified interface name
- File content lists the implementation class(es)

**Resources:**
- [Oracle Tutorial: Creating Extensible Applications](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html)
- [Baeldung: Java Service Provider Interface](https://www.baeldung.com/java-spi)

### JUnit5 Extensions

[JUnit5 Extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) provide a powerful way to extend test behavior. This project implements:

- **TestWatcher**: Monitors test execution lifecycle (`testSuccessful()`, `testFailed()`)
- **TestExecutionListener**: Listens to test plan execution events

**Key Extension Points:**
- `org.junit.jupiter.api.extension.Extension` - Base interface for all extensions
- `org.junit.jupiter.api.extension.TestWatcher` - Observes test method execution
- Auto-detection enabled via `junit.jupiter.extensions.autodetection.enabled=true`

**Resources:**
- [JUnit5 User Guide: Extensions](https://junit.org/junit5/docs/current/user-guide/#extensions)
- [Baeldung: Guide to JUnit5 Extensions](https://www.baeldung.com/junit-5-extensions)
- [JUnit5 Extension Model](https://junit.org/junit5/docs/current/user-guide/#extensions-model)

### JUnit Platform Launcher

The [JUnit Platform Launcher API](https://junit.org/junit5/docs/current/user-guide/#launcher-api) provides programmatic access to test execution. The `TestExecutionListener` interface allows monitoring test plan execution across multiple test runs (including retries).

**Key Features:**
- `org.junit.platform.launcher.TestExecutionListener` - Listens to test plan lifecycle
- `testPlanExecutionFinished()` - Triggered after each test plan completes
- Persists across Maven Surefire retry executions

**Resources:**
- [JUnit5 User Guide: Launcher API](https://junit.org/junit5/docs/current/user-guide/#launcher-api)
- [JUnit Platform Launcher JavaDoc](https://junit.org/junit5/docs/current/api/org.junit.platform.launcher/org/junit/platform/launcher/package-summary.html)
- [Baeldung: JUnit5 Test Execution Listeners](https://www.baeldung.com/junit-testexecutionlistener)

## How It Works

1. **Service Loader** auto-discovers `MyTestWatcher` via `META-INF/services/` configuration
2. **TestWatcher** tracks individual test executions and their outcomes
3. **TestExecutionListener** aggregates results after each test plan execution
4. **Maven Surefire** retries failed tests (configured with `rerunFailingTestsCount=2`)
5. **Flaky tests** are identified as tests that fail initially but pass on retry
6. **JSON report** is generated in each module's `target/` directory
