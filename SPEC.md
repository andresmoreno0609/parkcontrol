# SPEC - MVP ParkControl

## Objetivo del MVP

Desarrollar un sistema de gestión de parqueadero que permita:
1. Registro digital de ingresos de vehículos con inventario
2. Control de salida con inspección y comparación de estado
3. Integración con sistemas externos para datos de personas y vehículos
4. Sistema de evidencias fotográficas
5. Autenticación y control de acceso por roles

---

## 1. Autenticación y Usuarios

### 1.1 Login

**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 - OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "rol": "ADMIN",
  "nombre": "Administrador"
}
```

**Response (401 - Error):**
```json
{
  "error": "Credenciales inválidas"
}
```

**PreConditions (UseCase):**
- Validar que username no esté vacío
- Validar que password no esté vacío
- Verificar que el usuario existe y está ACTIVO
- Verificar que la contraseña coincide (BCrypt)
- Actualizar `ultimo_login` en la BD

---

### 1.2 Cambiar Contraseña

**Endpoint:** `POST /api/v1/auth/cambiar-password`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "passwordActual": "admin123",
  "passwordNuevo": "nuevaPass123"
}
```

**Response (200 - OK):**
```json
{
  "mensaje": "Contraseña actualizada correctamente"
}
```

**PreConditions:**
- Validar que passwordActual es correcta
- Validar que passwordNuevo tiene mínimo 6 caracteres
- Actualizar password_hash en BD

---

## 2. Gestión de Usuarios (Solo ADMIN)

### 2.1 Crear Usuario

**Endpoint:** `POST /api/v1/usuarios`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "username": "operario01",
  "password": "pass123",
  "rol": "OPERARIO",
  "personaId": 5
}
```

**Response (201 - Created):**
```json
{
  "id": 3,
  "username": "operario01",
  "rol": "OPERARIO",
  "estado": "ACTIVO"
}
```

**PreConditions:**
- Verificar que el token tiene rol ADMIN
- Validar que username no existe ya
- Validar que password tiene mínimo 6 caracteres
- Validar que rol sea ADMIN u OPERARIO
- Validar que personaId existe
- Crear usuario con estado ACTIVO

---

### 2.2 Listar Usuarios

**Endpoint:** `GET /api/v1/usuarios`

**Headers:** `Authorization: Bearer {token}`

**Query Params:**
- `page` (default: 0)
- `size` (default: 10)
- `estado` (optional: ACTIVO, INACTIVO)

**Response (200 - OK):**
```json
{
  "content": [
    { "id": 1, "username": "admin", "rol": "ADMIN", "estado": "ACTIVO", "ultimoLogin": "2026-04-17T10:30:00" },
    { "id": 2, "username": "operario01", "rol": "OPERARIO", "estado": "ACTIVO", "ultimoLogin": null }
  ],
  "totalElements": 2,
  "totalPages": 1
}
```

---

### 2.3 Cambiar Estado Usuario

**Endpoint:** `PATCH /api/v1/usuarios/{id}/estado`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "estado": "INACTIVO"
}
```

**PreConditions:**
- Verificar que el token tiene rol ADMIN
- Validar que el usuario a modificar no sea el mismo que hace la petición (no puede desactivarse a sí mismo)
- Actualizar estado

---

## 3. Consultas a Sistemas Externos

### 3.1 Buscar Persona por Documento

**Endpoint:** `GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678`

**Response (200 - Encontrado):**
```json
{
  "encontrado": true,
  "nombres": "Juan Pérez",
  "tipoDocumento": "CC",
  "numeroDocumento": "12345678",
  "correo": "juan@email.com",
  "telefono": "3001234567",
  "sexo": "MASCULINO",
  "fechaNacimiento": "1990-05-15"
}
```

**Response (200 - No encontrado):**
```json
{
  "encontrado": false
}
```

**PreConditions:**
- Validar tipoDocumento no vacío
- Validar numeroDocumento no vacío
- Consumir sistema externo (HTTP)
- Si hay error en el externo, retornar encontrado: false (fallback)

---

### 3.2 Buscar Vehículo por Placa

**Endpoint:** `GET /api/v1/externo/vehiculo?placa=ABC123`

**Response (200 - Encontrado):**
```json
{
  "encontrado": true,
  "placa": "ABC123",
  "marca": "TOYOTA",
  "modelo": 2021,
  "referencia": "COROLLA"
}
```

**Response (200 - No encontrado):**
```json
{
  "encontrado": false
}
```

**PreConditions:**
- Validar que placa no esté vacía
- Consumir sistema externo (HTTP)
- Si hay error, retornar encontrado: false

