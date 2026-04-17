# 🗄️ Base de Datos - ParkControl

---

## 1. Modelo Entidad-Relación

```
┌─────────────────┐       ┌─────────────────┐
│    USUARIO      │       │    PERSONA      │
├─────────────────┤       ├─────────────────┤
│ id (BIGINT, PK) │       │ id (BIGINT, PK) │
│ username        │       │ nombres         │
│ password_hash   │       │ tipo_documento  │
│ rol (ADMIN/OPER)│       │ numero_documento│
│ persona_id (FK) │───────│ correo          │
│ estado          │       │ telefono        │
│ ultimo_login    │       │ sexo            │
│ created_at      │       │ fecha_nacimiento│
└─────────────────┘       │ created_at      │
         │                └─────────────────┘
         │                         │
         │                         ▼
         │                ┌─────────────────┐
         │                │    VEHICULO     │
         │                ├─────────────────┤
         │                │ id (BIGINT, PK) │
         │                │ placa (UK)       │
         │                │ marca            │
         │                │ modelo           │
         │                │ referencia       │
         │                │ color            │
         │                │ tipo             │
         │                │ servicio         │
         │                │ created_at       │
         │                └─────────────────┘
         │                         │
         │                         ▼
┌─────────────────────────────────────────────┐
│                  INGRESO                     │
├─────────────────────────────────────────────┤
│ id (BIGINT, PK)                                    │
│ vehiculo_id (FK → Vehiculo)                 │
│ persona_id (FK → Persona)                   │
│ numero_registro (unique)                    │
│ fecha_ingreso                                │
│ hora_ingreso                                 │
│ tipo_acceso (HORA/DIARIO/MENSUAL/CONVENIO)  │
│ cupo_asignado                                │
│ kilometraje (opcional)                      │
│ motivo_ingreso (opcional)                   │
│ observaciones (opcional)                     │
│ firma_conductor (boolean)                   │
│ firma_operario (boolean)                     │
│ estado (ACTIVO/EXITO/CANCELADO)             │
│ usuario_registro                             │
│ fecha_creacion                               │
└─────────────────────────────────────────────┘
            │
            ├──┬────────────────┬──────────────┐
            │  │                │              │
            ▼  ▼                ▼              ▼
┌──────────────────┐ ┌──────────────┐ ┌─────────────────┐
│ INVENTARIO_      │ │ INVENTARIO_   │ │ EVIDENCIA_      │
│ EXTERIOR         │ │ INTERIOR      │ │ INGRESO         │
├──────────────────┤ ├──────────────┤ ├─────────────────┤
│ id (BIGINT, PK)        │ │ id (BIGINT, PK)    │ │ id (BIGINT, PK)       │
│ ingreso_id (FK)  │ │ ingreso_id   │ │ ingreso_id (FK) │
│ parachoques_d    │ │ tapiceria    │ │ tipo_id (FK)    │
│ parachoques_t    │ │ tablero      │ │ ruta_archivo    │
│ puertas          │ │ radio        │ │ fecha_creacion  │
│ espejos          │ │ alfombras    │ │                 │
│ vidrios          │ │ cinturones   │ │                 │
│ luces            │ │ elem_pers    │ │                 │
│ llantas          │ │ observaciones│ │                 │
│ rayones          │ │ fecha_proceso│ │                 │
│ golpes           │ │              │ │                 │
│ observaciones    │ │              │ │                 │
│ fecha_proceso    │ │              │ │                 │
└──────────────────┘ └──────────────┘ └─────────────────┘

┌──────────────────┐
│ INVENTARIO_      │
│ SEGURIDAD        │
├──────────────────┤
│ id (BIGINT, PK)        │
│ ingreso_id (FK)  │
│ llanta_repuesto  │
│ gato             │
│ cruceta          │
│ extintor         │
│ botiquin         │
│ triangulos       │
│ herramientas     │
│ otros            │
│ fecha_proceso    │
└──────────────────┘


┌─────────────────────────────────────────────┐
│                   SALIDA                     │
├─────────────────────────────────────────────┤
│ id (BIGINT, PK)                                    │
│ ingreso_id (FK → Ingreso)                  │
│ fecha_salida                                │
│ hora_salida                                 │
│ persona_retira (nombre)                     │
│ documento_retira                           │
│ observaciones                               │
│ monto_pagado                                │
│ usuario_registro                           │
│ fecha_creacion                              │
└─────────────────────────────────────────────┘
            │
            ▼
┌──────────────────┐
│ EVIDENCIA_       │
│ SALIDA           │
├──────────────────┤
│ id (BIGINT, PK)        │
│ salida_id (FK)   │
│ tipo_id (FK)     │
│ ruta_archivo     │
│ fecha_creacion   │
└──────────────────┘


┌──────────────────┐
│ TIPO_EVIDENCIA   │
├──────────────────┤
│ id (BIGINT, PK)        │
│ nombre           │
│ descripcion      │
│ activo (boolean) │
│ orden            │
│ fecha_creacion   │
└──────────────────┘

┌──────────────────┐
│ TARIFA           │
├──────────────────┤
│ id (BIGINT, PK)        │
│ tipo_vehiculo    │
│ tipo_acceso      │
│ valor            │
│ activo (boolean) │
│ fecha_creacion   │
└──────────────────┘
```

