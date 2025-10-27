# Ktor Sample API - Техническое задание

## 📋 Описание проекта

Полнофункциональное REST API приложение на Kotlin с использованием Ktor, PostgreSQL, JWT авторизацией, WebSocket поддержкой и Swagger документацией.

## ✅ Реализованные требования

- ✅ **API на Ktor** - Полноценный REST API с CRUD операциями
- ✅ **PostgreSQL** - Надежное хранение данных
- ✅ **JWT Авторизация** - Безопасная аутентификация с токенами
- ✅ **Роли пользователей** - Поддержка ролей `admin` и `user`
- ✅ **WebSocket** - Чат в реальном времени и система оповещений
- ✅ **Docker** - Контейнеризация приложения
- ✅ **Heroku** - Готовность к развертыванию

## 🛠 Технологический стек

- **Kotlin 2.2.20** - Язык программирования
- **Ktor 3.3.1** - Фреймворк для разработки сервера
- **PostgreSQL 15** - Реляционная база данных
- **Exposed** - ORM для работы с БД
- **JWT** - JSON Web Tokens для авторизации
- **WebSocket** - Протокол для двусторонней связи
- **Docker** - Контейнеризация
- **Heroku** - Облачная платформа для деплоя

---

## 🚀 Быстрый старт

### Важно: Остановите локальный PostgreSQL

```bash
# Остановите локальный PostgreSQL
brew services stop postgresql@15
```

### Шаг 1: Запустить PostgreSQL через Docker

```bash
# Запустить PostgreSQL контейнер
docker-compose up -d postgres

# Проверить, что запущен
docker ps | grep postgres
```

### Шаг 2: Запустить приложение

```bash
# Собрать и запустить
./gradlew run
```

Приложение будет доступно на: **http://localhost:8080**

### Шаг 3: Проверить работу

Откройте в браузере: http://localhost:8080

Должно показать: **"Ktor Sample API is running"**

---

## 📝 API Эндпоинты

### Базовая информация

- **Базовый URL**: http://localhost:8080
- **Формат**: JSON

### Аутентификация

#### Регистрация пользователя

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "email": "admin@example.com",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "role": "user",
  "createdAt": "2025-10-27T11:14:32Z"
}
```

#### Вход в систему

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Пользователи (требуется JWT токен)

#### Получить всех пользователей

```bash
GET /api/users
Authorization: Bearer <token>
```

#### Получить пользователя по ID

```bash
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Получить профиль текущего пользователя

```bash
GET /api/profile
Authorization: Bearer <token>
```

### Оповещения (только для admin)

#### Отправить оповещение

```bash
POST /api/notifications/send
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "title": "System Update",
  "content": "The system will be updated tomorrow",
  "recipientId": null
}
```

---

## 🧪 Примеры использования

### cURL примеры

#### 1. Регистрация пользователя

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"pass123"}'
```

#### 2. Вход в систему

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"pass123"}'
```

#### 3. Получить список пользователей (с токеном)

```bash
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### JavaScript примеры

#### Подключение к WebSocket чату

```javascript
// Получите токен через /api/auth/login
const token = "YOUR_JWT_TOKEN_HERE";

// Подключение к чату
const ws = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`);

ws.onopen = () => {
  console.log('Connected to chat');
};

ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('Received:', message);
  
  // Отображение сообщения в UI
  displayMessage(message);
};

oa.onerror = (error) => {
  console.error('WebSocket error:', error);
};

// Отправить сообщение
function sendChatMessage(content) {
  const message = {
    content: content,
    type: "CHAT"
  };
  ws.send(JSON.stringify(message));
}

// Подключение к уведомлениям
const notificationWs = new WebSocket(`ws://localhost:8080/ws/notifications?token=${token}`);

notificationWs.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  console.log('Notification:', notification);
  alert(`${notification.title}: ${notification.content}`);
};
```

---

## 🐳 Docker команды

### Основные команды

```bash
# Запустить PostgreSQL
docker-compose up -d postgres

# Посмотреть логи PostgreSQL
docker logs ktor_sample_db

# Остановить PostgreSQL
docker-compose stop postgres

# Запустить PostgreSQL снова
docker-compose up -d postgres

# Остановить и удалить (БД очистится!)
docker-compose down

# Подключиться к PostgreSQL вручную
docker exec -it ktor_sample_db psql -U postgres -d ktor_sample
```

### Запуск всего через Docker

```bash
# Запустить PostgreSQL + Приложение
docker-compose up

# Или в фоновом режиме
docker-compose up -d

# Посмотреть логи
docker-compose logs -f

