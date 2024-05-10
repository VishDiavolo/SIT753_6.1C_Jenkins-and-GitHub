pipeline {
    agent any  
    stages {
        stage('Build') {
            steps {
                script {
                    sleep time: 20, unit: 'SECONDS'
                }
                echo 'Building the application...'
                echo "sh 'mvn clean package'"  
            }
        }
        stage('Unit and Integration Tests') {
            steps {
                echo 'Running Integration tests...'
                echo "Running unit tests"
                echo "sh 'mvn test'"              
            }
            post {
                success {
                    email('Unit and Integration Tests', 'Success')
                }
                failure {
                    email('Unit and Integration Tests', 'Failure')
                }
            }
        }
        stage('Code Analysis') {
            steps {
                echo 'Analyzing code...'
                echo "sh 'mvn sonar:sonar'"       
        }
        }
        stage('Security Scan') {
            steps {
                echo 'Performing security scan...'
                echo "sh sh 'zap-cli start --start-options '-config api.disablekey=true' -d'"
                
            }
            post {
                success {
                    email('Security Scan', 'Success')
                }
                failure {
                    email('Security Scan', 'Failure')
                }
            }
           
        }

        stage('Deploy to Staging') {
            steps {
                echo 'Deploying to staging...'
                echo "sh 'scp target/myapp.war user@STAGING_SERVER:/path/to/deploy"
                
            }
        }

        stage('Integration Tests on Staging') {
            steps {
                echo 'Running integration tests on staging...'
                echo "sh 'mvn integration-test'"
            }
        }

        stage('Deploy to Production') {
            steps {
                echo 'Deploying to production...'
                echo "sh 'aws deploy ...'"
            }
        }
    }

    post {
        success {
            echo 'Pipeline ran successfully!'
            emailext subject: 'Pipeline Status - Success',
                      body: 'The pipeline ran successfully.',
                      to: 'svishuddha@gmail.com'
        }
        failure {
            echo 'Pipeline failed!'
            emailext subject: 'Pipeline Status - Failure',
                      body: 'The pipeline failed. Please check the logs for details.',
                      to: 'svishuddha@gmail.com'
        }
    }
}
def email(stageName, status) {
    emailext subject: "Pipeline Status - $status: $stageName",
              body: "The $stageName stage ${status.toLowerCase()}. Please see attached logs for details.",
              to: 'svishuddha@gmail.com',
              replyTo: 'svishuddha@gmail.com',
              attachLog: true,
              attachmentsPattern: '**/*.log'
}