---

## 2. Esquema de Tablas

### 2.1 persona

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| nombres | VARCHAR(255) | NO | | Nombres completos |
| tipo_documento | VARCHAR(20) | NO | | Tipo: CC, CE, NIT, etc. |
| numero_documento | VARCHAR(50) | NO | INDEX | Número de documento |
| correo | VARCHAR(100) | YES | | Correo electrónico |
| telefono | VARCHAR(20) | YES | | Teléfono de contacto |
| sexo | VARCHAR(20) | YES | | MASCULINO, FEMENINO, OTRO |
| fecha_nacimiento | DATE | YES | | Fecha de nacimiento (opcional) |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_persona_numero_documento` ON numero_documento

---

### 2.2 vehiculo

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| placa | VARCHAR(10) | NO | UNIQUE | Placa del vehículo |
| marca | VARCHAR(50) | NO | | Marca: BMW, Toyota, etc. |
| modelo | INTEGER | NO | | Año del vehículo |
| referencia | VARCHAR(100) | YES | | Línea/versión: Serie 3, Corolla |
| color | VARCHAR(30) | YES | | Color del vehículo |
| tipo | VARCHAR(20) | NO | | AUTOMOVIL, CAMIONETA, MOTO, CAMION, OTRO |
| servicio | VARCHAR(20) | NO | DEFAULT 'PARTICULAR' | PARTICULAR, PUBLICO, OFICIAL |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_vehiculo_placa` ON placa (UNIQUE)

---

### 2.3 usuario

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| username | VARCHAR(50) | NO | UNIQUE | Nombre de usuario para login |
| password_hash | VARCHAR(255) | NO | | Hash de la contraseña (BCrypt) |
| rol | VARCHAR(20) | NO | | ADMIN, OPERARIO |
| persona_id | BIGINT | NO | FK → persona.id | Persona asociada al usuario |
| estado | VARCHAR(20) | NO | 'ACTIVO' | ACTIVO, INACTIVO |
| ultimo_login | TIMESTAMP | YES | | Fecha del último login |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_usuario_username` ON username (UNIQUE)
- `idx_usuario_estado` ON estado

---

### 2.4 ingreso

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| vehiculo_id | BIGINT | NO | FK → vehiculo.id | Vehículo asociado |
| persona_id | BIGINT | NO | FK → persona.id | Conductor/Propietario |
| numero_registro | VARCHAR(20) | NO | UNIQUE | Número único de registro |
| fecha_ingreso | DATE | NO | | Fecha de ingreso |
| hora_ingreso | TIME | NO | | Hora de ingreso |
| tipo_acceso | VARCHAR(20) | NO | | HORA, DIARIO, MENSUAL, CONVENIO |
| cupo_asignado | VARCHAR(20) | YES | | Cupo/espacio asignado |
| kilometraje | VARCHAR(20) | YES | | Kilometraje (opcional) |
| motivo_ingreso | VARCHAR(255) | YES | | Motivo del ingreso |
| observaciones | TEXT | YES | | Observaciones generales |
| firma_conductor | BOOLEAN | NO | FALSE | Firma del conductor |
| firma_operario | BOOLEAN | NO | FALSE | Firma del operario |
| estado | VARCHAR(20) | NO | 'ACTIVO' | ACTIVO, EXITO, CANCELADO |
| usuario_registro | VARCHAR(50) | NO | | Usuario que registra |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_ingreso_numero_registro` ON numero_registro (UNIQUE)
- `idx_ingreso_fecha` ON fecha_ingreso
- `idx_ingreso_estado` ON estado
- `idx_ingreso_vehiculo` ON vehiculo_id

