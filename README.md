# Arquitectura de Microservicios con Spring Boot

Un proyecto educativo diseñado para estudiantes de segundo año de Ingeniería en Informática que enseña los principios fundamentales de una **arquitectura de microservicios** con Spring Framework: registro y descubrimiento de servicios, enrutamiento centralizado mediante API Gateway, comunicación entre microservicios con OpenFeign, y buenas prácticas de APIs REST.

Este proyecto corresponde al ramo **Fullstack I** para estudiantes de **Ingeniería en Informática de DUOC UC**.

---

## 📚 Objetivos de Aprendizaje

- Comprender la arquitectura de microservicios y sus componentes principales
- Implementar un Eureka Server para registro y descubrimiento de servicios
- Configurar un API Gateway como punto de entrada único
- Comunicar microservicios entre sí mediante **OpenFeign**
- Dominar decoradores (anotaciones) de Spring Boot
- Implementar APIs REST siguiendo buenas prácticas con códigos HTTP apropiados
- Gestionar esquemas de base de datos con migraciones **Flyway**
- Documentar APIs con **Swagger UI** agregado en el Gateway
- Aplicar el patrón **CSR (Controller-Service-Repository)**
- Implementar **logging** con SLF4J y Logback
- Contenerizar servicios con **Docker** y orquestarlos con **Docker Compose**

---

## 🔧 Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+**
- **Docker Desktop** (o Docker Engine + Docker Compose v2)
- **IDE recomendado**: IntelliJ IDEA, Visual Studio Code o Eclipse
- Conocimientos básicos de Java, POO y APIs REST

---

## 📦 Dependencias Principales (por microservicio)

```xml
<!-- Spring Boot Web MVC (REST Controllers) -->
spring-boot-starter-web

<!-- Spring Boot Validation (Validación de datos) -->
spring-boot-starter-validation

<!-- Spring Boot Data JPA (Persistencia con Hibernate) -->
spring-boot-starter-data-jpa

<!-- MySQL Connector -->
mysql-connector-j

<!-- Lombok (Generación automática de boilerplate) -->
lombok

<!-- Flyway (Migraciones de base de datos) -->
flyway-core 10.11.1
flyway-mysql 10.11.1

<!-- Spring Cloud OpenFeign (Comunicación entre microservicios) -->
spring-cloud-starter-openfeign

<!-- Eureka Client (Registro en Eureka Server) -->
spring-cloud-starter-netflix-eureka-client

<!-- SpringDoc OpenAPI UI (Swagger automático) -->
springdoc-openapi-starter-webmvc-ui 2.8.6

<!-- H2 (Base de datos en memoria para tests) -->
h2 (scope: test)
```

---

## 🏗️ Estructura del Proyecto

```
microservicios/
├── eureka/             # Eureka Server — registro y descubrimiento (puerto 8761)
├── gateway/            # API Gateway — punto de entrada único (puerto 8080)
├── productos/          # Microservicio Productos (puerto 8082)
├── clientes/           # Microservicio Clientes (puerto 8081)
├── categorias/         # Microservicio Categorías (puerto 8083)
├── ventas/             # Microservicio Ventas (puerto 8084)
├── sistema.html        # SPA React — interfaz web para todos los microservicios
└── docker-compose.yml  # Orquestación de todos los servicios
```

### Estructura interna de cada microservicio

```
<servicio>/
├── src/
│   ├── main/
│   │   ├── java/com/duoc/<servicio>/
│   │   │   ├── <Servicio>Application.java        # Punto de entrada (@EnableFeignClients)
│   │   │   ├── client/                           # Clientes Feign (comunicación entre servicios)
│   │   │   ├── config/
│   │   │   │   └── SwaggerConfig.java            # Configuración Swagger / OpenAPI
│   │   │   ├── controller/                       # Endpoints REST
│   │   │   ├── dto/                              # Request y Response DTOs
│   │   │   ├── model/                            # Entidades JPA
│   │   │   ├── repository/                       # Acceso a datos (JPA)
│   │   │   ├── service/                          # Lógica de negocio
│   │   │   └── exception/                        # Excepciones personalizadas y GlobalExceptionHandler
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/                     # Scripts SQL versionados (Flyway)
│   └── test/
├── Dockerfile
└── pom.xml
```

