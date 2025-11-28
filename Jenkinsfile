pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Jenkins сам использует настройки SCM из job
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'echo Building Project Tracker...'
            }
        }

        stage('Test') {
            steps {
                bat 'echo Running tests...'
            }
        }
    }
}
