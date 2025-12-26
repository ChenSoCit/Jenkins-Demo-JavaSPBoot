pipeline {
    agent any

    tools {
        maven 'MAVEN'
        jdk 'JDK17'
    }

    environment{
        APP_NAME = 'training-jv'
        IMAGE_NAME = 'training-jv:latest'
        CONTAINER_NAME = 'training-jv-container'
    }

    stages {

        /*
           CHECKOUT SOURCE
         */
        stage('Checkout Source') {
            steps {
                echo 'Checkout source code from Github'
                checkout scm
            }
        }

        /* 
           BUILD & TEST 
         */
        stage('Build & Unit Test') {
            steps {
                echo 'Build project and run unit tests'
                sh 'mvn clean test'
            }
            post {
                 always{

                    echo 'Publishing Code Coverage Report'
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                    )

                    echo 'HTML report for code coverage'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
                 }
            }
        }

        /*
           BUILD JAR
        */
        stage('Package JAR') {
            steps {
                echo 'Package application into Jar'
                sh 'mvn package -DskipTests'
            }
        }

        /*
           BUILD DOCKER IMAGE
        */
        stage('Build Docker Image') {
            steps {
                echo 'Build Docker image'
                sh ''' docker build -t ${IMAGE_NAME} . '''
            }
        }

        /*
           DEPLOY TO DEV ENVIRONMENT
        */
        stage('Deloy Docker Container') {
            steps {
                echo 'Deloy application using Docker'

                sh ''' 
                docker stop ${CONTAINER_NAME} || true
                docker rm ${CONTAINER_NAME} || true

                docker run -d \
                    --name ${CONTAINER_NAME} \
                    -p 8088:8080 \
                    ${IMAGE_NAME}
                '''
            }
        }

    }

    post {
        success {
            echo 'CI/CD PIPELINE SUCCESS'
        }

        failure {
            echo 'CI/CD PIPELINE FAILED'
        }
    }
}