---

## 🌐 Arquitectura del Sistema

```
                          ┌──────────────-───┐
    sistema.html          │   Eureka Server  │
    (React SPA)  ────────▶│   :8761          │◀─── todos los servicios se registran
         │                └───────────────-──┘
         │
         ▼
┌─────────────────┐
│   API Gateway   │  ←── punto de entrada único
│   :8080         │
└────────┬────────┘
         │  enruta según el path
    ┌────┴────────────────────────────────────┐
    │            │              │             │
    ▼            ▼              ▼             ▼
┌───────-─┐  ┌─────────┐  ┌──────────┐  ┌────────┐
│Productos│  │Clientes │  │Categorias│  │ Ventas │
│ :8082   │  │ :8081   │  │ :8083    │  │ :8084  │
└────┬───-┘  └────┬────┘  └────┬─────┘  └───┬────┘
     │            │            │            │
     ▼            ▼            ▼            ▼
┌─────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐
│db_prod. │ │db_clien.│ │db_categ. │ │db_ventas │
│ MySQL   │ │ MySQL   │ │ MySQL    │ │ MySQL    │
│ :3307   │ │ :3308   │ │ :3309    │ │ :3310    │
└─────────┘ └─────────┘ └──────────┘ └──────────┘
```

### Comunicación entre microservicios (OpenFeign)

```
Productos  ──Feign──▶  Categorias   (valida que la categoría exista antes de guardar)
Ventas     ──Feign──▶  Clientes     (valida que el cliente exista)
Ventas     ──Feign──▶  Productos    (obtiene precio y valida que el producto exista)
```

---

## ⚙️ Servicios y Puertos

| Servicio          | Descripción                | Puerto host |
|-------------------|----------------------------|-------------|
| `eureka`          | Eureka Server              | 8761        |
| `gateway`         | API Gateway                | 8080        |
| `productos`       | Microservicio productos    | —           |
| `clientes`        | Microservicio clientes     | —           |
| `categorias`      | Microservicio categorías   | —           |
| `ventas`          | Microservicio ventas       | —           |
| `db-productos`    | MySQL para productos       | 3307        |
| `db-clientes`     | MySQL para clientes        | 3308        |
| `db-categorias`   | MySQL para categorías      | 3309        |
| `db-ventas`       | MySQL para ventas          | 3310        |

---

## 🚀 Cómo Levantar el Proyecto

### Paso 1: Ir a la raíz del proyecto

```bash
cd microservicios
```

### Paso 2: Construir e iniciar todos los servicios

```bash
docker compose up --build -d
```

> La primera vez tarda unos minutos mientras descarga imágenes y compila los JARs. Las siguientes son mucho más rápidas gracias al caché de Docker.

### Paso 3: Verificar que todos los contenedores estén corriendo

```bash
docker compose ps
```

### Paso 4: Esperar ~30 segundos

Los microservicios necesitan registrarse en Eureka antes de que el Gateway pueda enrutarlos. Es normal ver errores de conexión en los primeros segundos.

### Paso 5: Verificar en Eureka Dashboard

Abre `http://localhost:8761` y confirma que aparecen: `GATEWAY`, `PRODUCTOS`, `CLIENTES`, `CATEGORIAS`, `VENTAS`.

---

## 💻 Desarrollo Local (Sin Docker)

Si prefieres desarrollar de manera local (ejecutando los servicios directamente en tu máquina o desde el IDE), sigue estas directrices:

### Paso 1: Base de Datos Local
1. Asegúrate de tener una instancia de MySQL corriendo localmente en el puerto `3306`.
2. Las credenciales por defecto en los archivos `application.properties` son:
   * **Usuario:** `root`
   * **Contraseña:** `my-secret-pw`
3. Crea las cuatro bases de datos necesarias para que Flyway pueda inicializar el esquema. Ejecuta en tu cliente SQL:
   ```sql
   CREATE DATABASE IF NOT EXISTS db_productos;
   CREATE DATABASE IF NOT EXISTS db_clientes;
   CREATE DATABASE IF NOT EXISTS db_categorias;
   CREATE DATABASE IF NOT EXISTS db_ventas;
   ```

