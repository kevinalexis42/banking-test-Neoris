# 🏦 Sistema de Gestión Bancaria - Microservicios

Sistema completo de gestión de clientes y cuentas bancarias desarrollado con arquitectura de microservicios. Permite gestionar clientes, crear cuentas, realizar movimientos (depósitos y retiros) y generar reportes de estado de cuenta.

---

## 📖 ¿Qué es este proyecto?

Este es un sistema bancario completo que permite:

- ✅ **Gestionar clientes**: Crear, consultar, actualizar y eliminar clientes
- ✅ **Gestionar cuentas**: Crear cuentas de ahorro o corriente para los clientes
- ✅ **Realizar movimientos**: Hacer depósitos (créditos) y retiros (débitos) en las cuentas
- ✅ **Generar reportes**: Obtener estados de cuenta en formato JSON o Excel
- ✅ **Validaciones automáticas**: El sistema valida saldos, previene sobregiros y registra todas las transacciones

---

## 🏗️ ¿Cómo está organizado?

El sistema está dividido en **2 microservicios** que trabajan juntos:

### 1. Customer Service (Servicio de Clientes)
**Puerto**: 8080 (Docker) o 8083 (local)

**¿Qué hace?**
- Gestiona la información de **Personas** (nombre, género, identificación, dirección, teléfono)
- Gestiona **Clientes** (que heredan todos los datos de Persona, más contraseña y estado)
- Se comunica con Account Service mediante Kafka cuando se crean, actualizan o eliminan clientes

**Endpoints principales**:
- `POST /api/v1/customers` - Crear un nuevo cliente
- `GET /api/v1/customers` - Ver todos los clientes activos (status = true)
- `GET /api/v1/customers/{id}` - Ver un cliente específico
- `PUT /api/v1/customers/{id}` - Actualizar un cliente (actualización parcial - solo campos enviados)
- `DELETE /api/v1/customers/{id}` - Eliminar un cliente

**Características**:
- ✅ Validación de identificación duplicada al crear cliente
- ✅ Actualización parcial: solo el ID es obligatorio, todos los demás campos son opcionales
- ✅ Solo retorna clientes activos en el listado general

### 2. Account Service (Servicio de Cuentas)
**Puerto**: 8081 (Docker) o 8084 (local)

**¿Qué hace?**
- Gestiona **Cuentas** bancarias (número, tipo, saldo inicial, estado)
- Gestiona **Movimientos** (depósitos y retiros)
- Genera **Reportes** de estado de cuenta
- Recibe notificaciones de Customer Service cuando hay cambios en clientes

**Endpoints principales**:
- `POST /api/v1/accounts` - Crear una cuenta
- `GET /api/v1/accounts` - Ver todas las cuentas
- `GET /api/v1/accounts/{id}` - Ver una cuenta específica
- `GET /api/v1/accounts/customer/{customerId}` - Ver cuentas de un cliente
- `PUT /api/v1/accounts/{id}` - Actualizar una cuenta (actualización parcial - solo account_type, status e initial_balance)
- `DELETE /api/v1/accounts/{id}` - Eliminar una cuenta
- `POST /api/v1/movements` - Realizar un movimiento (depósito o retiro)
- `GET /reports/{client-id}` - Generar estado de cuenta (JSON o Excel)
- `GET /reports/{client-id}/movements` - Listado detallado de movimientos por fecha y usuario

**Características**:
- ✅ Validación de número de cuenta duplicado al crear cuenta
- ✅ Actualización parcial: solo el ID es obligatorio, solo se pueden actualizar account_type, status e initial_balance

---

## 🚀 Inicio Rápido

### Opción 1: Ejecutar con Docker (Más Fácil)

**Paso 1**: Tener Docker instalado y corriendo

**Paso 2**: Abrir una terminal en la carpeta del proyecto y ejecutar:

```bash
docker-compose up -d
```

Esto iniciará automáticamente:
- ✅ 2 bases de datos PostgreSQL (una para cada servicio)
- ✅ Kafka y Zookeeper (para comunicación entre servicios)
- ✅ Kafka UI (interfaz web para ver mensajes)
- ✅ Customer Service (puerto 8080)
- ✅ Account Service (puerto 8081)

**Paso 3**: Esperar unos segundos y verificar que todo esté corriendo:

```bash
docker-compose ps
```

Verificar que los servicios esten con estado "Up" o "healthy".