---

### 2.5 inventario_exterior

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| ingreso_id | BIGINT | NO | FK → ingreso.id | Ingreso asociado |
| parachoques_delantero | BOOLEAN | NO | FALSE | Parachoques_delantero OK |
| parachoques_trasero | BOOLEAN | NO | FALSE | Parachoques_trasero OK |
| puertas | BOOLEAN | NO | FALSE | Puertas OK |
| espejos | BOOLEAN | NO | FALSE | Espejos OK |
| vidrios | BOOLEAN | NO | FALSE | Vidrios OK |
| luces | BOOLEAN | NO | FALSE | Luces OK |
| llantas | BOOLEAN | NO | FALSE | Llantas OK |
| rayones | BOOLEAN | NO | FALSE | Rayones visibles OK |
| golpes | BOOLEAN | NO | FALSE | Golpes visibles OK |
| observaciones | TEXT | YES | | Observaciones exterior |
| fecha_proceso | TIMESTAMP | NO | NOW() | Fecha del checklist |

**Índices:**
- `idx_inv_ext_ingreso` ON ingreso_id

---

### 2.6 inventario_interior

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| ingreso_id | BIGINT | NO | FK → ingreso.id | Ingreso asociado |
| tapiceria | BOOLEAN | NO | FALSE | Tapicería OK |
| tablero | BOOLEAN | NO | FALSE | Tablero OK |
| radio_pantalla | BOOLEAN | NO | FALSE | Radio/Pantalla OK |
| alfombras | BOOLEAN | NO | FALSE | Alfombras OK |
| cinturones | BOOLEAN | NO | FALSE | Cinturones OK |
| elementos_personales | BOOLEAN | NO | FALSE | Elementos personales OK |
| observaciones | TEXT | YES | | Observaciones interior |
| fecha_proceso | TIMESTAMP | NO | NOW() | Fecha del checklist |

**Índices:**
- `idx_inv_int_ingreso` ON ingreso_id

---

### 2.7 inventario_seguridad

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| ingreso_id | BIGINT | NO | FK → ingreso.id | Ingreso asociado |
| llanta_repuesto | BOOLEAN | NO | FALSE | Llanta de repuesto OK |
| gato | BOOLEAN | NO | FALSE | Gato OK |
| cruceta | BOOLEAN | NO | FALSE | Cruceta OK |
| extintor | BOOLEAN | NO | FALSE | Extintor OK |
| botiquin | BOOLEAN | NO | FALSE | Botiquín OK |
| triangulos | BOOLEAN | NO | FALSE | Triángulos OK |
| herramientas | BOOLEAN | NO | FALSE | Herramientas OK |
| otros | VARCHAR(100) | YES | | Otros elementos |
| fecha_proceso | TIMESTAMP | NO | NOW() | Fecha del checklist |

**Índices:**
- `idx_inv_seg_ingreso` ON ingreso_id

---

### 2.8 evidencia_ingreso

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| ingreso_id | BIGINT | NO | FK → ingreso.id | Ingreso asociado |
| tipo_id | BIGINT | NO | FK → tipo_evidencia.id | Tipo de evidencia |
| ruta_archivo | VARCHAR(500) | NO | | Ruta/URL del archivo |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_evi_ing_ingreso` ON ingreso_id
- `idx_evi_ing_tipo` ON tipo_id

---

### 2.9 salida

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| ingreso_id | BIGINT | NO | FK → ingreso.id | Ingreso asociado |
| fecha_salida | DATE | NO | | Fecha de salida |
| hora_salida | TIME | NO | | Hora de salida |
| persona_retira | VARCHAR(255) | NO | | Nombre de quien retira |
| documento_retira | VARCHAR(50) | NO | | Documento de quien retira |
| observaciones | TEXT | YES | | Observaciones de salida |
| monto_pagado | DECIMAL(10,2) | YES | | Monto total pagado |
| usuario_registro | VARCHAR(50) | NO | | Usuario que registra |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_salida_ingreso` ON ingreso_id (UNIQUE)

