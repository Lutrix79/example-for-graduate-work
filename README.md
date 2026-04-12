# Платформа объявлений (STOLB)

## О проекте

**STOLB** - это бэкенд-часть веб-приложения для размещения и управления объявлениями.
Проект разработан в рамках учебного курса и представляет собой REST API сервис,
аналогичный популярным платформам (Avito, Юла).

### Основной функционал:
- Регистрация и аутентификация пользователей (Basic Auth)
- Управление объявлениями (CRUD операции)
- Добавление и управление комментариями
- Загрузка и отображение изображений (аватары, фото объявлений)
- Разграничение прав доступа (USER / ADMIN)

---

## Используемые технологии

| Категория | Технологии |
|-----------|------------|
| **Язык** | Java 17 |
| **Фреймворк** | Spring Boot 3.1.0 |
| **Безопасность** | Spring Security 6.1.0 (Basic Auth) |
| **База данных** | PostgreSQL 42.6.0, Hibernate 6.2.2 |
| **Маппинг** | MapStruct 1.5.5, Lombok 1.18.26 |
| **Документация API** | OpenAPI 3.0, Swagger UI (springdoc-openapi 2.1.0) |
| **Сборка** | Maven |
| **Тестирование** | Postman |

---

## 🔧 Установка и запуск

### Требования
- Java 17 или выше
- Maven 3.8+
- PostgreSQL 14+

### Шаг 1: Клонирование репозитория
git clone <repository-url>
cd STOOLB

### 2. Создание базы данных PostgreSQL
- CREATE DATABASE ads_db;
- CREATE USER your_user WITH PASSWORD 'your_password';
- GRANT ALL PRIVILEGES ON DATABASE ads_db TO your_user;

### 3. Настройка application.properties
Создайте файл src/main/resources/application.properties:

- spring.datasource.url=jdbc:postgresql://localhost:5432/ads_db
- spring.datasource.username=your_user
- spring.datasource.password=your_password
- spring.datasource.driver-class-name=org.postgresql.Driver
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=false
- server.port=8080

### 4. Сборка и запуск
- mvn clean install
- mvn spring-boot:run

### 5. Проверка
Откройте в браузере: http://localhost:8080/swagger-ui.html

## API Эндпоинты

### Пользователи
| **POST /register**    | **- регистрация**      |
|-----------------------|------------------------|
| **POST /login**       | **- вход**             |
| **GET /users/me**     | **- получить профиль** |
| **PATCH /users/me     | **- обновить профиль** |
| PATCH /users/me/image | **- загрузить аватар** |

### Объявления
| **GET /ads**       | **- все объявления**      |
|--------------------|---------------------------|
| **GET /ads/{id}**  | **- объявление по ID**    |
| **POST /ads**      | **- создать объявление**  |
| **PATCH  /ads/{id}** | **- обновить объявление** |
| **DELETE /ads/{id}** | **- удалить объявление**  |
| **GET /ads/me**    | **- мои объявления** |

### Комментарии
| **GET    /ads/{id}/comments**               | **- комментарии объявления** |
|---------------------------------------------|------------------------------|
| **POST   /ads/{id}/comments**               | **- добавить комментарий**   |
| **PATCH  /ads/{adId}/comments/{commentId}** | **- обновить комментарий**   |
| **DELETE /ads/{adId}/comments/{commentId}** | **- удалить комментарий**    |

## Документация
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Структура проекта
```plaintext
STOOLB/
├── src/main/java/ru/skypro/homework/
│   ├── controller/     - REST контроллеры
│   ├── service/        - бизнес-логика
│   ├── repository/     - работа с БД
│   ├── entity/         - сущности
│   ├── dto/            - объекты передачи данных
│   ├── mapper/         - преобразование Entity <-> DTO
│   ├── config/         - настройки (Security, Swagger)
│   └── exception/      - обработка ошибок
├── uploads/
│   ├── avatars/        - аватары пользователей
│   └── ads/            - картинки объявлений
└── pom.xml