**Paso 4**: Abrir el navegador e ir a:
- **Swagger UI Customer Service**: http://localhost:8080/swagger-ui.html
- **Swagger UI Account Service**: http://localhost:8081/swagger-ui.html
- **Kafka UI**: http://localhost:8082

Verificar inicio del sistema

---

### Opción 2: Ejecutar desde Local (Para Desarrollo)

**Paso 1**: Inicia Docker (solo la infraestructura):

```bash
docker-compose up -d postgres-customer postgres-account zookeeper kafka kafka-ui
```

**Paso 2**: Compilar los proyectos en IntelliJ (o cualquier IDE):
- Abrir la pestaña **Maven** (lateral derecho)
- Para cada proyecto (`customer-service` y `account-service`):
  - Expandir el proyecto → **Lifecycle**
  - Ejecutar: **clean** y luego **compile**

**Paso 3**: Ejecutar los servicios desde local:
- **Customer Service**: Run → `CustomerServiceApplication` (puerto 8083)
- **Account Service**: Run → `AccountServiceApplication` (puerto 8084)

**Nota**: Los servicios en local usan puertos diferentes (8083 y 8084) para no entrar en conflicto con Docker.

---

## 📚 Documentación Completa

Este README es una guía rápida. Para información más detallada, consulta:

- **[FLUJO_PRUEBAS_EXHAUSTIVO.md](FLUJO_PRUEBAS_EXHAUSTIVO.md)** - Guía paso a paso para probar todas las funcionalidades
- **[VERIFICACION_EXHAUSTIVA_REQUISITOS.md](VERIFICACION_EXHAUSTIVA_REQUISITOS.md)** - Verificación técnica completa
- **[CONFIGURACION_DUAL.md](CONFIGURACION_DUAL.md)** - Cómo ejecutar Docker e IntelliJ simultáneamente
- **[OPENAPI_ESPECIFICACION.md](OPENAPI_ESPECIFICACION.md)** - Cómo acceder a la documentación OpenAPI

---

## 🎯 Funcionalidades Principales

### F1: CRUD Completo ✅

Puedes crear, leer, actualizar y eliminar:
- **Clientes** (`/api/v1/customers`)
- **Cuentas** (`/api/v1/accounts`)
- **Movimientos** (`/api/v1/movements`)

Cada entidad tiene operaciones completas de CRUD con validaciones.

**Actualización Parcial**:
- ✅ **Clientes**: Solo el ID es obligatorio. Todos los campos (name, gender, identification, address, phone, password, status) son opcionales. Solo se actualizan los campos enviados.
- ✅ **Cuentas**: Solo el ID es obligatorio. Solo se pueden actualizar: `account_type`, `status` e `initial_balance`. Todos son opcionales.

**Validaciones de Duplicados**:
- ✅ No se puede crear un cliente con una identificación que ya existe
- ✅ No se puede crear una cuenta con un número de cuenta que ya existe

### F2: Registro de Movimientos ✅

**Reglas importantes**:
- ✅ El valor del movimiento **debe ser mayor que cero**
- ✅ **Débito (retiro)**: Se resta del saldo disponible
- ✅ **Crédito (depósito)**: Se suma al saldo disponible
- ✅ Cada transacción se registra automáticamente con fecha y hora
- ✅ El saldo de la cuenta se actualiza automáticamente

**Ejemplo**:
- Cuenta con saldo: 1000.00
- Retiro de 200.00 → Saldo final: 800.00
- Depósito de 500.00 → Saldo final: 1300.00

### F3: Validación de Saldo Insuficiente ✅

Si intentas retirar más dinero del disponible, el sistema:
- ❌ **Rechaza la operación**
- 📢 Muestra el mensaje: **"Saldo no disponible"**
- 🔒 **No modifica el saldo** de la cuenta
- 📝 **No crea ningún registro** de movimiento

**Ejemplo**:
- Cuenta con saldo: 100.00
- Intentas retirar 500.00 → Error: "Saldo no disponible"
- El saldo sigue siendo 100.00

### F4: Reportes de Estado de Cuenta ✅

Puedes generar reportes que muestran:
- 📊 Todas las cuentas de un cliente
- 👤 Nombre del cliente en cada fila
- 💰 Saldo inicial (antes del movimiento) y saldo disponible (después del movimiento)
- 📝 El detalle de todos los movimientos en un rango de fechas

**Formatos disponibles**:
- **JSON**: Para integración con otros sistemas
- **Excel**: Para análisis y presentación (generado en memoria, descarga directa)