### Paso 2: Compilación y Ejecución (Uso de Maven Wrapper)
Dado que el proyecto no cuenta con un POM padre en el directorio raíz, cada microservicio debe ser gestionado individualmente dentro de su propia carpeta usando el Maven Wrapper (`./mvnw` o `mvnw.cmd`):

*   **Compilar un servicio (genera el archivo .jar):**
    ```bash
    cd <carpeta-del-servicio>
    ./mvnw clean package -DskipTests
    ```
*   **Ejecutar un servicio en modo desarrollo:**
    ```bash
    cd <carpeta-del-servicio>
    ./mvnw spring-boot:run
    ```

### Paso 3: Orden de Encendido Recomendado
Para evitar errores de conexión en el arranque, levanta los servicios en el siguiente orden:
1. **Eureka Server (`eureka`):** Debe iniciarse primero para recibir los registros.
2. **Microservicios de negocio (`categorias`, `clientes`, `productos`, `ventas`):** Espera a que se registren en Eureka.
3. **API Gateway (`gateway`):** Debe levantarse al final para poder enrutar correctamente las solicitudes hacia las instancias registradas.

---

## 🌍 URLs Útiles

| Recurso                     | URL                                        |
|-----------------------------|--------------------------------------------|
| Eureka Dashboard            | http://localhost:8761                      |
| Swagger UI (todos los APIs) | http://localhost:8080/swagger-ui.html      |
| API Productos               | http://localhost:8080/api/v1/productos     |
| API Clientes                | http://localhost:8080/api/v1/clientes      |
| API Categorías              | http://localhost:8080/api/v1/categorias    |
| API Ventas                  | http://localhost:8080/api/v1/ventas        |

---

## 📖 Swagger UI Agregado

Todos los microservicios están documentados con Swagger y se acceden **desde un único punto** a través del API Gateway:

```
http://localhost:8080/swagger-ui.html
```

Usa el **selector desplegable** en la parte superior derecha para cambiar entre:
- `productos`
- `clientes`
- `categorias`
- `ventas`

Cada uno permite explorar y probar todos los endpoints directamente desde el navegador.

---

## 🗺️ Rutas Configuradas en el Gateway

| Path                       | Microservicio |
|----------------------------|---------------|
| `/api/v1/productos/**`     | PRODUCTOS     |
| `/api/v1/clientes/**`      | CLIENTES      |
| `/api/v1/categorias/**`    | CATEGORIAS    |
| `/api/v1/ventas/**`        | VENTAS        |

---

## 📋 Endpoints por Microservicio

### Productos (`/api/v1/productos`)

| Método | URL                              | Descripción                    | Status |
|--------|----------------------------------|--------------------------------|--------|
| POST   | `/api/v1/productos`              | Crear producto                 | 201    |
| GET    | `/api/v1/productos`              | Listar todos / filtrar nombre  | 200    |
| GET    | `/api/v1/productos/{id}`         | Buscar por ID                  | 200/404|
| PUT    | `/api/v1/productos/{id}`         | Actualizar producto            | 200/404|
| DELETE | `/api/v1/productos/{id}`         | Eliminar producto              | 204/404|

> La categoría es validada contra el microservicio **Categorias** vía Feign antes de guardar o actualizar.

### Clientes (`/api/v1/clientes`)

| Método | URL                            | Descripción                      | Status |
|--------|--------------------------------|----------------------------------|--------|
| POST   | `/api/v1/clientes`             | Crear cliente                    | 201    |
| GET    | `/api/v1/clientes`             | Listar todos / filtrar nombre    | 200    |
| GET    | `/api/v1/clientes/{id}`        | Buscar por ID                    | 200/404|
| PUT    | `/api/v1/clientes/{id}`        | Actualizar cliente               | 200/404|
| DELETE | `/api/v1/clientes/{id}`        | Eliminar cliente                 | 204/404|

### Categorías (`/api/v1/categorias`)

