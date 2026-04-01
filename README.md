# WalkOfInterest
Route Generator with interesting spots

## Требования
Перед установкой убедитесь, что на вашей системе установлены:
 - 
 - [Docker](https://docs.docker.com/get-docker/) (версия 20.10 или выше)
 - [Docker Compose](https://docs.docker.com/compose/install/) (обычно входит в состав Docker Desktop)
 - [Postgres]
 - [PostGIS]

## Установка
1. 
```bash
git clone https://github.com/artemalo/WalkOfInterest.git
cd WalkOfInterest
git clone https://github.com/artemalo/WalkOfInterest-backend.git
cd ..
docker compose up -d
```
2. Установите данные для базы данных и карт
 - Скачайте архив по ссылке: https://disk.yandex.ru/d/4IBh3J78BAYxfA
 - Распакуйте содержимое архива в папку WalkOfInterest (ту, куда вы клонировали репозиторий).
 Или в корневой папке WalkOfInterest/.. :
 ```bash
 unzip /путь/к/скачанному/архиву.zip
 ```
 3. Запустите контейнеры:
 ```bash
 docker compose up -d
 ```
 Флаг `-d` запускает контейнеры в фоновом режиме. Если вы хотите видеть логи, опустите этот флаг.