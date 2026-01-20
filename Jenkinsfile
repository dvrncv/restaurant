pipeline {
    agent any
    
    environment {
        COMPOSE_PROJECT_NAME = "restaurant"
        CONTAINERS = "rabbitmq zipkin prometheus grafana analytics-service audit-service notification-service demo-rest"
    }
    
    triggers {
        pollSCM('H/2 * * * *')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Maven Projects') {
            steps {
                script {
                    // Сначала собираем контракты (install в локальный Maven репозиторий)
                    sh '''
                        cd events-contract-restaurant && ./mvnw clean install -DskipTests
                        cd ../api-contract-restaurant && ./mvnw clean install -DskipTests
                    '''
                    // Затем собираем все сервисы (package создает JAR файлы)
                    sh '''
                        cd analytics-service-restaurant && ./mvnw clean package -DskipTests
                        cd ../audit-service-restaurant && ./mvnw clean package -DskipTests
                        cd ../notification-service-restaurant && ./mvnw clean package -DskipTests
                        cd ../rest-restaurant && ./mvnw clean package -DskipTests
                    '''
                }
            }
        }
        
        stage('Cleanup') {
            steps {
                sh '''
                    docker compose down -v --remove-orphans ${CONTAINERS} || true
                    docker rm -f ${CONTAINERS} || true
                '''
            }
        }
        
        stage('Compose Build & Up') {
            steps {
                sh '''
                    set -e
                    docker compose up -d --build ${CONTAINERS}
                '''
            }
        }
    }
    
    post {
        always {
            sh 'docker compose ps || true'
        }
        success {
            echo 'Deployment completed successfully!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}

