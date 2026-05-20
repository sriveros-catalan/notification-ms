# notification-ms

Microservicio de **notificaciones** (proyecto académico). Consume eventos desde **RabbitMQ**, persiste las notificaciones y las entrega en **tiempo real** a clientes vía **WebSocket (STOMP + SockJS)**.

> Repo: `sriveros-catalan/notification-ms`

---

## 1. Rol dentro del sistema

Este microservicio se encarga de:

- **Escuchar eventos** (ej. *coincidencia/match*) desde una cola RabbitMQ.
- **Procesar** el evento y construir una notificación.
- **Persistir** la notificación en BD (JPA) en la tabla `notificaciones`.
- **Notificar en tiempo real** al usuario mediante WebSocket publicando en un topic.

Flujo general:

1) Otro componente publica un evento (JSON) en RabbitMQ.
2) `notification-ms` lo consume con `@RabbitListener`.
3) Guarda la notificación en BD.
4) Publica el mensaje a `/topic/notifications/{idUsuario}`.
5) El cliente web suscrito recibe la notificación al instante.

---

## 2. Tecnologías

- Java / Spring Boot
- Spring AMQP (RabbitMQ)
- Spring WebSocket (STOMP) + SockJS
- Spring Data JPA
- Lombok
- Docker (multi-stage build)

---

## 3. Configuración de WebSocket

- Endpoint STOMP (SockJS):
  - `http://localhost:8081/ws-notifications`
- Broker en memoria:
  - `/topic` y `/queue`
- Prefijo para mensajes enviados por el cliente:
  - `/app`

Un cliente debe suscribirse a:

- `/topic/notifications/{idUsuario}`

Ejemplo:

- Usuario `1` → `/topic/notifications/1`

---

## 4. Configuración de RabbitMQ

- El listener consume desde la cola configurada por:
  - `app.rabbitmq.queue`
- Se utiliza `Jackson2JsonMessageConverter` para convertir automáticamente JSON → DTO.

---

## 5. Endpoints REST

Base path:

- `/api/notifications`

Endpoints:

- `POST /api/notifications/match`
  - Recibe un `CoincidenciaEventDTO` y procesa la notificación (útil para pruebas sin RabbitMQ).

- `GET /api/notifications`
  - Lista todas las notificaciones persistidas.

- `GET /api/notifications/{id}`
  - Obtiene una notificación por ID.

---

## 6. Prueba rápida (WebSocket)

Hay una página HTML de prueba incluida en el proyecto:

- `src/main/resources/static/index.html`

Abre en el navegador:

- `http://localhost:8081/index.html`

Pasos:

1) Inicia el microservicio.
2) Abre `http://localhost:8081/index.html`.
3) Ingresa un `ID de usuario` (por ejemplo `1`).
4) Conéctate: se suscribe a `/topic/notifications/{idUsuario}`.
5) Cuando llegue un evento para ese usuario, verás el mensaje en pantalla.

---

## 7. Estructura del proyecto

Principales clases:

- Listener y lógica:
  - `src/main/java/com/sanosysalvos/notification/service/NotificationService.java`

- Envío WebSocket:
  - `src/main/java/com/sanosysalvos/notification/service/WebSocketNotificationService.java`

- Config WebSocket:
  - `src/main/java/com/sanosysalvos/notification/config/WebSocketConfig.java`

- Config RabbitMQ:
  - `src/main/java/com/sanosysalvos/notification/config/RabbitMQConfig.java`

- REST Controller:
  - `src/main/java/com/sanosysalvos/notification/controller/NotificationController.java`

- Entidad JPA:
  - `src/main/java/com/sanosysalvos/notification/model/Notificacion.java`

- Repositorio:
  - `src/main/java/com/sanosysalvos/notification/repository/NotificacionRepository.java`

- DTO del evento:
  - `src/main/java/com/sanosysalvos/notification/dto/CoincidenciaEventDTO.java`

---

## 8. Docker

Este repo incluye un `Dockerfile` multi-stage que:

1) Compila el proyecto con Maven
2) Ejecuta el JAR en una imagen JRE

Expone el puerto:

- `8081`

---

## 9. Notas de resiliencia

El procesamiento implementa reintentos:

- `@Retryable` con `maxAttempts = 3`
- `@Recover` para registrar notificaciones fallidas en BD cuando se agotan los intentos

---

## 10. Licencia

Proyecto académico (sin licencia explícita).