---

## 4. Gestión de Tipos de Evidencia

### 4.1 Listar Tipos de Evidencia

**Endpoint:** `GET /api/v1/tipos-evidencia`

**Response (200 - OK):**
```json
[
  { "id": 1, "nombre": "Foto Frontal", "descripcion": "Vista frontal del vehículo", "orden": 1 },
  { "id": 2, "nombre": "Foto Lateral Izquierda", "descripcion": "Lado izquierdo", "orden": 2 },
  { "id": 3, "nombre": "Foto Lateral Derecha", "descripcion": "Lado derecho", "orden": 3 },
  { "id": 4, "nombre": "Foto Trasera", "descripcion": "Vista trasera", "orden": 4 },
  { "id": 5, "nombre": "Foto Interior", "descripcion": "Interior del vehículo", "orden": 5 },
  { "id": 6, "nombre": "Foto Daños", "descripcion": "Evidencia de daños", "orden": 6 },
  { "id": 7, "nombre": "Foto Documentos", "descripcion": "Documentos del vehículo", "orden": 7 }
]
```

---

### 4.2 Crear Tipo de Evidencia (Solo ADMIN)

**Endpoint:** `POST /api/v1/tipos-evidencia`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "nombre": "Foto Panorámica",
  "descripcion": "Vista panorámica del vehículo",
  "orden": 8
}
```

**Response (201 - Created):**
```json
{
  "id": 8,
  "nombre": "Foto Panorámica",
  "descripcion": "Vista panorámica del vehículo",
  "orden": 8,
  "activo": true
}
```

---

## 5. Ingreso de Vehículos

### 5.1 Registrar Ingreso

**Endpoint:** `POST /api/v1/ingresos`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "placa": "ABC123",
  "tipoVehiculo": "AUTOMOVIL",
  "marca": "TOYOTA",
  "modelo": 2021,
  "referencia": "COROLLA",
  "color": "BLANCO",
  "servicio": "PARTICULAR",

  "tipoDocumentoConductor": "CC",
  "numeroDocumentoConductor": "12345678",

  "tipoAcceso": "HORA",
  "cupoAsignado": "A-15",
  "kilometraje": "45000",
  "motivoIngreso": "Trabajo",
  "observaciones": "Cliente frecuente",

  "firmaConductor": true,
  "firmaOperario": true,

  "inventarioExterior": {
    "parachoquesDelantero": false,
    "parachoquesTrasero": false,
    "puertas": false,
    "espejos": false,
    "vidrios": false,
    "luces": false,
    "llantas": false,
    "rayones": true,
    "golpes": false,
    "observaciones": "Rayón en puerta trasera izquierda"
  },

  "inventarioInterior": {
    "tapiceria": false,
    "tablero": false,
    "radioPantalla": false,
    "alfombras": true,
    "cinturones": false,
    "elementosPersonales": false,
    "observaciones": "Alfombra manchada"
  },

  "inventarioSeguridad": {
    "llantaRepuesto": true,
    "gato": true,
    "cruceta": true,
    "extintor": false,
    "botiquin": true,
    "triangulos": true,
    "herramientas": true,
    "otros": null
  },

  "evidencias": [
    { "tipoId": 1, "rutaArchivo": "/uploads/ingreso/001_frente_20260417.jpg" },
    { "tipoId": 2, "rutaArchivo": "/uploads/ingreso/001_lateral_izq_20260417.jpg" }
  ]
}
```

**Response (201 - Created):**
```json
{
  "id": 1,
  "numeroRegistro": "IN-20260417-0001",
  "placa": "ABC123",
  "fechaIngreso": "2026-04-17",
  "horaIngreso": "14:30:00",
  "tipoAcceso": "HORA",
  "cupoAsignado": "A-15",
  "estado": "ACTIVO",
  "vehiculo": {
    "id": 1,
    "placa": "ABC123",
    "marca": "TOYOTA",
    "modelo": 2021,
    "referencia": "COROLLA"
  },
  "conductor": {
    "id": 1,
    "nombres": "Juan Pérez",
    "tipoDocumento": "CC",
    "numeroDocumento": "12345678"
  },
  "usuarioRegistro": "operario01"
}
```

**PreConditions:**
- Verificar token válido
- Buscar persona en sistema externo (RN-11, RN-12)
  - Si encuentra → usar datos del externo
  - Si NO encuentra → buscar/crear persona con datos manuales
- Buscar vehículo en sistema externo (RN-17, RN-18)
  - Si encuentra → usar datos
  - Si NO encuentra → buscar/crear vehículo con datos manuales
