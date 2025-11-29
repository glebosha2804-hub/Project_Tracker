pipeline {
    agent any   // Run the pipeline on any available Jenkins agent

    environment {
        // Load the SonarScanner tool configured in Jenkins (Manage Jenkins → Tools)
        SONAR_SCANNER_HOME = tool 'SonarScanner'
    }

    stages {

        stage('Checkout') {
            steps {
                // Pull the latest code from the GitHub repository
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('TaskTracker') {
                    // Compile all Java files and output .class files into the "bin" folder
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
                    // Run all JUnit tests using the JUnit Console Launcher
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
                // Use the stored SonarCloud token for authentication
                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONAR_TOKEN')]) {
                    dir('TaskTracker') {
                        // Run SonarScanner to analyze code quality
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
