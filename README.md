# Hybrid Automation Framework

A production-grade Hybrid Test Automation Framework covering **UI** (Selenium WebDriver) and **API** (RestAssured) automation using **BDD Cucumber + TestNG**, designed for scalability, maintainability, and CI/CD readiness.

---

## Technology Stack

| Category        | Technology              | Version   |
|-----------------|-------------------------|-----------|
| Language        | Java                    | 11        |
| Build Tool      | Maven                   | 3.9+      |
| UI Automation   | Selenium WebDriver      | 4.18.1    |
| API Automation  | RestAssured             | 5.4.0     |
| BDD Framework   | Cucumber                | 7.15.0    |
| Test Framework  | TestNG                  | 7.9.0     |
| Reporting       | ExtentReports           | 5.1.1     |
| Logging         | Log4j2                  | 2.22.1    |
| Driver Mgmt     | WebDriverManager        | 5.7.0     |
| Config Mgmt     | OWNER Library           | 1.0.12    |
| DI Container    | PicoContainer           | 7.15.0    |
| JSON            | Jackson                 | 2.16.1    |
| Assertions      | AssertJ                 | 3.25.1    |
| CI/CD           | Jenkins (Declarative)   | -         |
| Notifications   | Slack                   | -         |
| UI Application  | SauceDemo               | https://www.saucedemo.com        |
| API Application | PetStore                | https://petstore.swagger.io/v2   |

---

## Framework Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     HYBRID AUTOMATION FRAMEWORK                      │
├──────────────────────────────┬──────────────────────────────────────┤
│         UI LAYER             │           API LAYER                   │
│  Selenium WebDriver          │  RestAssured                          │
│  SauceDemo                   │  PetStore API                         │
├──────────────────────────────┴──────────────────────────────────────┤
│                   BDD LAYER — Cucumber 7                             │
│         Feature Files → Step Definitions → Pages / Endpoints         │
├─────────────────────────────────────────────────────────────────────┤
│                 TEST ORCHESTRATION — TestNG                          │
│   TestRunner / SmokeRunner / RegressionRunner / APIRunner / UIRunner │
│        RetryAnalyzer (3 retries) + RetryTransformer (global)         │
├─────────────────────────────────────────────────────────────────────┤
│                    CONFIGURATION LAYER                               │
│     OWNER Library → qa.properties / stage.properties / prod.properties│
│         ConfigManager (Singleton) | -Denv=qa / stage / prod          │
├─────────────────────────────────────────────────────────────────────┤
│              INFRASTRUCTURE & CROSS-CUTTING CONCERNS                 │
│  DriverManager (ThreadLocal Singleton) | DriverFactory (Factory)     │
│  ExtentReportManager (Singleton)       | CucumberHooks (Lifecycle)   │
│  ScenarioContext (DI - PicoContainer)  | Log4j2 (Rolling File)       │
├─────────────────────────────────────────────────────────────────────┤
│                        CI/CD PIPELINE                                │
│   Jenkins Declarative Pipeline | Slack Notifications | HTML Reports  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns

### Singleton
Applied with thread-safe double-checked locking (`volatile` + `synchronized`):
- `DriverManager` — `ThreadLocal<WebDriver>`, one driver per thread
- `ConfigManager` — single `EnvironmentConfig` instance per JVM
- `ExtentReportManager` — single `ExtentReports` + `ThreadLocal<ExtentTest>` per thread
- `ApiClient` — single `RequestSpecification` / `ResponseSpecification` per run

### Factory
- `DriverFactory` — creates `ChromeDriver`, `FirefoxDriver`, or `EdgeDriver` from a browser name string. New browsers added with a new `case` — `DriverManager` never changes.

### Builder (Pet + RequestBuilder — same class)
Both builders live inside `Pet.java` as required:

```java
// Pet.Builder — builds the request payload
Pet pet = new Pet.Builder()
    .id(101L)
    .name("Buddy")
    .status(Pet.PetStatus.AVAILABLE)
    .photoUrls("https://example.com/buddy.jpg")
    .category("Dogs")
    .build();

// Pet.RequestBuilder — builds the RestAssured RequestSpecification
RequestSpecification spec = new Pet.RequestBuilder()
    .withBaseUri("https://petstore.swagger.io/v2")
    .withContentType("application/json")
    .withBody(pet)
    .withLogging()
    .build();
```

### Dependency Injection
PicoContainer injects `ScenarioContext` into every step class sharing a scenario — one shared instance, zero static variables:

```java
public class PetStoreSteps {
    private final ScenarioContext context;
    public PetStoreSteps(ScenarioContext context) {
        this.context = context; // injected by PicoContainer
    }
}
```

### Page Object Model
Every SauceDemo page is a dedicated class extending `BasePage`:

