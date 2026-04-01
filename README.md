# WalkOfInterest
Route Generator with interesting spots

## Требования
Перед установкой убедитесь, что на вашей системе установлены:
 - [Docker](https://docs.docker.com/get-docker/) (версия 20.10 или выше)
 - [Docker Compose](https://docs.docker.com/compose/install/) (обычно входит в состав Docker Desktop)
 - [Postgres]
 - [PostGIS]

## Установка
```bash
git clone https://github.com/artemalo/WalkOfInterest.git
cd WalkOfInterest
git clone https://github.com/artemalo/WalkOfInterest-backend.git
cd ..
docker compose up -d
```