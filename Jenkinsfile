pipeline {
    agent any

    environment {
        // SonarScanner из Global Tool Configuration (Name = SonarScanner)
        SONAR_SCANNER_HOME = tool 'SonarScanner'
        // токен SonarCloud в Jenkins Credentials (ID = sonarcloud-token)
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
                // создаём папку bin внутри TaskTracker
                bat 'if not exist TaskTracker\\bin mkdir TaskTracker\\bin'
                // компилируем все исходники пакета tasktracker
                bat 'javac -d TaskTracker\\bin TaskTracker\\src\\tasktracker\\*.java'

                // собираем простой JAR из скомпилированных классов
                // JAR будет лежать в TaskTracker\\TaskTracker.jar
                bat 'jar cf TaskTracker\\TaskTracker.jar -C TaskTracker\\bin .'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                // запускаем SonarScanner под конфигурацией сервера SonarCloud
                withSonarQubeEnv('SonarCloud') {
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

        stage('Quality Gate') {
            steps {
                // ждём, пока SonarCloud закончит анализ и вернёт статус quality gate
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