- Validar campos obligatorios: placa, tipoVehiculo, marca, modelo, tipoDocumentoConductor, numeroDocumentoConductor, tipoAcceso, firmaConductor, firmaOperario
- Validar que inventario tiene todos los checkitems (9 exterior, 6 interior, 8 seguridad)
- Generar numeroRegistro único: `IN-{fecha}-{secuencia}`
- Crear ingresos con estado ACTIVO
- Registrar inventario (3 tablas)
- Registrar evidencias (opcional)
- Registrar usuario que hace la operación

---

### 5.2 Buscar Ingreso Activo por Placa

**Endpoint:** `GET /api/v1/ingresos/activo?placa=ABC123`

**Headers:** `Authorization: Bearer {token}`

**Response (200 - OK):**
```json
{
  "id": 1,
  "numeroRegistro": "IN-20260417-0001",
  "placa": "ABC123",
  "fechaIngreso": "2026-04-17",
  "horaIngreso": "14:30:00",
  "tipoAcceso": "HORA",
  "cupoAsignado": "A-15",
  "estado": "ACTIVO"
}
```

**Response (404 - No hay ingreso activo):**
```json
{
  "error": "No existe ingreso activo para la placa ABC123"
}
```

---

### 5.3 Listar Ingresos

**Endpoint:** `GET /api/v1/ingresos`

**Headers:** `Authorization: Bearer {token}`

**Query Params:**
- `page` (default: 0)
- `size` (default: 10)
- `placa` (optional)
- `estado` (optional: ACTIVO, EXITO, CANCELADO)
- `fechaDesde` (optional: YYYY-MM-DD)
- `fechaHasta` (optional: YYYY-MM-DD)

**Response (200 - OK):**
```json
{
  "content": [
    {
      "id": 1,
      "numeroRegistro": "IN-20260417-0001",
      "placa": "ABC123",
      "fechaIngreso": "2026-04-17",
      "horaIngreso": "14:30:00",
      "tipoAcceso": "HORA",
      "estado": "ACTIVO"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 5.4 Obtener Detalle de Ingreso

**Endpoint:** `GET /api/v1/ingresos/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (200 - OK):**
```json
{
  "id": 1,
  "numeroRegistro": "IN-20260417-0001",
  "placa": "ABC123",
  "fechaIngreso": "2026-04-17",
  "horaIngreso": "14:30:00",
  "tipoAcceso": "HORA",
  "cupoAsignado": "A-15",
  "kilometraje": "45000",
  "motivoIngreso": "Trabajo",
  "observaciones": "Cliente frecuente",
  "firmaConductor": true,
  "firmaOperario": true,
  "estado": "ACTIVO",
  "vehiculo": { ... },
  "conductor": { ... },
  "inventarioExterior": { ... },
  "inventarioInterior": { ... },
  "inventarioSeguridad": { ... },
  "evidencias": [ ... ],
  "usuarioRegistro": "operario01",
  "createdAt": "2026-04-17T14:30:00"
}
```

---

## 6. Salida de Vehículos

### 6.1 Registrar Salida

**Endpoint:** `POST /api/v1/salidas`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "ingresoId": 1,
  "personaRetira": "Juan Pérez",
  "documentoRetira": "12345678",
  "observaciones": "Cliente满意的",

  "inventarioExterior": {
    "parachoquesDelantero": false,
    "parachoquesTrasero": false,
    "puertas": false,
    "espejos": false,
    "vidrios": false,
    "luces": false,
    "llantas": false,
    "rayones": true,
    "golpes": false,
    "observaciones": "Mismo rayón del ingreso"
  },

  "inventarioInterior": {
    "tapiceria": false,
    "tablero": false,
    "radioPantalla": false,
    "alfombras": false,
    "cinturones": false,
    "elementosPersonales": false,
    "observaciones": "Todo OK"
  },

  "inventarioSeguridad": {
    "llantaRepuesto": true,
    "gato": true,
    "cruceta": true,
    "extintor": false,
    "botiquin": true,
    "triangulos": true,
    "herramientas": true,
    "otros": null
  },

  "evidencias": [
    { "tipoId": 4, "rutaArchivo": "/uploads/salida/001_trasera_20260417.jpg" }
  ]
}
```

**Response (201 - Created):**
```json
{
  "id": 1,
  "ingresoId": 1,
  "fechaSalida": "2026-04-17",
  "horaSalida": "18:45:00",
  "personaRetira": "Juan Pérez",
  "documentoRetira": "12345678",
  "montoPagado": 5000.00,
  "estadoEntrega": "ENTREGADO",
  "observaciones": "Cliente满意的"
}
```

**PreConditions:**
- Verificar token válido
- Validar que ingresoId existe y estado es ACTIVO
- Validar que personaRetira y documentoRetira no estén vacíos
- Registrar checklist de salida (comparar con ingreso)
- Calcular tiempo y monto (RN-15: tiempo completo, sin importar salida temprana)
- Actualizar estado del ingreso a EXITO
- Crear registro de salida
- Registrar evidencias de salida (opcional)
- Registrar usuario que hace la operación

---

### 6.2 Obtener Detalle de Salida

**Endpoint:** `GET /api/v1/salidas/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (200 - OK):**
```json
{
  "id": 1,
  "ingresoId": 1,
  "numeroRegistro": "IN-20260417-0001",
  "placa": "ABC123",
  "fechaIngreso": "2026-04-17",
  "horaIngreso": "14:30:00",
  "fechaSalida": "2026-04-17",
  "horaSalida": "18:45:00",
  "personaRetira": "Juan Pérez",
  "documentoRetira": "12345678",
  "tiempoTotal": "4h 15m",
  "montoPagado": 5000.00,
  "observaciones": "Cliente满意的",
  "evidencias": [ ... ],
  "usuarioRegistro": "operario01"
}
```

