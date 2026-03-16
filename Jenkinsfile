// ============================================================
// Jenkinsfile - Declarative Pipeline
// Framework: Hybrid Automation (Selenium + RestAssured + Cucumber)
// Features:
//   - Multi-environment support (QA / Stage / Prod)
//   - Parallel UI + API execution
//   - ExtentReports HTML publishing
//   - Cucumber report publishing
//   - Slack notifications on pass/fail/completion
// ============================================================

pipeline {

    agent any  // Uses Jenkins Built-in Agent

    // ── Parameters (configurable per build) ─────────────────
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'stage', 'prod'],
            description: 'Target environment for test execution'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser for UI test execution'
        )
        choice(
            name: 'TAGS',
            choices: ['@Smoke', '@Regression', '@UI', '@API', '@API_Pet', '@Login', '@Cart'],
            description: 'Cucumber tag filter for test execution'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (recommended for CI)'
        )
        booleanParam(
            name: 'SEND_SLACK_NOTIFICATION',
            defaultValue: true,
            description: 'Send Slack notification after pipeline execution'
        )
    }

    // ── Environment Variables ────────────────────────────────
    environment {
        MAVEN_OPTS          = '-Xmx2048m -XX:MaxPermSize=512m'
        REPORT_DIR          = 'test-output/extent-reports'
        CUCUMBER_REPORT_DIR = 'test-output/cucumber-reports'
        SLACK_CHANNEL       = '#automationlearningchannel'
        SLACK_CREDENTIALS   ='OAUTHBOTTokenAutomationlearning'
        // SLACK_CREDENTIALS stored in Jenkins Credentials store as 'slack-webhook-url'
    }

    // ── Build Tools ──────────────────────────────────────────
    tools {
        maven 'Maven_3.9'   // Matches Jenkins Global Tool Configuration name
        jdk   'JDK_11'      // Matches Jenkins Global Tool Configuration name
    }

    // ── Pipeline Stages ─────────────────────────────────────
    stages {

        // 1. Checkout source code
        stage('Checkout') {
            steps {
                echo "═══════════════════════════════════════"
                echo " Checking out source code..."
                echo " Branch: ${env.GIT_BRANCH}"
                echo " Commit: ${env.GIT_COMMIT}"
                echo "═══════════════════════════════════════"
                checkout scm
            }
        }

        // 2. Print build configuration
        stage('Build Info') {
            steps {
                echo """
                ╔══════════════════════════════════════╗
                  HYBRID AUTOMATION FRAMEWORK
                  Build: #${env.BUILD_NUMBER}
                  Environment : ${params.ENVIRONMENT}
                  Browser     : ${params.BROWSER}
                  Tags        : ${params.TAGS}
                  Headless    : ${params.HEADLESS}
                  Agent       : ${env.NODE_NAME}
                ╚══════════════════════════════════════╝
                """
            }
        }

        // 3. Compile
        stage('Compile') {
            steps {
                echo "Compiling project..."
                sh 'mvn clean compile test-compile -q'
            }
        }

        // 4. Execute Tests
        stage('Execute Tests') {
            steps {
                echo "Executing tests: env=[${params.ENVIRONMENT}] tags=[${params.TAGS}]"
                sh """
                    mvn test \
                        -Denv=${params.ENVIRONMENT} \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Dcucumber.filter.tags="${params.TAGS}" \
                        -Dmaven.test.failure.ignore=true \
                        --no-transfer-progress
                """
            }
        }

        // 5. Publish Reports
        stage('Publish Reports') {
            steps {
                echo "Publishing test reports..."

                // Publish ExtentReports HTML
                publishHTML(target: [
                    allowMissing         : false,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : "${env.REPORT_DIR}",
                    reportFiles          : '*.html',
                    reportName           : 'Extent Test Report',
                    reportTitles         : 'Hybrid Automation Report'
                ])

                // Publish Cucumber HTML Report
                publishHTML(target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : "${env.CUCUMBER_REPORT_DIR}",
                    reportFiles          : 'cucumber.html',
                    reportName           : 'Cucumber Report',
                    reportTitles         : 'Cucumber BDD Report'
                ])
            }
        }

        // 6. Archive Artifacts
        stage('Archive Artifacts') {
            steps {
                echo "Archiving test artifacts..."
                archiveArtifacts artifacts: """
                    test-output/**/*.html,
                    test-output/**/*.json,
                    test-output/**/*.xml,
                    test-output/screenshots/**/*.png,
                    test-output/logs/**/*.log
                """, allowEmptyArchive: true, fingerprint: true
            }
        }
    }

    // ── Post Actions (always run) ────────────────────────────
    post {

        always {
            echo "Pipeline execution complete. Publishing JUnit results..."
            junit(
                testResults: 'test-output/cucumber-reports/*.xml',
                allowEmptyResults: true
            )
        }

        success {
            echo "✔ Pipeline PASSED"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('SUCCESS')
                }
            }
        }

        failure {
            echo "✘ Pipeline FAILED"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('FAILURE')
                }
            }
        }

        unstable {
            echo "⚠ Pipeline UNSTABLE (some tests failed)"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('UNSTABLE')
                }
            }
        }

        cleanup {
            echo "Cleaning workspace..."
            cleanWs(
                cleanWhenSuccess: false,
                cleanWhenFailure: false,
                cleanWhenUnstable: false,
                deleteDirs: true,
                patterns: [[pattern: 'test-output/screenshots/**', type: 'EXCLUDE']]
            )
        }
    }
}

// ── Slack Notification Helper ────────────────────────────────
def sendSlackNotification(String buildStatus) {

    def colorMap = [
        'SUCCESS'  : '#36a64f',   // Green
        'FAILURE'  : '#cc0000',   // Red
        'UNSTABLE' : '#ffaa00',   // Orange
        'ABORTED'  : '#808080'    // Grey
    ]

    def emojiMap = [
        'SUCCESS'  : '✅',
        'FAILURE'  : '❌',
        'UNSTABLE' : '⚠️',
        'ABORTED'  : '🚫'
    ]

    def color  = colorMap.get(buildStatus, '#808080')
    def emoji  = emojiMap.get(buildStatus, '❓')
    def jobUrl = "${env.BUILD_URL}"

    def message = """
${emoji} *Hybrid Automation Framework - Build ${buildStatus}*

*Job:*         ${env.JOB_NAME}
*Build:*       #${env.BUILD_NUMBER}
*Environment:* ${params.ENVIRONMENT?.toUpperCase()}
*Browser:*     ${params.BROWSER}
*Tags:*        ${params.TAGS}
*Duration:*    ${currentBuild.durationString}
*Status:*      ${buildStatus}

📊 *Reports:* <${jobUrl}Extent_20Test_20Report|Extent Report> | <${jobUrl}Cucumber_20Report|Cucumber Report>
🔗 *Build URL:* ${jobUrl}
    """.stripIndent()

    slackSend(
        channel    : env.SLACK_CHANNEL,
        color      : color,
        message    : message,
        tokenCredentialId: env.SLACK_CREDENTIALS_ID
    )

    echo "Slack notification sent to: ${env.SLACK_CHANNEL} | Status: ${buildStatus}"
}
