# Restaurant Microservices Project

Проект микросервисной архитектуры ресторана с использованием Spring Boot, gRPC, RabbitMQ, Prometheus и Grafana.

## Схема портов

Все используемые порты в проекте:

| Сервис | Порт | Описание |
|--------|------|----------|
| **Demo REST API** | **8090** | Основной REST API сервис |
| Analytics Service HTTP | 8081 | HTTP порт для метрик и health checks |
| Analytics Service gRPC | 9090 | gRPC порт для коммуникации |
| Audit Service | 8082 | Сервис аудита событий |
| Notification Service | 8083 | Сервис уведомлений |
| RabbitMQ | 5672 | Порт для AMQP протокола |
| RabbitMQ Management | 15672 | Web-интерфейс управления |
| Zipkin | 9411 | Сервис распределенной трассировки |
| Prometheus | 9091 | Метрики и мониторинг |
| Grafana | 3000 | Визуализация метрик |

## Инструкция по запуску проекта

### 1. Клонирование проекта

Клонируем проект:
```bash
git clone https://github.com/dvrncv/restaurant.git
```
Или используем zip-архив с флешки.

### 2. Сборка проекта

Переходим в корневую директорию проекта и выполняем сборку:
```bash
.\mvnw.cmd clean package -DskipTests
```

### 3. Запуск инфраструктуры и микросервисов

Логинимся в Docker и переходим в папку проекта, затем запускаем все сервисы:
```bash
docker-compose up -d
```

### 4. Показ метрик

#### 4.1. Prometheus метрики

Откройте браузер:
```
http://localhost:9091
```

**Примеры запросов:**

В поисковой строке введите метрику, например:
- `jvm_memory_used_bytes` - использование памяти JVM
- `http_server_requests_seconds_count` - количество HTTP запросов
- `process_cpu_usage` - использование CPU

Нажмите **Execute** для просмотра данных.

Перейдите на вкладку **Graph** для визуализации временных рядов.

**Популярные запросы:**

```promql
# Общее использование памяти всеми сервисами
sum(jvm_memory_used_bytes)

# Количество HTTP запросов к REST API
http_server_requests_seconds_count{application="rest-restaurant"}

# Использование CPU аналитическим сервисом
process_cpu_usage{application="analytics-service-restaurant"}
```

#### 4.2. Grafana дашборды

Откройте браузер:
```
http://localhost:3000
```

**Вход в систему:**
- Username: `admin`
- Password: `admin`

При первом входе система попросит изменить пароль (можно пропустить).

**Настройка источника данных Prometheus:**

1. Нажмите **Configuration** (иконка шестеренки) → **Data Sources**
2. Нажмите **Add data source**
3. Выберите **Prometheus**
4. В поле **URL** введите: `http://prometheus:9090`
5. Нажмите **Save & Test**
6. Должно появиться сообщение: **Data source is working**

**Создание простого дашборда:**

1. Нажмите **+** → **Create Dashboard**
2. Нажмите **Add visualization**
3. Выберите источник данных: **Prometheus**
4. В поле запроса введите: `jvm_memory_used_bytes`
5. Нажмите **Run query**
6. Нажмите **Apply** для сохранения панели

#### 4.3. Actuator метрики (прямой доступ)

Метрики доступны напрямую через Actuator endpoints:

**Prometheus метрики:**
- rest-restaurant: `http://localhost:8090/actuator/prometheus`
- analytics-service-restaurant: `http://localhost:8081/actuator/prometheus`
- audit-service-restaurant: `http://localhost:8082/actuator/prometheus`
- notification-service-restaurant: `http://localhost:8083/actuator/prometheus`

**Health checks:**
- rest-restaurant: `http://localhost:8090/actuator/health`
- analytics-service-restaurant: `http://localhost:8081/actuator/health`
- audit-service-restaurant: `http://localhost:8082/actuator/health`
- notification-service-restaurant: `http://localhost:8083/actuator/health`

**Другие доступные endpoints:**
- Info: `http://localhost:8090/actuator/info`
- Metrics: `http://localhost:8090/actuator/metrics`
- Все endpoints: `http://localhost:8090/actuator`





