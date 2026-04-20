# 🔌 Integración con Sistemas Externos

Documento técnico para la integración con sistemas externos de personas y vehículos.

---

## 1. Resumen del Flujo

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO GENERAL                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   FRONTEND/APP                                                  │
│        │                                                       │
│        │ GET /api/v1/externo/persona?tipoDocumento=CC...       │
│        ▼                                                       │
│   EXTERNO CONTROLLER                                           │
│        │                                                       │
│        ▼                                                       │
│   [ enabled? ] ──false──► retorna {encontrado: false}          │
│        │                                                       │
│       true                                                     │
│        ▼                                                       │
│   EXTERNAL CLIENT                                              │
│        │                                                       │
│        ▼                                                       │
│   REST TEMPLATE ──llamada HTTP────► SISTEMA EXTERNO             │
│        │                                                       │
│        ▼                                                       │
│   EXTERNAL DATA (DTO del externo)                               │
│        │                                                       │
│        ▼                                                       │
│   MAPPER (transformación)                                       │
│        │                                                       │
│        ▼                                                       │
│   RESPONSE DTO (formato interno)                                │
│        │                                                       │
│        ▼                                                       │
│   FRONTEND/APP                                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Endpoints API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/extero/persona?tipoDocumento=CC&numeroDocumento=12345678` | Buscar persona |
| GET | `/api/v1/externo/vehiculo?placa=ABC123` | Buscar vehículo |

### Request

**Persona:**
```
GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678
```

**Vehículo:**
```
GET /api/v1/externo/vehiculo?placa=ABC123
```

### Response

**Persona - Encontrado:**
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

**Persona - No encontrado:**
```json
{
  "encontrado": false
}
```

**Vehículo - Encontrado:**
```json
{
  "encontrado": true,
  "placa": "ABC123",
  "marca": "TOYOTA",
  "modelo": 2021,
  "referencia": "COROLLA"
}
```

**Vehículo - No encontrado:**
```json
{
  "encontrado": false
}
```

---

## 3. Estructura de Archivos

```
src/main/java/com/parkcontrol/
├── config/
│   ├── ExternalApiProperties.java    ← Lee configuración
│   └── RestTemplateConfig.java       ← Bean REST con timeout
│
├── external/
│   ├── dto/
│   │   ├── ExternalPersonaData.java  ← DTOdelexterno (PERSONA)
│   │   └── ExternalVehiculoData.java ← DTOdelexterno (VEHÍCULO)
│   │
│   ├── mapper/
│   │   ├── PersonaMapper.java        ← Transforma PERSONA
│   │   └── VehiculoMapper.java       ← Transforma VEÍCULO
│   │
│   ├── PersonaExternalClient.java    ← Cliente HTTP para personas
│   └── VehiculoExternalClient.java   ← Cliente HTTP para vehículos
│
├── common/dto/externo/
│   ├── PersonaExternoResponse.java   ← DTO interno (respuesta API)
│   └── VehiculoExternoResponse.java  ← DTO interno (respuesta API)
│
└── controller/
    └── ExternoController.java       ← Endpoints REST
```

---

## 4. Configuración

###application.yml

```yaml
app:
  externo:
    # URLs de los sistemas externos
    persona-url: http://localhost:9999/api/personas
    vehiculo-url: http://localhost:9999/api/vehiculos
    
    # Timeout en milisegundos
    timeout: 5000
    
    # Habilitar/deshabilitar cada consulta
    # false = retorna {encontrado: false} sin llamar al externo
    persona-enabled: true
    vehiculo-enabled: true
```

### Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `app.externo.persona-url` | URL del API de personas | `http://localhost:9999/api/personas` |
| `app.externo.vehiculo-url` | URL del API de vehículos | `http://localhost:9999/api/vehiculos` |
| `app.externo.timeout` | Timeout en ms | `5000` |
| `app.externo.persona-enabled` | Habilitar consulta de personas | `true` |
| `app.extero.vehiculo-enabled` | Habilitar consulta de vehículos | `true` |

---

## 5. DTOs: Externo vs Interno

### 5.1 Persona