---

## 7. Tarifas

### 7.1 Listar Tarifas

**Endpoint:** `GET /api/v1/tarifas`

**Response (200 - OK):**
```json
[
  { "id": 1, "tipoVehiculo": "AUTOMOVIL", "tipoAcceso": "HORA", "valor": 2500, "activo": true },
  { "id": 2, "tipoVehiculo": "AUTOMOVIL", "tipoAcceso": "DIARIO", "valor": 15000, "activo": true },
  { "id": 3, "tipoVehiculo": "MOTO", "tipoAcceso": "HORA", "valor": 1500, "activo": true },
  { "id": 4, "tipoVehiculo": "MOTO", "tipoAcceso": "DIARIO", "valor": 8000, "activo": true }
]
```

---

### 7.2 Crear/Actualizar Tarifa (Solo ADMIN)

**Endpoint:** `POST /api/v1/tarifas`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "tipoVehiculo": "AUTOMOVIL",
  "tipoAcceso": "MENSUAL",
  "valor": 350000
}
```

**Response (201 - Created):**
```json
{
  "id": 5,
  "tipoVehiculo": "AUTOMOVIL",
  "tipoAcceso": "MENSUAL",
  "valor": 350000,
  "activo": true
}
```

---

## 8. Códigos de Error Estándar

| Código | Significado |
|--------|-------------|
| 400 | Bad Request - Request inválido |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error inesperado |

---

## 9. Endpoints Resumen

| # | Método | Endpoint | Rol | Descripción |
|---|--------|----------|-----|-------------|
| 1 | POST | /api/v1/auth/login | ALL | Login |
| 2 | POST | /api/v1/auth/cambiar-password | ALL | Cambiar contraseña |
| 3 | POST | /api/v1/usuarios | ADMIN | Crear usuario |
| 4 | GET | /api/v1/usuarios | ADMIN | Listar usuarios |
| 5 | PATCH | /api/v1/usuarios/{id}/estado | ADMIN | Cambiar estado usuario |
| 6 | GET | /api/v1/externo/persona | ALL | Buscar persona (sistema externo) |
| 7 | GET | /api/v1/externo/vehiculo | ALL | Buscar vehículo (sistema externo) |
| 8 | GET | /api/v1/tipos-evidencia | ALL | Listar tipos de evidencia |
| 9 | POST | /api/v1/tipos-evidencia | ADMIN | Crear tipo evidencia |
| 10 | POST | /api/v1/ingresos | OPERARIO | Registrar ingreso |
| 11 | GET | /api/v1/ingresos/activo | OPERARIO | Buscar ingreso activo por placa |
| 12 | GET | /api/v1/ingresos | ALL | Listar ingresos |
| 13 | GET | /api/v1/ingresos/{id} | ALL | Detalle ingreso |
| 14 | POST | /api/v1/salidas | OPERARIO | Registrar salida |
| 15 | GET | /api/v1/salidas/{id} | ALL | Detalle salida |
| 16 | GET | /api/v1/tarifas | ALL | Listar tarifas |
| 17 | POST | /api/v1/tarifas | ADMIN | Crear tarifa |

---

*Documento basado en docs/negocio.md y docs/database.md*
*Versión: 1.0.0 - 2026-04-17*