# Jenkins Setup

## Запуск Jenkins в Docker

Для запуска Jenkins выполните:

```bash
cd jenkins
docker-compose up -d
```

Jenkins будет доступен по адресу: http://localhost:8085

## Первый запуск

1. Откройте http://localhost:8080
2. Получите начальный пароль:
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
3. Установите рекомендуемые плагины
4. Создайте администратора

## Настройка Jenkins для сборки проекта

1. Установите необходимые плагины:
   - Maven Integration
   - Pipeline
   - Docker Pipeline
   - Git

2. Настройте инструменты:
   - **JDK**: Установите JDK 17
   - **Maven**: Установите Maven (или используйте Maven Wrapper из проекта)

3. Создайте Pipeline Job:
   - New Item → Pipeline
   - В разделе Pipeline выберите "Pipeline script from SCM"
   - Укажите репозиторий Git
   - Script Path: `Jenkinsfile`

## Использование Jenkinsfile

Jenkinsfile находится в корне проекта и автоматически:
- Собирает родительский POM
- Собирает контракты (events-contract-restaurant, api-contract-restaurant)
- Собирает все сервисы параллельно
- Собирает Docker образы для всех сервисов

