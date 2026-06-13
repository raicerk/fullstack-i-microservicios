# Microservicios - Guia de levantamiento

Este repositorio contiene una arquitectura de microservicios con:
- Eureka Server
- API Gateway
- Servicio de Productos
- Servicio de Clientes
- 2 bases de datos MySQL (una por servicio)

## Requisitos

Antes de iniciar, asegurate de tener instalado:
- Docker Desktop (o Docker Engine)
- Docker Compose v2 (comando `docker compose`)

## Estructura levantada por Docker Compose

Servicios definidos en `docker-compose.yml`:
- `db-productos` (MySQL)
- `db-clientes` (MySQL)
- `eureka` (puerto 8761)
- `gateway` (puerto 8080)
- `productos`
- `clientes`

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

- Eureka Dashboard: http://localhost:8761
- Gateway: http://localhost:8080

Ruta configurada actualmente en Gateway:
- `Path=/api/v1/**` hacia el servicio de productos

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
