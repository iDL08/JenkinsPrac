
pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube' // Name configured in Jenkins
        DOCKER_IMAGE = 'your-dockerhub-username/demo-app'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-username/demo.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$BUILD_NUMBER .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$BUILD_NUMBER'
                }
            }
        }

        stage('Deploy via Argo CD') {
            steps {
                sh '''
                kubectl set image deployment/demo-app demo-app=$DOCKER_IMAGE:$BUILD_NUMBER -n default
                kubectl rollout status deployment/demo-app -n default
                '''
            }
        }
    }
}