**Formato del reporte**:
- Retorna un **array plano** de movimientos (no agrupado por cuenta)
- Cada fila incluye: Fecha, Cliente (nombre), Número Cuenta, Tipo, Saldo Inicial, Estado, Valor movimiento, Tipo Movimiento, Saldo Disponible
- Muestra **TODAS las cuentas** del cliente, incluso las que no tienen movimientos en el rango de fechas
- El saldo inicial se calcula automáticamente (saldo antes del movimiento)
- El saldo disponible es el saldo después del movimiento
- Los movimientos están ordenados por fecha (más recientes primero)

**Endpoints disponibles**:
- `GET /reports/{client-id}?startDate={fecha}&endDate={fecha}&format={json|excel}` - Estado de cuenta en JSON o Excel
- `GET /reports/{client-id}/movements?startDate={fecha}&endDate={fecha}` - Listado detallado de movimientos

**Ejemplo de uso**:
```
GET /reports/1?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&format=json
GET /reports/1?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&format=excel
GET /reports/1/movements?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
```

### F5: Pruebas Unitarias ✅

El sistema incluye pruebas unitarias que verifican:
- ✅ Creación exitosa de movimientos
- ✅ Validación de saldo insuficiente
- ✅ Validación de valores mayores que cero

### F6: Pruebas de Integración ✅

Incluye pruebas que verifican el funcionamiento completo:
- ✅ Creación de cuentas y movimientos
- ✅ Persistencia en base de datos
- ✅ Actualización de saldos

### F7: Despliegue en Contenedores ✅

Todo el sistema puede ejecutarse con Docker:
- ✅ Un solo comando inicia todo
- ✅ Bases de datos configuradas automáticamente
- ✅ Comunicación entre servicios funcionando

---

## 📋 Casos de Uso de Ejemplo

### Caso 1: Crear un Cliente

**Paso 1**: Crear el cliente "Jose Lema"

```bash
POST http://localhost:8080/api/v1/customers
Content-Type: application/json

{
    "person": {
    "name": "Jose Lema",
    "gender": "M",
      "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785"
    },
  "password": "1234",
    "status": true
}
```

**Respuesta esperada**: Cliente creado con un ID (por ejemplo: `{"id": 1, ...}`)

**Guarda este ID** para los siguientes pasos.

---

### Caso 2: Crear una Cuenta para el Cliente

**Paso 2**: Crear cuenta de ahorro para Jose Lema

```bash
POST http://localhost:8081/api/v1/accounts
Content-Type: application/json

{
  "account_number": "478758",
  "account_type": "Ahorro",
  "initial_balance": 2000.00,
    "status": true,
  "customer_id": 1
}
```

**Respuesta esperada**: Cuenta creada con ID y saldo inicial de 2000.00

**Guarda este ID de cuenta** para realizar movimientos.

---

### Caso 3: Realizar un Retiro

**Paso 3**: Retirar 575.00 de la cuenta

```bash
POST http://localhost:8081/api/v1/movements
Content-Type: application/json

{
  "account_id": 1,
  "movement_type": "DEBIT",
  "value": 575.00
}
```

**Qué sucede**:
- ✅ El movimiento se crea exitosamente
- ✅ El saldo de la cuenta cambia de 2000.00 a **1425.00**
- ✅ Se registra la transacción con fecha y hora

---

### Caso 4: Realizar un Depósito

**Paso 4**: Depositar 600.00 en la cuenta

```bash
POST http://localhost:8081/api/v1/movements
Content-Type: application/json

{
  "account_id": 1,
  "movement_type": "CREDIT",
  "value": 600.00
}
```

**Qué sucede**:
- ✅ El movimiento se crea exitosamente
- ✅ El saldo de la cuenta cambia de 1425.00 a **2025.00**

---

### Caso 5: Intentar Retirar Más de lo Disponible

**Paso 5**: Intentar retirar 5000.00 (más del saldo disponible)

```bash
POST http://localhost:8081/api/v1/movements
Content-Type: application/json

{
  "account_id": 1,
  "movement_type": "DEBIT",
  "value": 5000.00
}
```

**Qué sucede**:
- ❌ La operación se rechaza
- 📢 Mensaje de error: **"Saldo no disponible"**
- 🔒 El saldo NO cambia (sigue siendo 2025.00)
- 📝 NO se crea ningún movimiento

---

### Caso 6: Generar Estado de Cuenta

**Paso 6**: Obtener el reporte de todas las cuentas y movimientos del cliente

**En formato JSON**:
```bash
GET http://localhost:8081/reports/1?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&format=json
```