---

### 2.10 evidencia_salida

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| salida_id | BIGINT | NO | FK → salida.id | Salida asociada |
| tipo_id | BIGINT | NO | FK → tipo_evidencia.id | Tipo de evidencia |
| ruta_archivo | VARCHAR(500) | NO | | Ruta/URL del archivo |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_evi_sal_salida` ON salida_id
- `idx_evi_sal_tipo` ON tipo_id

---

### 2.11 tipo_evidencia

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| nombre | VARCHAR(50) | NO | | Nombre del tipo |
| descripcion | VARCHAR(255) | YES | | Descripción |
| activo | BOOLEAN | NO | TRUE | Si está disponible |
| orden | INTEGER | NO | DEFAULT 0 | Orden en selector |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_tipo_evi_activo` ON activo

---

### 2.12 tarifa

| Campo | Tipo | Nullable | Default | Descripción |
|-------|------|----------|---------|-------------|
| id | BIGINT | NO | PK | Identificador único (autoIncrement) |
| tipo_vehiculo | VARCHAR(20) | NO | | AUTOMOVIL, MOTO, etc. |
| tipo_acceso | VARCHAR(20) | NO | | HORA, DIARIO, MENSUAL |
| valor | DECIMAL(10,2) | NO | | Valor de la tarifa |
| activo | BOOLEAN | NO | TRUE | Si está vigente |
| created_at | TIMESTAMP | NO | NOW() | Fecha de creación |

**Índices:**
- `idx_tarifa_tipos` ON (tipo_vehiculo, tipo_acceso)

---

## 3. Enums (Valores Fijos)

### 3.1 UsuarioRol
- ADMIN
- OPERARIO

### 3.2 TipoVehiculo
- AUTOMOVIL
- CAMIONETA
- MOTO
- CAMION
- OTRO

### 3.3 TipoServicio
- PARTICULAR
- PUBLICO
- OFICIAL

### 3.4 TipoAcceso
- HORA
- DIARIO
- MENSUAL
- CONVENIO

### 3.5 EstadoIngreso
- ACTIVO (vehículo dentro del parqueadero)
- EXITO (vehículo salió)
- CANCELADO (ingreso cancelado)

---

## 4. Observaciones Técnicas

### 4.1 Convenciones de Nombres
- Tablas: snake_case (minúsculas con guión bajo)
- Columnas: snake_case
- FK: `{tabla}_id`
- PK: `id` (BIGINT, autoIncrement)
- Timestamps: `created_at`, `updated_at`

### 4.2 Tipo de Datos para IDs
- Todas las tablas usan `BIGINT` como tipo de ID primario
- Se genera automáticamente con `autoIncrement`
- FK también son `BIGINT`

### 4.3 Evidencias (Archivos)
- En BD solo se guarda la ruta/path
- El archivo físico se almacena en sistema de archivos local o cloud storage
- Naming sugerido: `{ingreso_id}_{tipo}_{timestamp}.{ext}`

---

## 5. Datos Iniciales (Seed)

### 5.1 Usuario Admin (por defecto)

| username | password | rol | persona_id |
|----------|----------|-----|-------------|
| admin | (hash BCrypt de 'admin123') | ADMIN | (FK a persona admin) |

> **Nota:** La contraseña por defecto debe cambiarse en primer inicio de sesión.

### 5.2 Tipos de Evidencia (tipo_evidencia)

| nombre | descripcion | orden |
|--------|-------------|-------|
| Foto Frontal | Vista frontal del vehículo | 1 |
| Foto Lateral Izquierda | Lado izquierdo del vehículo | 2 |
| Foto Lateral Derecha | Lado derecho del vehículo | 3 |
| Foto Trasera | Vista trasera del vehículo | 4 |
| Foto Interior | Interior del vehículo | 5 |
| Foto Daños | Evidencia de daños encontrados | 6 |
| Foto Documentos | Foto de documentos del vehículo | 7 |

---

*Documento basado en reglas de negocio de negocio.md*
*Versión: 1.0.0 - 2026-04-17*