# Остановить всё
docker-compose down
```

---

## 🔐 Роли пользователей

### User (обычный пользователь)
- Может регистрироваться и входить
- Может получать список пользователей
- Может использовать чат
- Может получать оповещения

### Admin (администратор)
- Все права пользователя +
- Может отправлять оповещения всем пользователям или конкретному

#### Сделать пользователя администратором

```bash
docker exec -it ktor_sample_db psql -U postgres -d ktor_sample \
  -c "UPDATE users SET role = 'admin' WHERE username = 'admin';"
```

---

## 🌐 Развертывание на Heroku

### Шаг 1: Установить Heroku CLI

```bash
# macOS
brew tap heroku/brew && brew install heroku

# Проверить
heroku --version
```

### Шаг 2: Войти в Heroku

```bash
heroku login
```

### Шаг 3: Создать приложение

```bash
heroku create your-app-name
```

### Шаг 4: Добавить PostgreSQL

```bash
heroku addons:create heroku-postgresql:hobby-dev
```

### Шаг 5: Настроить переменные окружения

```bash
heroku config:set JWT_SECRET="your-secret-key-here"
heroku config:set DB_USER="postgres"
heroku config:set DB_PASSWORD="your-password"
```

### Шаг 6: Отправить код

```bash
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

### Шаг 7: Открыть приложение

```bash
heroku open
```

---

## 🗄 База данных

### Схема базы данных

Таблица `users`:

| Колонка | Тип | Описание |
|---------|-----|----------|
| id | BIGSERIAL | Первичный ключ |
| username | VARCHAR(50) | Имя пользователя (уникальное) |
| email | VARCHAR(100) | Email (уникальный) |
| password | VARCHAR(255) | Хеш пароля (SHA-256) |
| role | VARCHAR(20) | Роль: user, admin |
| created_at | TIMESTAMP | Дата создания |

### Просмотр данных

```bash
# Посмотреть всех пользователей
docker exec -it ktor_sample_db psql -U postgres -d ktor_sample \
  -c "SELECT id, username, email, role FROM users;"

# Посмотреть структуру таблицы
docker exec -it ktor_sample_db psql -U postgres -d ktor_sample \
  -c "\d users"
```

---

## 🔧 Устранение проблем

### Ошибка: "Connection refused" к PostgreSQL

```bash
# Проверьте, что PostgreSQL запущен
docker ps | grep postgres

# Если не запущен
docker-compose up -d postgres

# Проверьте порт
lsof -i :5432
```

### Ошибка: "role postgres does not exist"

Это значит, что локальный PostgreSQL мешает. Остановите его:

```bash
brew services list | grep postgresql
brew services stop postgresql@15
```

### Порт 5432 занят

```bash
# Найдите процесс
lsof -i :5432

# Убейте процесс или измените порт в docker-compose.yml
```

### Ошибка подключения к БД

```bash
# Проверьте, что контейнер работает
docker ps | grep postgres

# Проверьте подключение
docker exec -it ktor_sample_db pg_isready -U postgres
```

---

## 📁 Структура проекта

```
ktor-sample/
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/
│   │   │   ├── model/          # Модели данных
│   │   │   ├── repository/     # Репозитории для работы с БД
│   │   │   ├── service/        # Бизнес-логика
│   │   │   ├── routes/         # API роуты
│   │   │   ├── config/         # Конфигурация (DB, JWT)
│   │   │   └── util/           # Утилиты
│   │   └── resources/
│   │       └── application.yaml
├── build.gradle.kts
├── docker-compose.yml
├── Dockerfile
├── Procfile
└── README.md
```

---

## 📊 Все эндпоинты

| Метод | URL | Описание | Требует авторизации |
|-------|-----|----------|---------------------|
| GET | / | Health check | Нет |
| POST | /api/auth/register | Регистрация | Нет |
| POST | /api/auth/login | Вход | Нет |
| GET | /api/users | Список пользователей | Да |
| GET | /api/users/{id} | Пользователь по ID | Да |
| GET | /api/profile | Профиль текущего пользователя | Да |
| POST | /api/notifications/send | Отправить уведомление | Да (admin) |
| WS | /ws/chat?token=... | WebSocket чат | Да |
| WS | /ws/notifications?token=... | WebSocket уведомления | Да |

---

## 🎯 Критерии выполненного ТЗ

- ✅ **API полностью функционально** - Все эндпоинты работают
- ✅ **Авторизация и роли работают** - JWT + admin/user
- ✅ **WebSocket функционирует** - Чат и оповещения
- ✅ **Приложение доступно** - http://localhost:8080
- ✅ **Код структурирован** - Четкая архитектура
- ✅ **Код в Git с README** - Полная документация

