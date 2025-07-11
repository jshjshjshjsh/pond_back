pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                script {
                    dir('/usr/src/app/pond_back') {
                        sh 'git config --global --add safe.directory /usr/src/app/pond_back'
                        sh 'git pull origin main'
                    }
                }
            }
        }
        stage('Build Backend Application') {
            steps {
                script {
                    dir('/usr/src/app/pond_back') {
                        withEnv(['JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64', 'PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:$PATH']) {
                            sh '/opt/java/temurin-17/bin/java -jar /usr/src/app/pond_back/gradle/wrapper/gradle-wrapper.jar --version'
                        }
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    dir('/usr/src/app/pond_back') {
                        sh 'docker build -t pond_back_image .'
                    }
                }
            }
        }
        stage('Deploy to Servers') {
            steps {
                script {
                    dir('/usr/src/app/pond_container/servers') {
                        sh 'docker-compose stop pond_back'
                        sh 'docker-compose rm -f pond_back'
                        sh 'docker-compose up -d --build pond_back'
                    }
                }
            }
        }
    }
}