**En formato Excel**:
```bash
GET http://localhost:8081/reports/1?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&format=excel
```

**Listado detallado de movimientos**:
```bash
GET http://localhost:8081/reports/1/movements?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
```

**Qué obtienes**:
- 📊 Array plano de movimientos de todas las cuentas del cliente
- 👤 Nombre del cliente en cada fila
- 💰 Saldo inicial (antes del movimiento) y saldo disponible (después del movimiento)
- 📝 Historial completo con: fecha, cliente, número cuenta, tipo, saldo inicial, estado, valor movimiento, tipo movimiento, saldo disponible
- 📋 Todas las cuentas aparecen, incluso las que no tienen movimientos en el rango

---

## 🔧 Configuración de Puertos

### Cuando usas Docker:
- **Customer Service**: http://localhost:8080
- **Account Service**: http://localhost:8081
- **Kafka UI**: http://localhost:8082

### Cuando usas IntelliJ:
- **Customer Service**: http://localhost:8083
- **Account Service**: http://localhost:8084

**Nota**: Puedes ejecutar Docker e IntelliJ simultáneamente sin conflictos. Ver [CONFIGURACION_DUAL.md](CONFIGURACION_DUAL.md) para más detalles.

---

## 🗄️ Bases de Datos

El sistema usa **2 bases de datos PostgreSQL**:

### Customer Database
- **Puerto**: 5432
- **Nombre**: `customerdb`
- **Tablas**: `persons`, `customers`
- **Usuario**: `postgres`
- **Contraseña**: `postgres`

### Account Database
- **Puerto**: 5433
- **Nombre**: `accountdb`
- **Tablas**: `accounts`, `movements`
- **Usuario**: `postgres`
- **Contraseña**: `postgres`

**Para conectarte con DBeaver o cualquier cliente SQL**, usa estos puertos y credenciales. Ver [CONFIGURACION_DBEAVER.md](CONFIGURACION_DBEAVER.md) para instrucciones detalladas.

---

## 📖 Documentación de la API

### Swagger UI (Interfaz Visual)

Una vez que los servicios estén corriendo, puedes acceder a la documentación interactiva:

- **Customer Service**: http://localhost:8080/swagger-ui.html
- **Account Service**: http://localhost:8081/swagger-ui.html

Desde aquí puedes:
- ✅ Ver todos los endpoints disponibles
- ✅ Probar los endpoints directamente desde el navegador
- ✅ Ver ejemplos de requests y responses
- ✅ Entender qué parámetros necesita cada endpoint

### OpenAPI (Especificación Técnica)

Para obtener la especificación técnica en formato JSON o YAML:

- **Customer Service JSON**: http://localhost:8080/api-docs
- **Customer Service YAML**: http://localhost:8080/api-docs.yaml
- **Account Service JSON**: http://localhost:8081/api-docs
- **Account Service YAML**: http://localhost:8081/api-docs.yaml

Ver [OPENAPI_ESPECIFICACION.md](OPENAPI_ESPECIFICACION.md) para más detalles.

---

## 🧪 Cómo Probar el Sistema

### Opción 1: Usar Swagger UI (Integrado)

1. Abrir http://localhost:8080/swagger-ui.html o http://localhost:8081/swagger-ui.html
2. Expandir cualquier endpoint
3. Click en "Try it out"
4. Completar los datos
5. Click en "Execute"
6. Ver la respuesta

### Opción 2: Usar Postman

1. Importar el archivo `Postman_Collection.json` en Postman
2. Seleccionar cualquier request
3. Ajustar los datos si es necesario
4. Click en "Send"
5. Ver la respuesta

### Opción 3: Usar curl (Terminal)

Ejemplos de comandos curl están en la sección "Casos de Uso" más arriba.

---

## 🏛️ Estructura del Proyecto

