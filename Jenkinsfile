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
                bat 'if not exist bin mkdir bin'
                bat 'javac -d bin src\\tasktracker\\*.java'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                bat "\"%SONAR_SCANNER_HOME%\\bin\\sonar-scanner.bat\" " +
                    "-D\"sonar.organization=glebosha2804-hub\" " +
                    "-D\"sonar.projectKey=glebosha2804-hub_Project_Tracker\" " +
                    "-D\"sonar.host.url=https://sonarcloud.io\" " +
                    "-D\"sonar.sources=src\" " +
                    "-D\"sonar.java.binaries=bin\" " +
                    "-D\"sonar.login=%SONAR_TOKEN%\""
            }
        }
    }
}
