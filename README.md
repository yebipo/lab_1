Task API
REST API для управления задачами агента-антипрокрастинатора.

Стек
Java 25, Spring Boot 3

Maven, Checkstyle, SonarLint

Эндпоинты
GET /api/tasks/{id} — получение по ID (@PathVariable)

GET /api/tasks/search?title=... — поиск по названию (@RequestParam)

Модель данных (JSON)
JSON
{
  "id": 1,
  "title": "String",
  "description": "String",
  "focusScore": 100,
  "status": "ACTIVE"
}