```
SkillTest/
│
├── 📁 customer-service/          # Microservicio de Clientes
│   ├── src/main/java/           # Código fuente
│   │   └── org/example/customerservice/
│   │       ├── controller/      # Endpoints REST
│   │       ├── service/         # Lógica de negocio
│   │       ├── repository/      # Acceso a base de datos
│   │       ├── entity/          # Entidades (Person, Customer)
│   │       ├── dto/             # Objetos de transferencia
│   │       ├── mapper/          # Conversión entre entidades y DTOs
│   │       └── exception/       # Manejo de errores
│   ├── src/main/resources/
│   │   ├── application.yml      # Configuración
│   │   └── schema.sql           # Esquema de base de datos
│   └── src/test/                # Pruebas
│
├── 📁 account-service/          # Microservicio de Cuentas
│   ├── src/main/java/          # Código fuente
│   │   └── org/example/accountservice/
│   │       ├── controller/      # Endpoints REST
│   │       ├── service/         # Lógica de negocio
│   │       ├── repository/     # Acceso a base de datos
│   │       ├── entity/          # Entidades (Account, Movement)
│   │       └── ...
│   └── src/test/                # Pruebas
│
├── 📄 docker-compose.yml        # Configuración de Docker
├── 📄 BaseDatos.sql            # Script completo de base de datos
├── 📄 Postman_Collection.json  # Colección de pruebas para Postman
│
└── 📚 Documentación/
    ├── README.md                # Este archivo
    └── ... (más documentos)
```

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.2.5** - Framework principal de Java
- **Spring WebFlux** - Para programación reactiva
- **R2DBC** - Para acceso reactivo a base de datos
- **PostgreSQL** - Base de datos relacional

### Comunicación
- **Apache Kafka** - Para comunicación asíncrona entre microservicios
- **Reactor Kafka** - Versión reactiva de Kafka

### Herramientas de Desarrollo
- **Lombok** - Reduce código repetitivo
- **MapStruct** - Convierte automáticamente entre entidades y DTOs
- **OpenAPI/Swagger** - Documentación automática de la API

### Pruebas
- **JUnit 5** - Framework de pruebas
- **Mockito** - Para crear mocks en pruebas unitarias
- **Testcontainers** - Para pruebas de integración con contenedores reales

### Despliegue
- **Docker** - Contenedores para bases de datos y servicios
- **Docker Compose** - Orquestación de todos los servicios

---

## ✅ Características Técnicas Implementadas

### Programación Reactiva ✅
- ✅ Todo el código usa programación reactiva (no bloqueante)
- ✅ Los endpoints retornan `Mono` o `Flux` (tipos reactivos)
- ✅ Las bases de datos se acceden de forma reactiva
- ✅ Kafka se usa de forma reactiva
- ✅ **Resultado**: El sistema puede manejar muchas peticiones simultáneas sin bloquearse

### Arquitectura Limpia ✅
- ✅ Separación clara de responsabilidades
- ✅ Controladores solo manejan HTTP
- ✅ Servicios contienen la lógica de negocio
- ✅ Repositorios solo acceden a datos
- ✅ DTOs para transferencia de datos
- ✅ Mappers para conversiones

### Manejo de Errores ✅
- ✅ Manejo global de excepciones
- ✅ Mensajes de error claros y estructurados
- ✅ Códigos HTTP apropiados (400, 404, 500, etc.)
- ✅ Logging de todos los errores

### Validaciones ✅
- ✅ Validación automática de datos de entrada
- ✅ Validaciones de negocio (saldo insuficiente, valores positivos, etc.)
- ✅ Mensajes de error descriptivos

### Seguridad y Buenas Prácticas ✅
- ✅ Inyección de dependencias por constructor
- ✅ Logging en puntos clave
- ✅ Código en inglés
- ✅ Documentación completa

---

## 📊 Endpoints Completos

### Customer Service (`http://localhost:8080`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Crear un nuevo cliente |
| GET | `/api/v1/customers` | Obtener todos los clientes |
| GET | `/api/v1/customers/{id}` | Obtener un cliente por ID |
| PUT | `/api/v1/customers/{id}` | Actualizar un cliente |
| DELETE | `/api/v1/customers/{id}` | Eliminar un cliente |

### Account Service (`http://localhost:8081`)

#### Cuentas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Crear una nueva cuenta |
| GET | `/api/v1/accounts` | Obtener todas las cuentas |
| GET | `/api/v1/accounts/{id}` | Obtener una cuenta por ID |
| GET | `/api/v1/accounts/customer/{customerId}` | Obtener cuentas de un cliente |
| PUT | `/api/v1/accounts/{id}` | Actualizar una cuenta |
| DELETE | `/api/v1/accounts/{id}` | Eliminar una cuenta |

#### Movimientos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/movements` | Crear un movimiento (débito o crédito) |
| GET | `/api/v1/movements` | Obtener todos los movimientos |
| GET | `/api/v1/movements/{id}` | Obtener un movimiento por ID |
| GET | `/api/v1/movements/account/{accountId}` | Obtener movimientos de una cuenta |
| PUT | `/api/v1/movements/{id}` | Actualizar un movimiento |
| DELETE | `/api/v1/movements/{id}` | Eliminar un movimiento |

