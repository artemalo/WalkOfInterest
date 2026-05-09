# Руководство: Запуск бэкенда на чистом Ubuntu 24.04

## Шаг 1. Базовая подготовка и защита от зависаний (Swap)

Первым делом обновляем систему и создаем файл подкачки (Swap). Так как у сервера всего 2 ГБ оперативной памяти, без Swap сервер зависнет при запуске базы данных и маршрутизатора.

```bash
# Обновляем списки пакетов и саму систему
sudo apt update && sudo apt upgrade -y

# Создаем файл подкачки на 4 ГБ
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Делаем так, чтобы Swap работал и после перезагрузки сервера
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

## Шаг 2. Безопасность (Брандмауэр UFW)

**ВНИМАНИЕ:** Строка с портом 22 критически важна

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp    # Обязательно: доступ по SSH
sudo ufw allow 80/tcp
sudo ufw --force enable
```

## Шаг 3. Установка «Сердца» (`Docker`)

```bash
# Устанавливаем необходимые системные утилиты
sudo apt install apt-transport-https ca-certificates curl software-properties-common -y

# Добавляем официальный ключ безопасности Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Добавляем репозиторий Docker в систему
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Устанавливаем сам Docker и Docker Compose
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io docker-compose-plugin -y
```

## Шаг 4. Подготовка проекта и данных

Создаем папку для проекта, скачиваем код бэкенда и карту для навигатора GraphHopper

```bash
# Создаем рабочую директорию и переходим в нее
mkdir ~/app && cd ~/app

# Скачиваем код бэкенда
git clone https://github.com/artemalo/WalkOfInterest-backend.git

# Перед использованием wget команды
apt-get install wget

# Создаем папку для карт и скачиваем карту ЮФО
mkdir -p ~/app/data/graphhopper
cd ~/app/data/graphhopper
wget https://download.geofabrik.de/russia/south-fed-district-latest.osm.pbf

# Возвращаемся в главную папку проекта
cd ~/app
```

Конфиг для GraphHopper (*обязательно*)

```bash
graphhopper:
  datareader.file: /data/map.osm.pbf
  graph.location: /data/cache

  import.osm.ignored_highways: |
    motorway, trunk, busway,
    motorway_link, trunk_link

  profiles:
    - name: foot
      custom_model_files: [foot.json, foot_elevation.json]

  profiles_ch:
    - profile: foot

  graph.encoded_values: |
    foot_access, foot_average_speed, foot_priority, foot_road_access,
    hike_rating, average_slope,
    country, road_class, mtb_rating, road_environment, ferry_speed

  graph.elevation.provider: srtm

server:
  application_connectors:
  - type: http
    port: 8989
    max_request_header_size: 50k
  request_log:
      appenders: []
logging:
  appenders:
    - type: file
      time_zone: UTC
      current_log_filename: logs/graphhopper.log
      log_format: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
      archive: true
      archived_log_filename_pattern: ./logs/graphhopper-%d.log.gz
      archived_file_count: 30
      never_block: true
    - type: console
      time_zone: UTC
      log_format: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  loggers:
    "com.graphhopper.osm_warnings":
      level: DEBUG
      additive: false
      appenders:
        - type: file
          currentLogFilename: logs/osm_warnings.log
          archive: false
          logFormat: '[%level] %msg%n'
```

Переименовать файл (или везде использовать имя нужное .pbf)

```
mv ~/app/data/graphhopper/south-fed-district-latest.osm.pbf ~/app/data/graphhopper/map.osm.pbf## Шаг 5. Конфигурация (Секреты и Архитектура)
```

### 5.1 Файл переменных окружения (`.env`)

```bash
nano .env
```

```env
DB_PASSWORD=super_strong_password
JWT_SECRET=длинная_случайная_строка_без_пробелов
DOMAIN=<server-name>
```

### 5.2 Файл архитектуры (`docker-compose.yml`)

```bash
nano docker-compose.yml
```

```yaml
services:
  db:
    image: postgis/postgis:15-3.3
    container_name: walk-db
    ports:
      - "127.0.0.1:5432:5432"
    restart: always
    environment:
      POSTGRES_DB: walk
      POSTGRES_USER: walk
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    command: >
      postgres
      -c shared_buffers=128MB
      -c work_mem=4MB
      -c maintenance_work_mem=64MB
      -c effective_cache_size=512MB
    mem_limit: 512m
    volumes:
      - ./data/postgres:/var/lib/postgresql/data

  graphhopper:
    image: israelhikingmap/graphhopper:latest
    container_name: graphhopper
    restart: always
    mem_limit: 700m
    volumes:
      - ./data/graphhopper:/data
      - ./graphhopper.yml:/config.yml
    command: ["-c", "/config.yml"]
    environment:
      - JAVA_OPTS=-Xmx512m -Xms512m

  backend:
    build: ./WalkOfInterest-backend
    container_name: walk-api
    restart: always
    depends_on:
      - db
      - graphhopper
    environment:
      - DB_URL=jdbc:postgresql://db:5432/walk
      - DB_USERNAME=walk
      - DB_PASSWORD=${DB_PASSWORD}
      - GH_URL=http://graphhopper:8989
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=900000
      - JWT_REFRESH_EXPIRATION=2592000000
      - JAVA_OPTS="-Xms256m -Xmx512m"
    mem_limit: 700m

  nginx:
    image: nginx:alpine
    container_name: nginx
    restart: always
    mem_limit: 64m
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/conf:/etc/nginx/conf.d
      - ./certbot/conf:/etc/letsencrypt
      - ./certbot/www:/var/www/certbot
    depends_on:
      - backend
```

### 5.3 Файл архитектуры (`.dockerignore`)

```bash
nano .dockerignore
```

```dockerignore
target
.git
.idea
*.iml
```

## Шаг 6. Настройка Nginx

```bash
mkdir -p ~/app/nginx/conf && nano ~/app/nginx/conf/app.conf
```

*Вставить этот код:*

```nginx
server {
    listen 80;
    server_name <server-name>;

    location / {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Шаг 7. Запуск

```bash
sudo docker compose up -d
```

---

## Другое

### Полезные команды

```bash
# Посмотреть статус всех контейнеров
sudo docker compose ps

# Посмотреть логи бэкенда (чтобы убедиться, что Spring Boot запустился)
sudo docker compose logs -f backend

# Проверить потребление оперативной памяти контейнерами
sudo docker stats

# Полностью остановить проект
sudo docker compose down
```

---

### Папка ~/app

```bash
../app/
├── WalkOfInterest-backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── data/
│   ├── postgres/
│   └── graphhopper/
│       └── south-fed-district-latest.osm.pbf
├── nginx/
│   └── conf/
│       └── app.conf
├── certbot/
│   ├── conf/
│   └── www/
├── .env
├── docker-compose.yml
├── graphhopper.yml
└── .dockerignore
```

---

### Пересбор `docker`

#### Образы

Если был изменен `docker-compose.yml` и внутренние файлы (`.env`)

```bash
sudo docker compose down
sudo docker compose up -d
```

### Конкретный образ

Например, нужны новые изменения backend с git:

```bash
cd ~/app/WalkOfInterest-backend
git pull origin master
```

```bash
cd ~/app
sudo docker compose up -d --build backend
```
