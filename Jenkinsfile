pipeline {
    agent any

    environment {
        // Установка SonarScanner'а по имени из Manage Jenkins → Tools
        SONAR_SCANNER_HOME = tool 'SonarScanner'
        // Токен SonarCloud (ID учётки в Jenkins: sonarcloud-token)
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
                    bat '''
                    if not exist bin mkdir bin
                    javac -d bin src\\tasktracker\\*.java
                    '''
                }
            }
        }

        stage('Tests') {
            steps {
                dir('TaskTracker') {
                    bat '''
                    cd bin
                    java -jar ..\\lib\\junit-platform-console-standalone-1.10.2.jar ^
                      -cp . ^
                      -scan-class-path ^
                      --include-classname=.*Test
                    '''
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
                      -Dsonar.sources=TaskTracker/src ^
                      -Dsonar.java.binaries=TaskTracker/bin ^
                      -Dsonar.login=%SONAR_TOKEN%
                    """
                }
            }
        }
    }
}