#### Reportes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reports/{client-id}?startDate={fecha}&endDate={fecha}&format={json\|excel}` | Generar estado de cuenta (JSON o Excel) |
| GET | `/reports/{client-id}/movements?startDate={fecha}&endDate={fecha}` | Listado detallado de movimientos por fecha y usuario |

**Parámetros del reporte**:
- `client-id`: ID del cliente (en la URL)
- `startDate`: Fecha de inicio (formato: `2025-01-01T00:00:00`)
- `endDate`: Fecha de fin (formato: `2025-12-31T23:59:59`)
- `format`: `json` o `excel` (opcional, por defecto es `json`)

---

## 🔍 Códigos de Respuesta HTTP

El sistema usa los siguientes códigos HTTP:

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| **200 OK** | Operación exitosa | Al obtener o actualizar recursos |
| **201 CREATED** | Recurso creado | Al crear un cliente, cuenta o movimiento |
| **204 NO_CONTENT** | Eliminación exitosa | Al eliminar un recurso |
| **400 BAD_REQUEST** | Error en los datos | Datos inválidos, validaciones fallidas, saldo insuficiente |
| **404 NOT_FOUND** | Recurso no encontrado | Cuando buscas un ID que no existe |
| **500 INTERNAL_SERVER_ERROR** | Error del servidor | Errores inesperados |

---

## 🧪 Ejecutar Pruebas

### Pruebas Unitarias

```bash
# Customer Service
cd customer-service
mvn test

# Account Service
cd account-service
mvn test
```

### Pruebas de Integración

```bash
# Account Service (incluye prueba de integración)
cd account-service
mvn test
```

**Nota**: Las pruebas de integración requieren Docker corriendo.

---

## 🐳 Comandos Docker Útiles

### Iniciar todo
```bash
docker-compose up -d
```

### Ver estado de los servicios
```bash
docker-compose ps
```

### Ver logs
```bash
# Todos los servicios
docker-compose logs -f

# Solo un servicio
docker-compose logs -f customer-service
docker-compose logs -f account-service
```

### Detener todo
```bash
docker-compose down
```

### Detener y eliminar volúmenes (borra las bases de datos)
```bash
docker-compose down -v
```

### Reconstruir imágenes (si cambiaste código)
```bash
docker-compose build
docker-compose up -d
```

---

## 📝 Scripts y Archivos Importantes

### BaseDatos.sql
Script completo que contiene:
- ✅ Esquema de ambas bases de datos
- ✅ Creación de tablas
- ✅ Índices para optimización
- ✅ Datos de ejemplo basados en los casos de uso

**Ubicación**: `BaseDatos.sql` (en la raíz del proyecto)

### Postman_Collection.json
Colección completa de pruebas para Postman con:
- ✅ Todos los endpoints de Customer Service
- ✅ Todos los endpoints de Account Service
- ✅ Ejemplos de requests
- ✅ Casos de prueba (incluyendo errores)

**Cómo usar**:
1. Abre Postman
2. File → Import
3. Selecciona `Postman_Collection.json`
4. ¡Listo para probar!

---

## 🎓 Conceptos Importantes

### ¿Qué es Programación Reactiva?

La programación reactiva permite que el sistema maneje muchas peticiones simultáneamente sin bloquearse. En lugar de esperar a que termine una operación antes de empezar otra, el sistema puede procesar múltiples operaciones al mismo tiempo.

**Ventajas**:
- ✅ Mejor rendimiento
- ✅ Puede manejar más usuarios simultáneamente
- ✅ Uso más eficiente de recursos

### ¿Qué es R2DBC vs JPA?

- **JPA**: Es bloqueante (espera a que termine la operación de base de datos)
- **R2DBC**: Es reactivo (no bloquea, permite hacer otras cosas mientras espera)

**Por qué usamos R2DBC**: Porque el proyecto requiere programación reactiva (WebFlux), y JPA no es compatible con reactivo. R2DBC es la elección correcta y necesaria.

### ¿Qué es Kafka?

Kafka es un sistema de mensajería que permite que los microservicios se comuniquen entre sí de forma asíncrona (sin esperar respuesta inmediata).

**En este proyecto**:
- Customer Service envía eventos cuando se crean/actualizan/eliminan clientes
- Account Service recibe estos eventos y puede reaccionar (por ejemplo, eliminar cuentas si se elimina un cliente)

---

