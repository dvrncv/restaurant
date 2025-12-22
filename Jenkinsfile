pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK 17'
    }
    
    environment {
        JAVA_HOME = tool 'JDK 17'
        MAVEN_HOME = tool 'Maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Parent POM') {
            steps {
                script {
                    try {
                        if (isUnix()) {
                            sh 'mvn clean install -N -DskipTests'
                        } else {
                            bat 'mvnw.cmd clean install -N -DskipTests'
                        }
                    } catch (Exception e) {
                        echo "Failed to build parent POM: ${e.getMessage()}"
                        throw e
                    }
                }
            }
        }
        
        stage('Build Contracts') {
            parallel failFast: false, {
                stage('Build events-contract-restaurant') {
                    steps {
                        dir('events-contract-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'mvn clean install -DskipTests'
                                    } else {
                                        bat 'mvnw.cmd clean install -DskipTests'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build events-contract-restaurant: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build api-contract-restaurant') {
                    steps {
                        dir('api-contract-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'mvn clean install -DskipTests'
                                    } else {
                                        bat 'mvnw.cmd clean install -DskipTests'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build api-contract-restaurant: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Services') {
            parallel failFast: false, {
                stage('Build analytics-service-restaurant') {
                    steps {
                        dir('analytics-service-restaurant') {
                            script {
                                if (isUnix()) {
                                    sh 'mvn clean package -DskipTests'
                                } else {
                                    bat 'mvnw.cmd clean package -DskipTests'
                                }
                            }
                        }
                    }
                }
                stage('Build audit-service-restaurant') {
                    steps {
                        dir('audit-service-restaurant') {
                            script {
                                if (isUnix()) {
                                    sh 'mvn clean package -DskipTests'
                                } else {
                                    bat 'mvnw.cmd clean package -DskipTests'
                                }
                            }
                        }
                    }
                }
                stage('Build notification-service-restaurant') {
                    steps {
                        dir('notification-service-restaurant') {
                            script {
                                if (isUnix()) {
                                    sh 'mvn clean package -DskipTests'
                                } else {
                                    bat 'mvnw.cmd clean package -DskipTests'
                                }
                            }
                        }
                    }
                }
                stage('Build rest-restaurant') {
                    steps {
                        dir('rest-restaurant') {
                            script {
                                if (isUnix()) {
                                    sh 'mvn clean package -DskipTests'
                                } else {
                                    bat 'mvnw.cmd clean package -DskipTests'
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel failFast: false, {
                stage('Build analytics-service image') {
                    steps {
                        dir('analytics-service-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t analytics-service-restaurant:latest .'
                                    } else {
                                        bat 'docker build -t analytics-service-restaurant:latest .'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build analytics-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build audit-service image') {
                    steps {
                        dir('audit-service-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t audit-service-restaurant:latest .'
                                    } else {
                                        bat 'docker build -t audit-service-restaurant:latest .'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build audit-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build notification-service image') {
                    steps {
                        dir('notification-service-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t notification-service-restaurant:latest .'
                                    } else {
                                        bat 'docker build -t notification-service-restaurant:latest .'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build notification-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build rest-restaurant image') {
                    steps {
                        dir('rest-restaurant') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t rest-restaurant:latest .'
                                    } else {
                                        bat 'docker build -t rest-restaurant:latest .'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build rest-restaurant image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Build completed successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
        }
        failure {
            echo 'Build failed!'
        }
    }
}

