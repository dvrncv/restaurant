# Быстрый старт для зачета

## Минимальная последовательность действий

### 1. Сборка всех проектов (ОБЯЗАТЕЛЬНО!)
```bash
# Windows PowerShell
.\mvnw.cmd clean package -DskipTests

# Linux/Mac
./mvnw clean package -DskipTests
```

### 2. Запуск всех сервисов
```bash
docker-compose up -d
```

### 3. Запуск Jenkins
```bash
cd jenkins
docker-compose up -d
cd ..
```

### 4. Настройка Jenkins (первый раз)
1. Откройте: http://localhost:8085
2. Получите пароль: `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`
3. Установите плагины: Maven Integration, Pipeline, Docker Pipeline, Git

### 5. Создание Pipeline Job
1. New Item → Pipeline
2. Pipeline script from SCM → Git
3. Repository URL: ваш репозиторий
4. Script Path: `Jenkinsfile`
5. Build Now

### 6. Проверка метрик
- Prometheus: http://localhost:9091
- Grafana: http://localhost:3000 (admin/admin)
- Actuator: http://localhost:8080/actuator/prometheus

## Проверка статуса
```bash
docker-compose ps
```

## Просмотр логов
```bash
docker-compose logs -f
```