```
BasePage (abstract)
  ├── LoginPage
  ├── InventoryPage
  ├── CartPage
  └── CheckoutPage
```

---

## SOLID Principles

| Principle | Implementation |
|-----------|---------------|
| **Single Responsibility** | `DriverManager` manages lifecycle only. `DriverFactory` creates drivers only. Each page class handles one page only. |
| **Open / Closed** | Add a new browser in `DriverFactory` without modifying `DriverManager`. Add a new endpoint without touching `ApiClient`. |
| **Liskov Substitution** | All `BasePage` subclasses are substitutable wherever `BasePage` is used. |
| **Interface Segregation** | `ApiEndpoint` is a narrow marker interface. `EnvironmentConfig` exposes only config methods. |
| **Dependency Inversion** | Step definitions depend on `ScenarioContext` abstraction. `BasePage` depends on `DriverManager`, not `WebDriver` directly. |

---

## Project Structure

```
hybrid-automation-framework/
│
├── pom.xml                          # Maven dependencies & build config
├── testng.xml                       # TestNG suite configuration
├── Jenkinsfile                      # Declarative Jenkins pipeline
├── .gitignore
├── README.md
│
├── .github/workflows/ci.yml         # GitHub Actions CI
│
└── src/
    ├── main/java/com/framework/
    │   ├── api/
    │   │   ├── client/ApiClient.java            # Singleton RestAssured base spec
    │   │   ├── endpoints/
    │   │   │   ├── ApiEndpoint.java             # Marker interface
    │   │   │   └── PetEndpoints.java            # /pet CRUD operations
    │   │   └── models/Pet.java                  # Domain model + Builder + RequestBuilder
    │   │
    │   ├── config/
    │   │   ├── ConfigManager.java               # Singleton config provider
    │   │   └── EnvironmentConfig.java           # OWNER interface
    │   │
    │   ├── core/
    │   │   ├── driver/DriverManager.java        # ThreadLocal Singleton WebDriver
    │   │   └── factory/DriverFactory.java       # Browser factory
    │   │
    │   ├── listeners/
    │   │   ├── ExtentReportManager.java         # Singleton ExtentReports
    │   │   ├── RetryAnalyzer.java               # Max 3 retries
    │   │   ├── RetryTransformer.java            # Global retry application
    │   │   └── TestListener.java                # TestNG → ExtentReports bridge
    │   │
    │   ├── pages/
    │   │   ├── BasePage.java                    # Abstract POM base
    │   │   ├── LoginPage.java
    │   │   ├── InventoryPage.java
    │   │   ├── CartPage.java
    │   │   └── CheckoutPage.java
    │   │
    │   └── utils/
    │       ├── APIAssertions.java               # Fluent API assertions
    │       ├── JsonUtils.java                   # Jackson helper
    │       └── WaitUtils.java                   # Explicit wait utilities
    │
    ├── main/resources/
    │   ├── configs/
    │   │   ├── qa.properties
    │   │   ├── stage.properties
    │   │   └── prod.properties
    │   └── log4j/log4j2.xml
    │
    ├── test/java/com/framework/
    │   ├── api/tests/PetStoreSteps.java         # API step definitions
    │   ├── ui/tests/
    │   │   ├── LoginSteps.java                  # UI login steps
    │   │   └── CartSteps.java                   # UI cart + checkout steps
    │   ├── runners/
    │   │   ├── TestRunner.java                  # Master runner
    │   │   ├── SmokeRunner.java
    │   │   ├── RegressionRunner.java
    │   │   ├── APIRunner.java
    │   │   └── UIRunner.java
    │   └── utils/
    │       ├── CucumberHooks.java               # @Before/@After lifecycle
    │       └── ScenarioContext.java             # Shared DI state
    │
    └── test/resources/
        ├── features/
        │   ├── api/PetStore.feature
        │   └── ui/
        │       ├── Login.feature
        │       └── Cart.feature
        ├── extent.properties
        └── cucumber.properties
```

---

## Prerequisites

| Requirement   | Version | Notes                       |
|---------------|---------|-----------------------------|
| Java JDK      | 11+     | `JAVA_HOME` must be set     |
| Maven         | 3.9+    | `mvn` on PATH               |
| Google Chrome | Latest  | Driver managed automatically |
| Git           | Any     | For cloning                 |
| Jenkins       | 2.x LTS | For CI/CD pipeline          |

---

## Quick Start

```bash
# Clone
git clone https://github.com/Udhay/hybrid-automation-framework.git
cd hybrid-automation-framework

# Run smoke tests (default QA environment)
mvn test

# Run with specific environment and tags
mvn test -Denv=stage -Dtags=@Regression -Dbrowser=chrome -Dheadless=true

# API tests only
mvn test -Dtags=@API

# UI tests only
mvn test -Dtags=@UI -Dbrowser=firefox

# Full regression
mvn test -Dtags=@Regression -Denv=qa -Dheadless=true
```