| Campo Interno (API) | Campo Externo (a mapear) | Tipo |
|---------------------|--------------------------|------|
| `encontrado` | (calculado) | boolean |
| `nombres` | `nombreCompleto` | String |
| `tipoDocumento` | `tipoIdentificacion` | String |
| `numeroDocumento` | `numeroIdentificacion` | String |
| `correo` | `email` | String |
| `telefono` | `celular` | String |
| `sexo` | `genero` | String |
| `fechaNacimiento` | `fechaNacimiento` | String |

### 5.2 Vehículo

| Campo Interno (API) | Campo Externo (a mapear) | Tipo |
|---------------------|--------------------------|------|
| `encontrado` | (calculado) | boolean |
| `placa` | `placaVehiculo` | String |
| `marca` | `marcaVehiculo` | String |
| `modelo` | `anioVehiculo` | Integer |
| `referencia` | `lineaVehiculo` | String |

---

## 6. cosas que FALTAN por Definir

### 6.1 Formato Real del Sistema Externo

**⚠️ IMPORTANTE:** Los DTOs externos actuales son un ejemplo genérico. Cuando nos den la URL real y el response, hay que revisar:

1. **URL exacta** - puede tener otro formato de query params
2. **Nombres de campos** - pueden ser diferentes a los definidos
3. **Tipos de datos** - puede haber diferencias
4. **Campo de éxito** - puede usar `success`, `status`, `found`, etc.

### 6.2 Cosas por Confirmar con el Equipo Externo

| # | Pregunta | Archivo a Cambiar |
|---|---------|-------------------|
| 1 | ¿Cuál es la URL exacta del endpoint de personas? | `application.yml` |
| 2 | ¿Cuál es la URL exacta del endpoint de vehículos? | `application.yml` |
| 3 | ¿Qué campos retorna el endpoint de personas? | `ExternalPersonaData.java` |
| 4 | ¿Qué campos retorna el endpoint de vehículos? | `ExternalVehiculoData.java` |
| 5 | ¿Cómo indica el API que la consulta fue exitosa? | `*Mapper.java` |
| 6 | ¿Necesita autenticación (API key, token)? | `RestTemplateConfig.java` |
| 7 | ¿El response es un objeto o un array? | `*ExternalClient.java` |

---

## 7. Guía de Implementación cuando TENGAMOS los datos del Externo

### Paso 1: Definir los DTOs del Externo

Editar `external/dto/ExternalPersonaData.java`:

```java
// CAMBIAR según el formato REAL del externo
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ExternalPersonaData {
    
    // AJUSTAR según los nombres reales del JSON
    private String id;
    private String nombreCompleto;      // ← puede ser "name", "nombre", etc.
    private String tipoIdentificacion;  // ← puede ser "tipoDoc", "docType", etc.
    private String numeroIdentificacion;
    private String email;               // ← puede ser "mail", "correo", etc.
    private String celular;             // ← puede ser "phone", "telefono", etc.
    private String genero;              // ← puede ser "gender", "sexo", etc.
    private String fechaNacimiento;
    private Boolean success;            // ← puede ser "found", "status", etc.
    private String mensaje;
}
```

### Paso 2: Ajustar el Mapper

Editar `external/mapper/PersonaMapper.java`:

```java
public PersonaExternoResponse mapToResponse(ExternalPersonaData data) {
    if (data == null) {
        return PersonaExternoResponse.builder()
                .encontrado(false)
                .build();
    }

    // Verificar si la respuesta es válida
    // AJUSTAR según cómo el externo indica éxito
    boolean esValido = (data.getSuccess() != null && data.getSuccess())
            || data.getNumeroIdentificacion() != null;

    if (!esValido) {
        return PersonaExternoResponse.builder()
                .encontrado(false)
                .build();
    }

    // MAPEAR los campos
    return PersonaExternoResponse.builder()
            .encontrado(true)
            // CAMBIAR: nombre del campo externo → campo interno
            .nombres(data.getNombreCompleto())
            .tipoDocumento(data.getTipoIdentificacion())
            .numeroDocumento(data.getNumeroIdentificacion())
            .correo(data.getEmail())
            .telefono(data.getCelular())
            .sexo(data.getGenero())
            .fechaNacimiento(data.getFechaNacimiento())
            .build();
}
```

### Paso 3: Verificar la URL

Editar `application.yml` con las URLs reales:

