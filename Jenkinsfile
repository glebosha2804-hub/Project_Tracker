pipeline {
    agent any

    environment {
        // SonarScanner из Global Tool Configuration (Name = SonarScanner)
        SONAR_SCANNER_HOME = tool 'SonarScanner'
        // токен SonarCloud, сохранённый в Jenkins Credentials c ID = sonarcloud-token
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
                // создаём папку bin внутри проекта TaskTracker, если её нет
                bat 'if not exist TaskTracker\\bin mkdir TaskTracker\\bin'
                // компилируем все классы из пакета tasktracker
                bat 'javac -d TaskTracker\\bin TaskTracker\\src\\tasktracker\\*.java'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                bat "\"%SONAR_SCANNER_HOME%\\bin\\sonar-scanner.bat\" " +
                    "-D\"sonar.organization=glebosha2804-hub\" " +
                    "-D\"sonar.projectKey=glebosha2804-hub_Project_Tracker\" " +
                    "-D\"sonar.host.url=https://sonarcloud.io\" " +
                    "-D\"sonar.sources=TaskTracker/src\" " +
                    "-D\"sonar.java.binaries=TaskTracker/bin\" " +
                    "-D\"sonar.login=%SONAR_TOKEN%\""
            }
        }
    }
}