| Método | URL                               | Descripción                     | Status |
|--------|-----------------------------------|---------------------------------|--------|
| POST   | `/api/v1/categorias`              | Crear categoría                 | 201    |
| GET    | `/api/v1/categorias`              | Listar todas / filtrar nombre   | 200    |
| GET    | `/api/v1/categorias/{id}`         | Buscar por ID                   | 200/404|
| PUT    | `/api/v1/categorias/{id}`         | Actualizar categoría            | 200/404|
| DELETE | `/api/v1/categorias/{id}`         | Eliminar categoría              | 204/404|

### Ventas (`/api/v1/ventas`)

| Método | URL                           | Descripción                                    | Status |
|--------|-------------------------------|------------------------------------------------|--------|
| POST   | `/api/v1/ventas`              | Registrar venta (valida cliente y producto)    | 201    |
| GET    | `/api/v1/ventas`              | Listar todas / filtrar por clienteId/productoId| 200    |
| GET    | `/api/v1/ventas/{id}`         | Buscar por ID                                  | 200/404|
| DELETE | `/api/v1/ventas/{id}`         | Eliminar venta (corrección operativa)          | 204/404|

> Al registrar una venta, el servicio valida automáticamente que el cliente y el producto existan vía Feign, obtiene el precio actual del producto, y calcula el `totalVenta = cantidad × precioUnitario`.

---

## 🧾 Modelo de Datos

### Venta

| Campo            | Tipo          | Descripción                                        |
|------------------|---------------|----------------------------------------------------|
| `id`             | Long          | ID único (auto-generado)                           |
| `clienteId`      | Long          | ID del cliente que realizó la compra               |
| `productoId`     | Integer       | ID del producto comprado                           |
| `cantidad`       | Integer       | Unidades compradas                                 |
| `precioUnitario` | Integer       | Precio al momento de la venta (snapshot histórico) |
| `totalVenta`     | Integer       | cantidad × precioUnitario (auto-calculado)         |
| `fechaVenta`     | LocalDateTime | Fecha y hora exacta de la venta (auto-generada)    |
| `notas`          | String        | Observaciones opcionales                           |

> `precioUnitario` almacena el precio en el momento exacto de la compra. Si el precio del producto cambia después, la venta conserva el precio histórico correcto.

---

## 🗄️ Conexión a Bases de Datos (desde el host)

Por defecto, las bases de datos no exponen puertos al host en `docker-compose.yml` para evitar conflictos de puertos en tu máquina local. Si deseas conectarte directamente desde un cliente SQL (DataGrip, DBeaver, TablePlus, MySQL Workbench), debes agregar la sección `ports` a cada base de datos en `docker-compose.yml` (por ejemplo, `- "3307:3306"` para `db-productos`). Una vez expuestos, los parámetros de conexión recomendados son:

| Base de datos   | Host        | Puerto | Usuario | Contraseña |
|-----------------|-------------|--------|---------|------------|
| `db_productos`  | `localhost` | `3307` | `root`  | `root`     |
| `db_clientes`   | `localhost` | `3308` | `root`  | `root`     |
| `db_categorias` | `localhost` | `3309` | `root`  | `root`     |
| `db_ventas`     | `localhost` | `3310` | `root`  | `root`     |

---

## 🎯 Conceptos Clave

### 1. Eureka Server — Registro y Descubrimiento

Eureka es el "directorio telefónico" de los microservicios. Cada servicio se registra al iniciar y consulta Eureka para encontrar a los demás sin necesidad de conocer IPs o puertos fijos.

```
Servicio arranca → se registra en Eureka con su nombre (ej: PRODUCTOS)
Gateway necesita PRODUCTOS → consulta Eureka → obtiene dirección → enruta la petición
```

