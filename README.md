# Облачное хранилище файлов

Многопользовательское файловое облако. Пользователи сервиса могут
использовать его для загрузки и хранения файлов. Источником вдохновения
для проекта является Google Drive.

## Что нужно знать

-   Java (коллекции, ООП)
-   Maven/Gradle
-   Backend: Spring Boot, Spring Security, Spring Sessions
-   REST, Swagger, Upload файлов
-   Cookies, сессии
-   SQL, Spring Data JPA, миграции
-   NoSQL: Redis, S3 (MinIO)
-   Тесты: JUnit, интеграционные тесты, Testcontainers
-   Docker, Docker Compose

## Мотивация проекта

-   Использование возможностей Spring Boot
-   Практика с Docker и Docker Compose
-   Разработка структуры базы данных
-   Работа с NoSQL хранилищами (Redis, MinIO)
-   Интеграция по REST с frontend (React)

## Функционал приложения

### Работа с пользователями

-   Регистрация
-   Авторизация
-   Logout

### Работа с файлами и папками

-   Загрузка (upload) файлов и папок
-   Создание пустой папки
-   Удаление
-   Переименование и перемещение
-   Скачивание файлов и папок

## REST API

-   Архитектурный стиль: RPC (авторизация/регистрация), REST (остальное)
-   Базовый путь: `/api`
-   Авторизация: сессии (Spring Security + Spring Session + Redis)
-   Формат: JSON (кроме upload/download файлов)

## Основные эндпоинты

### Регистрация

`POST /auth/sign-up`

### Авторизация

`POST /auth/sign-in`

### Логаут

`POST /auth/sign-out`

### Текущий пользователь

`GET /user/me`

### Работа с файлами

-   `GET /resource?path=$path` --- получить информацию
-   `DELETE /resource?path=$path` --- удалить
-   `GET /resource/download?path=$path` --- скачать
-   `GET /resource/move?from=$from&to=$to` --- переместить/переименовать
-   `GET /resource/search?query=$query` --- поиск
-   `POST /resource?path=$path` --- загрузка

### Работа с папками

-   `GET /directory?path=$path` --- содержимое папки
-   `POST /directory?path=$path` --- создать пустую папку

## Сессии и безопасность

-   Spring Security для авторизации
-   Spring Session + Redis для хранения сессий

## Swagger

-   Документация API через Swagger UI

## SQL база данных

-   PostgreSQL (структура Users + индексы)
-   Миграции (Flyway или Liquibase)

## Хранилище файлов S3

-   MinIO (локально)
-   Minio Java SDK

## Frontend

-  ([репозиторий](https://github.com/zhukovsd/cloud-storage-frontend))

## Тесты

-   Интеграционные тесты (JUnit + Testcontainers)
-   Тесты пользователей и файлов
-   Проверка прав доступа


## Запуск проекта

Клонируйте [репозиторий](https://github.com/zhukovsd/cloud-storage-frontend)) фронтенда
В файле public/config.js поменяйте функцию маппинга на:
```javascript
mapObjectToFrontFormat: (obj) => {
            let fullPath;
            if (obj.folder) {
                fullPath = obj.path ? obj.path + obj.name : obj.name;
            } else {
                fullPath = obj.path + obj.name;
            }

            return {
                lastModified: null,
                name: obj.name,
                size: obj.size,
                path: fullPath,
                folder: obj.folder
            };
        }
```


Потом запустите docker-compose:
``` bash
docker compose up -d --build
```

------------------------------------------------------------------------

## Этот проект - шестой в roadmap Сергея Жукова: https://zhukovsd.github.io/java-backend-learning-course/projects/cloud-file-storage/
