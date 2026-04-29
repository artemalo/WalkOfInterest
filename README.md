# WalkOfInterest
Route Generator with interesting spots

## Требования
Перед установкой убедитесь, что на вашей системе установлены:
 - 
 - [Docker](https://docs.docker.com/get-docker/) (версия 20.10 или выше)
 - [Docker Compose](https://docs.docker.com/compose/install/) (обычно входит в состав Docker Desktop)
 - [Postgres](https://www.postgresql.org/) + PostGIS (в момент установки Postgres включить расширение)

## Установка
1. 
```bash
git clone https://github.com/artemalo/WalkOfInterest.git
cd WalkOfInterest
git clone https://github.com/artemalo/WalkOfInterest-backend.git
cd ..
```
2. Установите данные для базы данных и карт
 - Скачайте архив по ссылке: ...
 - Распакуйте содержимое архива в папку WalkOfInterest (ту, куда вы клонировали репозиторий).
 Или в корневой папке WalkOfInterest/.. :
 ```bash
 unzip /путь/к/скачанному/архиву.zip
 ```
 2. ИЛИ ->
  - В папке WalkOfInterest/.. создать папку data
  - Перейти ../data/
  - Поместить walk.sql
  - Создать папку graphhopper
  - Перейти ../data/graphhopper/
  - Поместить файл .osm.pbf (https://download.geofabrik.de/russia/south-fed-district.html)
  - - Преиминовав в map.osm.pbf
 3. Запустите контейнеры:
 ```bash
 docker compose up -d
 ```
 Флаг `-d` запускает контейнеры в фоновом режиме. Если вы хотите видеть логи, опустите этот флаг.