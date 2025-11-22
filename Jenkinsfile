pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube' // Name configured in Jenkins
        DOCKER_IMAGE = 'idl0828/demo-app'
    }

    tools {
        jdk 'jdk17'
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/iDL08/JenkinsPrac.git'
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
                    sh 'mvn sonar:sonar -Dsonar.projectKey=Demo-Application'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
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

        stage('Update K8s Manifest for GitOps') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                    sed -i "s|image: .*|image: $DOCKER_IMAGE:$BUILD_NUMBER|" k8s/deployment.yaml
                    git config --global user.email "jenkins@example.com"
                    git config --global user.name "Jenkins CI"
                    git add k8s/deployment.yaml
                    git commit -m "Update image to $DOCKER_IMAGE:$BUILD_NUMBER"
                    git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/iDL08/JenkinsPrac.git
                    git push origin main
                    '''
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
