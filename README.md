# Microservicios - Guia de levantamiento

Este repositorio contiene una arquitectura de microservicios con:
- Eureka Server (registro y descubrimiento de servicios)
- API Gateway (punto de entrada único)
- Servicio de Productos
- Servicio de Clientes
- Servicio de Categorías
- 3 bases de datos MySQL (una por servicio)

## Requisitos

Antes de iniciar, asegurate de tener instalado:
- Docker Desktop (o Docker Engine)
- Docker Compose v2 (comando `docker compose`)

## Estructura del proyecto

```
microservicios/
├── eureka/         # Eureka Server - registro de servicios
├── gateway/        # API Gateway - enrutamiento centralizado
├── productos/      # Microservicio de productos (puerto 8082)
├── clientes/       # Microservicio de clientes (puerto 8081)
├── categorias/     # Microservicio de categorías (puerto 8083)
├── ventas/         # Microservicio de ventas (puerto 8084)
└── docker-compose.yml
```

## Servicios levantados por Docker Compose

| Servicio        | Descripción              | Puerto |
|-----------------|--------------------------|--------|
| `db-productos`  | MySQL para productos     | -      |
| `db-clientes`   | MySQL para clientes      | -      |
| `db-ventas`     | MySQL para ventas        | -      |
| `eureka`        | Eureka Server            | 8761   |
| `gateway`       | API Gateway              | 8080   |
| `productos`     | Microservicio productos  | -      |
| `clientes`      | Microservicio clientes   | -      |
| `categorias`    | Microservicio categorías | -      |
| `ventas`        | Microservicio ventas     | -      |

## Como levantar el proyecto

1. Ir a la raiz del repositorio (carpeta microservicios).
	Si ya estas en esa carpeta, puedes saltar este paso.

```bash
cd microservicios
```

2. Construir e iniciar todos los servicios:

```bash
docker compose up --build -d
```

3. Verificar estado de contenedores:

```bash
docker compose ps
```

4. Ver logs en tiempo real (opcional):

```bash
docker compose logs -f
```

## URLs utiles

| Recurso                | URL                                      |
|------------------------|------------------------------------------|
| Eureka Dashboard       | http://localhost:8761                    |
| Swagger UI (agregado)  | http://localhost:8080/swagger-ui.html    |
| API Productos          | http://localhost:8080/api/v1/productos   |
| API Clientes           | http://localhost:8080/api/v1/clientes    |
| API Ventas             | http://localhost:8080/api/v1/ventas      |

## Swagger UI

Todos los microservicios están documentados con Swagger y se acceden desde un único punto a través del API Gateway:

```
http://localhost:8080/swagger-ui.html
```

Usa el **selector desplegable** en la parte superior derecha para cambiar entre:
- `productos`
- `clientes`
- `categorias`

## Rutas configuradas en el Gateway

| Ruta                     | Microservicio |
|--------------------------|---------------|
| `/api/v1/productos/**`   | PRODUCTOS     |
| `/api/v1/clientes/**`    | CLIENTES      |
| `/api/v1/categorias/**`  | CATEGORIAS    |

## Como detener el proyecto

Detener contenedores:

```bash
docker compose down
```

Detener y eliminar volumenes (limpieza completa de datos locales):

```bash
docker compose down -v
```

## Notas

- Si cambias configuraciones o Dockerfiles, vuelve a ejecutar `docker compose up --build -d`.
- Si un servicio no levanta, revisa `docker compose logs -f <nombre-servicio>`.
- El microservicio de **productos** valida que la categoría exista consultando el microservicio de **categorias** antes de guardar o actualizar un producto. Asegúrate de que categorias esté corriendo.
- Espera ~30 segundos tras el inicio para que todos los servicios se registren en Eureka antes de usar el gateway.
