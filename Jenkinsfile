pipeline { 
agent any  // Runs on any available Jenkins agent 
environment { 
MAVEN_HOME = tool 'Maven'  // Using Jenkins' Maven tool 
} 
    stages { 
        stage('Checkout Code') { 
            steps { 
                git branch: 'main', url: 'https://github.com/your
repo/simple-java-maven-project.git' 
            } 
        } 
 
        stage('Build') { 
            steps { 
                sh "${MAVEN_HOME}/bin/mvn clean package" 
            } 
        } 
 
        stage('Test') { 
            steps { 
                sh "${MAVEN_HOME}/bin/mvn test" 
            } 
        } 
 
        stage('Deploy') { 
            steps { 
                echo "Deploying the application..." 
            } 
        } 
    } 
 
    post { 
        success { 
            echo "Pipeline completed successfully!" 
        } 
        failure { 
            echo "Pipeline failed!" 
        } 
    } 
} 