Configuración en cada microservicio:
```properties
spring.application.name=productos
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### 2. API Gateway — Punto de Entrada Único

El Gateway recibe todas las peticiones externas y las enruta al microservicio correcto según el path:

```
Cliente → GET http://localhost:8080/api/v1/productos/1
              │
              ▼
         Gateway lee el path: /api/v1/productos/**
              │
              ▼
         Consulta Eureka: ¿dónde está PRODUCTOS?
              │
              ▼
         Enruta al contenedor productos:8082
```

Usar `lb://PRODUCTOS` en lugar de una URL fija activa el **load balancing** — si hubiera múltiples instancias de Productos, el Gateway distribuiría la carga automáticamente.

### 3. OpenFeign — Comunicación Declarativa entre Microservicios

Feign permite llamar a otros microservicios como si fueran clases locales, sin escribir código HTTP:

```java
// En el microservicio Ventas:
@FeignClient(name = "clientes")         // nombre registrado en Eureka
public interface ClienteClient {
    @GetMapping("/api/v1/clientes/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);
}

// Uso en VentaService — igual que llamar a un Repository local:
ClienteDTO cliente = clienteClient.obtenerCliente(request.getClienteId());
```

Feign genera automáticamente la implementación HTTP. Para el Service, llamar a Feign es idéntico a llamar a un Repository.

### 4. Patrón DTO (Data Transfer Object)

Cada microservicio separa la entidad de base de datos del objeto expuesto al cliente:

| Clase                               | Rol         | Descripción                                                  |
|-------------------------------------|-------------|--------------------------------------------------------------|
| `Model` (ej: `Venta.java`)          | Entidad JPA | Mapeada a la tabla de BD, usada internamente                 |
| `Request` (ej: `VentaRequest.java`) | Entrada     | Datos que envía el cliente + validaciones                    |
| `DTO` (ej: `VentaDTO.java`)         | Salida      | Datos que devuelve la API (puede incluir datos enriquecidos) |

### 5. Migraciones con Flyway

Todos los microservicios usan Flyway para gestionar el esquema de base de datos de forma versionada:

```
db/migration/
├── V1__create_ventas_table.sql      # Crea la tabla
└── V2__insert_datos_iniciales.sql   # Datos de ejemplo (opcional)
```

Flyway garantiza que todos los entornos (desarrollo, producción, contenedores) tienen exactamente el mismo esquema, con historial auditable en `flyway_schema_history`.

```properties
spring.jpa.hibernate.ddl-auto=none   # Hibernate NO toca el esquema
spring.flyway.enabled=true           # Flyway gestiona el esquema
```

### 6. Patrón CSR (Controller-Service-Repository)

Versión adaptada de MVC para APIs REST con Spring Boot:

| Capa              | Componente                 | Responsabilidad                                              |
|-------------------|----------------------------|--------------------------------------------------------------|
| Presentación.     | **Controller**             | Mapea rutas HTTP, valida entrada, retorna respuestas         |
| Lógica de Negocio | **Service**                | Lógica empresarial, conversión DTO ↔ Entidad, llamadas Feign |
| Datos             | **Repository**             | Acceso a MySQL mediante JPA (CRUD automático)                |
| Datos             | **Model**                  | Entidad JPA mapeada a tabla                                  |
| Transferencia     | **DTOs**                   | Contratos de entrada/salida                                  |
| Errores           | **GlobalExceptionHandler** | Captura excepciones y retorna respuestas coherentes          |

### 7. HTTP Status Codes

| Código  | Nombre              | Cuándo se usa                                               |
|---------|---------------------|-------------------------------------------------------------|
| **200** | OK                  | GET por ID, PUT exitoso                                     |
| **201** | Created             | POST exitoso                                                |
| **204** | No Content          | Lista vacía, DELETE exitoso                                 |
| **400** | Bad Request         | Validaciones fallidas                                       |
| **404** | Not Found           | ID inexistente, recurso no encontrado en Feign              |
| **503** | Service Unavailable | Microservicio no disponible (Eureka no lo tiene registrado) |

### 8. Logging con SLF4J y `@Slf4j`

```java
@Slf4j
@Service
public class VentaService {
    public VentaDTO registrar(VentaRequest request) {
        log.info("Registrando venta para cliente id={}", request.getClienteId());    // flujo normal
        log.warn("No se pudo enriquecer con datos del cliente: {}", e.getMessage()); // inesperado
        log.error("Error al guardar venta: {}", e.getMessage());                     // error grave
    }
}
```

Los logs se guardan en `logs/<servicio>.log` con rotación automática de 10 MB y 7 días de historial.

---

## 🧪 Ejemplos de Uso con cURL

### Crear una categoría

```bash
curl -X POST http://localhost:8080/api/v1/categorias \
  -H "Content-Type: application/json" \
  -d '{"name": "Periféricos"}'
```

### Crear un producto

```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Teclado Gamer Razer",
    "precio": 39990,
    "categoria": "Periféricos"
  }'
```

### Crear un cliente

```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com"
  }'
```

### Registrar una venta

```bash
curl -X POST http://localhost:8080/api/v1/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "productoId": 1,
    "cantidad": 2,
    "notas": "Despacho a domicilio"
  }'
```

**Respuesta (201)**:
```json
{
  "id": 1,
  "clienteId": 1,
  "nombreCliente": "Juan Pérez",
  "productoId": 1,
  "nombreProducto": "Teclado Gamer Razer",
  "cantidad": 2,
  "precioUnitario": 39990,
  "totalVenta": 79980,
  "fechaVenta": "2026-06-13T20:40:00",
  "notas": "Despacho a domicilio"
}
```

### Filtrar ventas por cliente

```bash
curl http://localhost:8080/api/v1/ventas?clienteId=1
```

---

## 📊 Flujo de Datos en el Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENTE (sistema.html, cURL)                  │
└────────────────────────┬────────────────────────────────────────┘
                         │ 1. Envía JSON a :8080
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (:8080)                          │
│  - Recibe la petición HTTP                                      │
│  - Lee el path: /api/v1/ventas/**                               │
│  - Consulta Eureka para obtener dirección del microservicio     │
│  - Agrega headers CORS y deduplica Access-Control-Allow-Origin  │
└────────────────────────┬────────────────────────────────────────┘
                         │ 2. Enruta a ventas-service
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  VentaController                                │
│  - Recibe request HTTP                                          │
│  - Mapea JSON → VentaRequest (@RequestBody)                     │
│  - Valida datos (@Valid)                                        │
│  - Si hay errores → GlobalExceptionHandler (captura)            │
└────────────────────────┬────────────────────────────────────────┘
                         │ 3. Si válido, llama al servicio
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  VentaService                                   │
│  - Llama a ClienteClient (Feign → CLIENTES vía Eureka)          │
│  - Llama a ProductoClient (Feign → PRODUCTOS vía Eureka)        │
│  - Calcula totalVenta = cantidad × precioUnitario               │
│  - Auto-asigna fechaVenta = LocalDateTime.now()                 │
│  - Lanza ResourceNotFoundException si no existe el recurso      │
└────────────────────────┬────────────────────────────────────────┘
                         │ 4. Persistencia
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  VentaRepository (JPA)                          │
│  - Extiende JpaRepository (CRUD automático)                     │
│  - Conecta con MySQL (db_ventas)                                │
│  - Hibernate genera y ejecuta el SQL                            │
└────────────────────────┬────────────────────────────────────────┘
                         │ 5. Respuesta
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENTE (Recibe respuesta JSON)               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🖥️ Interfaz Web (sistema.html)

El archivo `sistema.html` es una SPA React de un solo archivo que permite gestionar todos los microservicios desde el navegador:

| Tab               | Funcionalidades                                                          |
|-------------------|--------------------------------------------------------------------------|
| 📦 **Productos**  | Listar, buscar por nombre, crear, editar, eliminar                        |
| 👤 **Clientes**   | Listar, buscar por nombre, crear, editar, eliminar                        |
| 🏷️ **Categorías** | Listar, crear, eliminar                                                   |
| 🧾 **Ventas**     | Listar, registrar nueva venta (selects de clientes y productos), eliminar |

Todas las llamadas pasan por el API Gateway en `http://localhost:8080`.

Para usarlo: abre `sistema.html` directamente en el navegador o con cualquier servidor HTTP local.

---

## 💡 Buenas Prácticas Implementadas

### 1. Separación de Responsabilidades
Cada clase tiene un único propósito claro:
- **Controller**: Mapeo HTTP, sin lógica de negocio
- **Service**: Lógica de negocio, conversión de datos y llamadas Feign
- **Repository**: Acceso a base de datos

### 2. Patrón DTO
Separa la entidad interna del contrato externo, protegiendo el modelo interno y permitiendo enriquecer las respuestas con datos de otros servicios.

### 3. Excepciones Personalizadas
`ResourceNotFoundException` con mensajes descriptivos y `GlobalExceptionHandler` centralizado, en lugar de errores genéricos 500.

### 4. Versionamiento de API
`/api/v1/...` permite evolucionar la API sin romper clientes existentes.

### 5. Migraciones Versionadas
Flyway en lugar de `ddl-auto=update` garantiza que todos los entornos tienen exactamente el mismo esquema, con historial auditable.

### 6. Comunicación Resiliente con Feign
El método `enriquecerVenta()` en `VentaService` envuelve las llamadas Feign en try/catch — si un microservicio está caído, retorna "Desconocido" en lugar de fallar toda la operación.

### 7. Datos Inmutables en Ventas
`precioUnitario` y `fechaVenta` se capturan al momento de la venta y no pueden modificarse. No existe endpoint PUT para ventas — son documentos históricos inmutables.

### 8. Logging Estructurado con SLF4J
Usar `@Slf4j` y los diferentes niveles de log (`INFO`, `WARN`, `ERROR`) en lugar de `System.out.println()`.

### 9. CORS Centralizado en Gateway
En lugar de configurar CORS en cada microservicio individualmente, el Gateway maneja CORS de forma global mediante `globalcors`. Los microservicios **no tienen** `@CrossOrigin` — el header `Access-Control-Allow-Origin` lo agrega únicamente el Gateway, una sola vez.

---

## 🛑 Cómo Detener el Proyecto

Detener contenedores:
```bash
docker compose down
```

Detener y eliminar volúmenes (limpieza completa de datos):
```bash
docker compose down -v
```

Reconstruir un servicio específico sin detener los demás:
```bash
docker compose up --build -d gateway
docker compose up --build -d ventas
```

---

## 🐛 Solución de Problemas Comunes

### Error 503 — Service Unavailable
El Gateway no puede encontrar el microservicio en Eureka. Causas:
1. El microservicio todavía no terminó de arrancar (espera ~30 segundos)
2. Eureka fue reiniciado — los microservicios se re-registran automáticamente en ~30 segundos
3. El microservicio falló al iniciar — verifica con `docker compose logs -f <servicio>`

### Error CORS — `multiple values '*, *'`
Ocurre si algún microservicio tiene `@CrossOrigin(origins = "*")` además del CORS del Gateway, lo que genera el header duplicado. Verifica que ningún controller tenga `@CrossOrigin`. Si reconstruiste antes de quitar la anotación, reconstruye de nuevo:
```bash
docker compose up --build -d productos clientes categorias ventas
```

### Error de base de datos al iniciar
Spring Boot no puede conectarse a MySQL. Verifica que el contenedor MySQL esté healthy:
```bash
docker compose ps
docker compose logs db-ventas
```

### `Unknown database`
Flyway intenta migrar pero la base de datos no existe. El `MYSQL_DATABASE` en docker-compose crea la BD automáticamente. Si falla, entra al contenedor:
```bash
docker exec -it db-ventas mysql -uroot -proot \
  -e "CREATE DATABASE IF NOT EXISTS db_ventas;"
```

### Error: "Port already in use"
Otro proceso está usando el puerto. Cambia el puerto en `application.properties`:
```properties
server.port=8085
```

### Error: "Field ventaService required a bean"
Asegúrate de que la clase Service tiene la anotación `@Service` y está en el paquete correcto para que Spring la detecte.

### Logs en tiempo real de un servicio
```bash
docker compose logs -f ventas
docker compose logs -f gateway
```

---

## 📝 Recursos Adicionales

- [Documentación oficial Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Flyway con Spring Boot y MySQL](https://documentation.red-gate.com/flyway/flyway-cli-and-api/usage/api-java/spring-boot)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/)
- [SLF4J Manual](https://www.slf4j.org/manual.html)
- [RESTful Web Services — HTTP Status Codes](https://developer.mozilla.org/es/docs/Web/HTTP/Status)
- [Spring Initializr](https://start.spring.io/)
- [🎬 Video del curso Fullstack I](https://www.youtube.com/watch?v=WSnnJeqGtOQ)

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT, diseñado para propósitos educativos — **DUOC UC, Ingeniería en Informática, Fullstack I**.
