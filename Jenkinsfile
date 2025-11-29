pipeline {
    agent any

    environment {
    SONAR_SCANNER_HOME = tool 'SonarScanner'
    SONAR_TOKEN = credentials('sonarcloud-token')
}

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

        stage('Tests & Coverage') {
            steps {
                dir('TaskTracker') {
                    bat '''
                    rem === подготовка папок для отчётов ===
                    if not exist reports mkdir reports
                    if not exist reports\\junit mkdir reports\\junit
                    if not exist reports\\jacoco mkdir reports\\jacoco

                    rem === запуск JUnit 5 через консоль раннер
                    rem     + подключение JaCoCo агентом ===
                    java ^
                      -javaagent:lib\\Jacoco\\org.jacoco.agent-0.8.11.jar=destfile=reports\\jacoco\\jacoco.exec ^
                      -jar lib\\Junit\\junit-platform-console-standalone-1.10.2.jar ^
                      --class-path bin ^
                      --scan-class-path ^
                      --reports-dir=reports\\junit
                    '''
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                bat '''
                "%SONAR_SCANNER_HOME%\\bin\\sonar-scanner.bat" ^
                  -D"sonar.login=%SONAR_TOKEN%"
                '''
            }
        }
    }
}