---

## Configuration & Environments

| Environment | Properties File          | Headless | Use Case                        |
|-------------|--------------------------|----------|---------------------------------|
| `qa`        | `configs/qa.properties`  | false    | Development & local testing     |
| `stage`     | `configs/stage.properties`| true    | Pre-release validation          |
| `prod`      | `configs/prod.properties` | true    | Post-deployment smoke only      |

Override any property via `-D` flag — system properties take highest priority:

```bash
mvn test -Denv=prod -Dbrowser=firefox -Dheadless=true
```

---

## Running Tests

| Command                                | What Runs                    |
|----------------------------------------|------------------------------|
| `mvn test -Dtags=@Smoke`               | All smoke tests (UI + API)   |
| `mvn test -Dtags=@Regression`          | Full regression suite        |
| `mvn test -Dtags=@API`                 | API tests only               |
| `mvn test -Dtags=@UI`                  | UI tests only                |
| `mvn test -Dtags="@API and @Smoke"`    | API smoke only               |
| `mvn test -Dtest=SmokeRunner`          | Run by runner class          |

---

## API Response Validation

API responses are deserialized directly into the `Pet` POJO using `response.as(Pet.class)`, then validated field by field via getters — no `jsonPath` string navigation, no `ObjectMapper` calls in tests:

```java
// Deserialize response into Pet POJO
Pet responsePet = response.as(Pet.class);

// Validate each field using getters
assertThat(responsePet.getId()).isNotNull();
assertThat(responsePet.getName()).isEqualTo("Buddy");
assertThat(responsePet.getStatus()).isEqualToIgnoringCase("available");
assertThat(responsePet.getCategory().getName()).isEqualTo("Dogs");
assertThat(responsePet.getTags().get(0).getName()).isEqualTo("tag1");

// Deserialize list response
Pet[] pets = response.as(Pet[].class);
assertThat(pets).isNotEmpty();
assertThat(pets[0].getId()).isNotNull();
```

---

## Reporting

Reports generated after every run at `test-output/extent-reports/`:

- Dark theme HTML dashboard with pass/fail/skip breakdown
- System info (environment, browser, OS, Java version, author)
- Screenshots on failure embedded as Base64
- Per-scenario logs and stack traces

**Viewing in Jenkins:** Navigate to the build → click **Extent Test Report** or **Cucumber Report** in the sidebar.

---

## Logging

| Appender            | Output                             | Level  |
|---------------------|------------------------------------|--------|
| `ConsoleAppender`   | Colored terminal                   | INFO+  |
| `FileAppender`      | `test-output/logs/framework.log`   | DEBUG+ |
| `ErrorFileAppender` | `test-output/logs/errors.log`      | ERROR+ |

Logs rotate daily and at 20MB with up to 10 compressed archives retained.

---

## CI/CD — Jenkins Pipeline

### Setup
1. Install plugins: **Pipeline**, **HTML Publisher**, **Slack Notification**, **JUnit**
2. Configure global tools: `Maven_3.9`, `JDK_11`
3. Add Slack credential: `slack-webhook-url` (Secret Text)

### Pipeline Stages
```
Checkout → Build Info → Compile → Execute Tests → Publish Reports → Archive Artifacts
```

### Build Parameters
| Parameter   | Options                          |
|-------------|----------------------------------|
| ENVIRONMENT | qa / stage / prod                |
| BROWSER     | chrome / firefox / edge          |
| TAGS        | @Smoke / @Regression / @API / @UI|
| HEADLESS    | true / false                     |

### Slack Notification
Triggered on **Success**, **Failure**, and **Unstable** with build details, environment, duration, and direct report links.

---

## Retry Mechanism

Failed tests retry automatically up to **3 times** — no annotation needed on individual tests:

- `RetryAnalyzer` — implements `IRetryAnalyzer`, tracks retry count per method
- `RetryTransformer` — implements `IAnnotationTransformer`, applies `RetryAnalyzer` globally via `testng.xml`

---

## Parallel Execution

Parallel execution at two levels:

```xml
<!-- testng.xml -->
<suite parallel="methods" thread-count="2">
```

```java
// Runners
@DataProvider(parallel = true)
public Object[][] scenarios() { return super.scenarios(); }
```

Thread safety guaranteed by:
- `ThreadLocal<WebDriver>` — each thread gets its own driver
- `ThreadLocal<ExtentTest>` — each thread gets its own report node
- `ScenarioContext` — fresh instance per scenario via PicoContainer

---

## Author

**Udhay** — SDET Lead  
GitHub: [github.com/Udhay](https://github.com/Udhay)