```yaml
app:
  externo:
    persona-url: http://API-REAL/personas/buscar
    vehiculo-url: http://API-REAL/vehiculos/buscar
```

### Paso 4: Probar

Llamar los endpoints:
- `GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678`
- `GET /api/v1/externo/vehiculo?placa=ABC123`

---

## 8. Casos de Uso

### 8.1 Persona Encontrada

```
Request: GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678

Respuesta Externa:
{
  "id": "1",
  "nombreCompleto": "Juan Pérez",
  "tipoIdentificacion": "CC",
  "numeroIdentificacion": "12345678",
  "email": "juan@email.com",
  "celular": "3001234567",
  "genero": "MASCULINO",
  "success": true
}

Response API:
{
  "encontrado": true,
  "nombres": "Juan Pérez",
  "tipoDocumento": "CC",
  "numeroDocumento": "12345678",
  "correo": "juan@email.com",
  "telefono": "3001234567",
  "sexo": "MASCULINO",
  "fechaNacimiento": null
}
```

### 8.2 Persona No Encontrada

```
Request: GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=99999999

Respuesta Externa:
{
  "success": false,
  "mensaje": "Persona no encontrada"
}

Response API:
{
  "encontrado": false
}
```

### 8.3 Sistema Externo Deshabilitado

```
app.externo.persona-enabled: false

Request: GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678

Response API:
{
  "encontrado": false
}
```

### 8.4 Error de Conexión

```
Request: GET /api/v1/externo/persona?tipoDocumento=CC&numeroDocumento=12345678

Error: Connection timeout o Connection refused

Response API (FALLBACK SEGURO):
{
  "encontrado": false
}

El sistema nunca fallahard - siempre retorna no encontrado
```

---

## 9. Consideraciones Importantes

### 9.1 Seguridad en el Fallback

- **Si el externo está deshabilitado** → retorna no encontrado
- **Si hay error de conexión** → retorna no encontrado
- **Si el externo retorna vacío** → retorna no encontrado

Esto permite que el sistema siga funcionando con entrada manual.

### 9.2 Timeout

- Timeout configurado: 5 segundos
- Si el externo no responde en 5s, se considera error y retorna no encontrado

### 9.3 Logging

- Cada llamada se loguea en nivel DEBUG: URL consumida
- Si hay error, se loguea en nivel WARN: mensaje de error
- Si hay éxito, se loguea en nivel INFO: placa/documento consultado

### 9.4 Autenticación

**⚠️ FALTAA IMPLEMENTAR:** El sistema actualmenteNo tiene soporte paraAuthentication headers.

Si el sistema externo requiere:
- API Key → agregar header
- Bearer Token → agregar header
- Basic Auth → agregar header

**Dónde agregar:** `RestTemplateConfig.java` o crear un `ClientHttpRequestInterceptor`

---

## 10. Pendientes / TO-DO

| # | Item | Prioridad | Estado |
|---|------|----------|--------|
| 1 | Obtener URLs reales del equipo externo | ALTA | ⏳ |
| 2 | Obtener formato JSON del response de personas | ALTA | ⏳ |
| 3 | Obtener formato JSON del response de vehículos | ALTA | ⏳ |
| 4 | Confirmar si necesita autenticación | MEDIA | ⏳ |
| 5 | Implementar autenticación si es necesaria | MEDIA | ⏳ |
| 6 | Ajustar mapeos según datos reales | ALTA | ⏳ |
| 7 | Pruebas de integración con sistema real | ALTA | ⏳ |

---

## 11. Uso desde el Ingreso

El `RegistrarIngresoUseCase` debe usar estos clientes para:

```java
// 1. Buscar persona antes de crear
PersonaExternoResponse persona = personaClient.buscarPorDocumento(
    request.tipoDocumentoConductor(),
    request.numeroDocumentoConductor()
);

if (persona.isEncontrado()) {
    // Usar datos del externo
} else {
    // Crear persona con datos manuales del request
}
```

```java
// 2. Buscar vehículo antes de crear
VehiculoExternoResponse vehiculo = vehiculoClient.buscarPorPlaca(
    request.placa()
);

if (vehiculo.isEncontrado()) {
    // Usar datos del externo
} else {
    // Crear vehículo con datos manuales del request
}
```

---

*Documento creado: 2026-04-20*
*Versión: 1.0.0*