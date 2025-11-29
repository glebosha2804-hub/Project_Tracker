pipeline {
    agent any

    environment {
        SONAR_SCANNER_HOME = tool 'SonarScanner'
        SONAR_TOKEN = credentials('sonarcloud-token')

    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('TaskTracker') {
                    bat """
                        if not exist bin mkdir bin
                        javac -cp ".;lib\\junit-platform-console-standalone-1.10.2.jar" -d bin src\\tasktracker\\*.java
                    """
                }
            }
        }

        stage('Tests with Coverage') {
            steps {
                dir('TaskTracker') {
                    bat """
                        cd bin
                        java ^
                          -javaagent:..\\lib\\org.jacoco.agent-0.8.11.jar=destfile=..\\jacoco.exec ^
                          -jar ..\\lib\\junit-platform-console-standalone-1.10.2.jar ^
                          -cp . ^
                          -scan-class-path ^
                          --include-classname=.*Test
                    """
                }
            }
        }

        stage('Generate JaCoCo XML') {
            steps {
                dir('TaskTracker') {
                    bat """
                        java -jar lib\\org.jacoco.cli-0.8.11.jar report jacoco.exec ^
                          --classfiles bin ^
                          --sourcefiles src ^
                          --xml jacoco.xml ^
                          --html jacoco-report
                    """
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                dir('TaskTracker') {
                    bat """
                        "%SONAR_SCANNER_HOME%\\bin\\sonar-scanner.bat" ^
                          -Dsonar.organization=glebosha2804-hub ^
                          -Dsonar.projectKey=glebosha2804-hub_Project_Tracker ^
                          -Dsonar.sources=src ^
                          -Dsonar.java.binaries=bin ^
                          -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info ^
                          -Dsonar.java.coveragePlugin=jacoco ^
                          -Dsonar.coverage.jacoco.xmlReportPaths=jacoco.xml ^
                          -Dsonar.token=%SONAR_TOKEN%
                    """
                }
            }
        }
    }
}
