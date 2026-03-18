// ============================================================
// Jenkinsfile - Declarative Pipeline (Windows Compatible)
// ============================================================

pipeline {

    agent any

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'stage', 'prod'],
            description: 'Target environment'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser for UI tests'
        )
        choice(
            name: 'TAGS',
            choices: ['@Smoke', '@Regression', '@UI', '@API', '@API_Pet', '@Login', '@Cart'],
            description: 'Cucumber tag filter'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode'
        )
        booleanParam(
            name: 'SEND_SLACK_NOTIFICATION',
            defaultValue: true,
            description: 'Send Slack notification after execution'
        )
    }

    environment {
        MAVEN_OPTS          = '-Xmx2048m'
        REPORT_DIR          = 'test-output/extent-reports'
        CUCUMBER_REPORT_DIR = 'test-output/cucumber-reports'
        SLACK_CHANNEL       = '#automationlearningchannel'
    }

    tools {
        maven 'MAVEN_HOME'
        jdk   'JAVA_HOME'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                echo "Branch: ${env.GIT_BRANCH}"
                echo "Commit: ${env.GIT_COMMIT}"
                checkout scm
            }
        }

        stage('Build Info') {
            steps {
                echo """
                ╔══════════════════════════════════════╗
                  HYBRID AUTOMATION FRAMEWORK
                  Build       : #${env.BUILD_NUMBER}
                  Environment : ${params.ENVIRONMENT}
                  Browser     : ${params.BROWSER}
                  Tags        : ${params.TAGS}
                  Headless    : ${params.HEADLESS}
                  Agent       : ${env.NODE_NAME}
                ╚══════════════════════════════════════╝
                """
            }
        }

        stage('Compile') {
            steps {
                echo "Compiling project..."
                bat 'mvn clean compile test-compile -q'
            }
        }

        stage('Execute Tests') {
            steps {
                echo "Executing tests: env=[${params.ENVIRONMENT}] tags=[${params.TAGS}]"
                bat """
                    mvn test ^
                        -Denv=${params.ENVIRONMENT} ^
                        -Dbrowser=${params.BROWSER} ^
                        -Dheadless=${params.HEADLESS} ^
                        -Dcucumber.filter.tags="${params.TAGS}" ^
                        --no-transfer-progress
                """
            }
        }

        stage('Publish Reports') {
            steps {
                echo "Publishing test reports..."
                publishHTML(target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : "${env.REPORT_DIR}",
                    reportFiles          : '*.html',
                    reportName           : 'Extent Test Report'
                ])
                publishHTML(target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : "${env.CUCUMBER_REPORT_DIR}",
                    reportFiles          : 'cucumber.html',
                    reportName           : 'Cucumber Report'
                ])
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "Archiving artifacts..."
                archiveArtifacts artifacts: 'test-output/**/*',
                                 allowEmptyArchive: true,
                                 fingerprint: true
            }
        }
    }

    post {

        always {
            echo "Pipeline complete. Publishing JUnit results..."
            junit(
                testResults: 'test-output/cucumber-reports/*.xml',
                allowEmptyResults: true
            )
        }

        success {
            echo "Pipeline PASSED"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('SUCCESS')
                }
            }
        }

        failure {
            echo "Pipeline FAILED"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('FAILURE')
                }
            }
        }

        unstable {
            echo "Pipeline UNSTABLE"
            script {
                if (params.SEND_SLACK_NOTIFICATION) {
                    sendSlackNotification('UNSTABLE')
                }
            }
        }
    }
}

// ── Slack Notification ───────────────────────────────────────
def sendSlackNotification(String buildStatus) {

    def colorMap = [
        'SUCCESS'  : '#36a64f',
        'FAILURE'  : '#cc0000',
        'UNSTABLE' : '#ffaa00'
    ]

    def emojiMap = [
        'SUCCESS'  : '✅',
        'FAILURE'  : '❌',
        'UNSTABLE' : '⚠️'
    ]

    def color   = colorMap.get(buildStatus, '#808080')
    def emoji   = emojiMap.get(buildStatus, '❓')
    def jobUrl  = "${env.BUILD_URL}"

    def message = """
${emoji} *Hybrid Automation Framework - Build ${buildStatus}*
*Job:*         ${env.JOB_NAME}
*Build:*       #${env.BUILD_NUMBER}
*Environment:* ${params.ENVIRONMENT?.toUpperCase()}
*Browser:*     ${params.BROWSER}
*Tags:*        ${params.TAGS}
*Duration:*    ${currentBuild.durationString}
*Status:*      ${buildStatus}
🔗 *Build URL:* ${jobUrl}
    """.stripIndent()

    slackSend(
        channel             : env.SLACK_CHANNEL,
        color               : color,
        message             : message,
        tokenCredentialId   : 'OAUTHBOTTokenAutomationlearning'
    )

    echo "Slack notification sent to: ${env.SLACK_CHANNEL} | Status: ${buildStatus}"
}