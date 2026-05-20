# notification-ms

Microservicio de **notificaciones** (proyecto académico). Consume eventos desde **RabbitMQ**, persiste las notificaciones y las entrega en **tiempo real** a clientes vía **WebSocket (STOMP + SockJS)[...]

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

## 3. Arquitectura y patrones de diseño

### 3.1 Estilo arquitectónico

- **Arquitectura por capas (Layered Architecture)**:
  - **Controller (API REST)**: expone endpoints HTTP y delega la lógica de negocio.
  - **Service (dominio / aplicación)**: orquesta el procesamiento de eventos y la creación de notificaciones.
  - **Repository (persistencia)**: acceso a datos mediante Spring Data JPA.
  - **Model/Entity**: representación de la tabla `notificaciones`.

- **Orientada a eventos (Event-driven)**:
  - El microservicio reacciona a eventos publicados por otros componentes (RabbitMQ) y ejecuta el flujo de notificación.

- **Comunicación en tiempo real (push)**:
  - Entrega de notificaciones hacia el cliente vía WebSocket (STOMP), publicando en topics.

### 3.2 Patrones aplicados

- **Publish/Subscribe (Pub/Sub)**:
  - RabbitMQ: productor publica evento → `notification-ms` se suscribe/consume desde una cola.
  - WebSocket STOMP: el servicio publica a `/topic/notifications/{idUsuario}` y los clientes suscritos reciben.

- **Message Listener / Consumer**:
  - El consumo de mensajes se implementa mediante `@RabbitListener`, separando la recepción del mensaje de la lógica del negocio.

- **DTO (Data Transfer Object)**:
  - El evento entrante se representa como `CoincidenciaEventDTO`.
  - Conversión JSON → DTO mediante `Jackson2JsonMessageConverter`.

- **Repository pattern**:
  - `NotificacionRepository` abstrae las operaciones CRUD sobre la entidad `Notificacion`.

- **Dependency Injection (IoC)**:
  - Spring gestiona las dependencias entre controllers, services, repositories y configuraciones.

- **Retry / Recovery (resiliencia)**:
  - Reintentos de procesamiento con `@Retryable` y manejo final con `@Recover` para registrar fallos cuando se agotan los intentos.

### 3.3 Componentes principales (mapeo a código)

- **Controller (REST)**: `NotificationController`
- **Servicio de dominio**: `NotificationService`
- **Gateway de tiempo real (WebSocket publisher)**: `WebSocketNotificationService`
- **Persistencia**: `NotificacionRepository` + entidad `Notificacion`
- **Configuración de infraestructura**: `WebSocketConfig`, `RabbitMQConfig`

---

## 4. Configuración de WebSocket

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

## 5. Configuración de RabbitMQ

- El listener consume desde la cola configurada por:
  - `app.rabbitmq.queue`
- Se utiliza `Jackson2JsonMessageConverter` para convertir automáticamente JSON → DTO.

---

## 6. Endpoints REST

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

## 7. Prueba rápida (WebSocket)

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

## 8. Estructura del proyecto

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

## 9. Docker

Este repo incluye un `Dockerfile` multi-stage que:

1) Compila el proyecto con Maven
2) Ejecuta el JAR en una imagen JRE

Expone el puerto:

- `8081`

---

## 10. Notas de resiliencia

El procesamiento implementa reintentos:

- `@Retryable` con `maxAttempts = 3`
- `@Recover` para registrar notificaciones fallidas en BD cuando se agotan los intentos

---

## 11. Licencia

Proyecto académico (sin licencia explícita).
