# Especificación OpenAPI

## Acceso a las Especificaciones

El proyecto utiliza SpringDoc OpenAPI para generar automáticamente la documentación y especificación OpenAPI.

### Customer Service

#### Con Docker (Puerto 8080)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

#### Local (Puerto 8083)
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/api-docs
- **OpenAPI YAML**: http://localhost:8083/api-docs.yaml

### Account Service

#### Con Docker (Puerto 8081)
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs
- **OpenAPI YAML**: http://localhost:8081/api-docs.yaml

#### Local (Puerto 8084)
- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8084/api-docs
- **OpenAPI YAML**: http://localhost:8084/api-docs.yaml

## Descarga de Especificaciones

### Con Docker (Puertos 8080 y 8081)

```bash
# Customer Service - JSON
curl http://localhost:8080/api-docs > customer-service-openapi.json

# Customer Service - YAML
curl http://localhost:8080/api-docs.yaml > customer-service-openapi.yaml

# Account Service - JSON
curl http://localhost:8081/api-docs > account-service-openapi.json

# Account Service - YAML
curl http://localhost:8081/api-docs.yaml > account-service-openapi.yaml
```

### Local (Puertos 8083 y 8084)

```bash
# Customer Service - JSON
curl http://localhost:8083/api-docs > customer-service-openapi.json

# Customer Service - YAML
curl http://localhost:8083/api-docs.yaml > customer-service-openapi.yaml

# Account Service - JSON
curl http://localhost:8084/api-docs > account-service-openapi.json

# Account Service - YAML
curl http://localhost:8084/api-docs.yaml > account-service-openapi.yaml
```

## Información de las APIs

### Customer Service API
- **Título**: Customer Service API
- **Versión**: 1.0.0
- **Descripción**: API for managing customers and persons

### Account Service API
- **Título**: Account Service API
- **Versión**: 1.0.0
- **Descripción**: API for managing accounts and movements

## Endpoints Documentados

### Customer Service

1. **POST /api/v1/customers** - Crear cliente
2. **GET /api/v1/customers** - Listar todos los clientes
3. **GET /api/v1/customers/{id}** - Obtener cliente por ID
4. **PUT /api/v1/customers/{id}** - Actualizar cliente
5. **DELETE /api/v1/customers/{id}** - Eliminar cliente

### Account Service

1. **POST /api/v1/accounts** - Crear cuenta
2. **GET /api/v1/accounts** - Listar todas las cuentas
3. **GET /api/v1/accounts/{id}** - Obtener cuenta por ID
4. **GET /api/v1/accounts/customer/{customerId}** - Obtener cuentas por cliente
5. **PUT /api/v1/accounts/{id}** - Actualizar cuenta
6. **DELETE /api/v1/accounts/{id}** - Eliminar cuenta
7. **POST /api/v1/movements** - Crear movimiento
8. **GET /api/v1/movements** - Listar todos los movimientos
9. **GET /api/v1/movements/{id}** - Obtener movimiento por ID
10. **GET /api/v1/movements/account/{accountId}** - Obtener movimientos por cuenta
11. **PUT /api/v1/movements/{id}** - Actualizar movimiento
12. **DELETE /api/v1/movements/{id}** - Eliminar movimiento
13. **GET /reports/{client-id}** - Generar estado de cuenta

## Resumen de Puertos

| Servicio | Docker | Local | Swagger UI (Docker) | Swagger UI (Local) |
|----------|--------|----------|---------------------|----------------------|
| Customer Service | 8080 | 8083 | http://localhost:8080/swagger-ui.html | http://localhost:8083/swagger-ui.html |
| Account Service | 8081 | 8084 | http://localhost:8081/swagger-ui.html | http://localhost:8084/swagger-ui.html |

**Nota**: Se puede ejecutar tanto en docker como en local simultáneamente sin conflictos. Cada uno usa sus propios puertos.

## Notas

- Las especificaciones se generan automáticamente al iniciar los servicios
- Todas las anotaciones `@Operation`, `@ApiResponse`, `@Parameter` están incluidas en los controladores
- Los DTOs están documentados con validaciones y ejemplos
- La especificación incluye esquemas de datos, códigos de respuesta y ejemplos
- Los puertos varían según cómo ejecutes los servicios (Docker vs Local)

