pipeline {
    agent any

    environment {
        // Имя SonarScanner из Manage Jenkins -> Tools
        SONAR_SCANNER_HOME = tool 'SonarScanner'
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
                    javac -cp ".;lib\\junit-platform-console-standalone-1.10.2.jar" -d bin src\\tasktracker\\*.java
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
                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONAR_TOKEN')]) {
                    dir('TaskTracker') {
                        bat '''
                        "%SONAR_SCANNER_HOME%\\bin\\sonar-scanner.bat" ^
                          -Dsonar.organization=glebosha2804-hub ^
                          -Dsonar.projectKey=glebosha2804-hub_Project_Tracker ^
                          -Dsonar.sources=src ^
                          -Dsonar.java.binaries=bin ^
                          -Dsonar.token=%SONAR_TOKEN%
                        '''
                    }
                }
            }
        }
    }
}
