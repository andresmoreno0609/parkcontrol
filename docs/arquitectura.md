# 🏗️ Arquitectura Backend - ParkControl

---

## 1. Estructura General del Proyecto

```
src/main/java/com/parkcontrol/
├── common/                          ← Compartido por todos los módulos
│   ├── entity/                      ← JPA Entities
│   ├── repository/                  ← JpaRepository interfaces
│   ├── service/                     ← Services (incluye BaseCrudService)
│   ├── dto/                         ← DTOs por dominio
│   │   └── {modulo}/
│   │       ├── Request.java         ← DTO de entrada
│   │       └── Response.java        ← DTO de salida
│   ├── usecase/
│   │   └── UseCaseAdvance.java      ← Template para UseCases
│   └── config/                      ← Configuración de Spring
│
├── controller/                      ← Controllers modularizados
│   └── {modulo}/
│       └── {Modulo}Controller.java
│
├── usecase/                         ← UseCases modularizados
│   └── {modulo}/
│       ├── {Accion}UseCase.java
│       └── dto/                     ← Request/Response específicos del use case
│
├── adapter/                         ← Adapters modularizados
│   └── {modulo}/
│       ├── {Modulo}Adapter.java     ← Interfaz
│       └── {Modulo}AdapterImpl.java ← Implementación
│
├── external/                        ← Integraciones con sistemas externos
│   └── {sistema}/
│       ├── {SistemaClient}.java     ← Cliente HTTP
│       └── {SistemaResponse}.java  ← Response del externo
│
└── config/                          ← Configuración de Spring
```

---

## 2. Flujo de una Request (HTTP)

```
HTTP Request
    │
    ▼
┌─────────────────────────────────────┐
│         Controller                  │ ◄── 1. Recibe request
│  - Define endpoint                  │     2. Documenta (OpenAPI)
│  - Valida DTO con @Valid            │     3. Llama al Adapter
│  - Define @RequestMapping           │     
└──────────────┬──────────────────────┘
                │ DTO Request
                ▼
┌─────────────────────────────────────┐
│         Adapter                     │ ◄── 1. Traduce DTO → Request object
│  - Interfaz + Implementación        │     2. Decide: Service o UseCase
│  - Translation layer                │     3. Traduce Response → DTO
└──────────────┬──────────────────────┘
                │
       ┌───────┴───────┐
       ▼               ▼
┌──────────────┐  ┌─────────────────┐
│   Service    │  │    UseCase     │ ◄── UseCase: lógica compleja
│  (CRUD base) │  │ (UseCaseAdvance)│    Service: operaciones básicas
└──────┬───────┘  └────────┬────────┘
       │                    │
       ▼                    ▼
┌─────────────────────────────────────┐
│       Repository                    │ ◄── JpaRepository
│  - Acceso a datos                   │     + JpaSpecificationExecutor
└─────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│         Entity                       │ ◄── JPA Entity (@Entity)
└─────────────────────────────────────┘
```

---

## 3. Convenciones por Capa

### 3.1 Entity (common/entity/)

```java
@Entity
@Table(name = "nombre_tabla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String field;

    @Enumerated(EnumType.STRING)
    private EnumType enumField;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**Consideraciones:**
- Usar `Long` como ID primario con `GenerationType.IDENTITY` (autoIncrement)
- Usar `EnumType.STRING` para enums
- Definir enums internos en la entidad cuando aplican solo a esa tabla
- Usar `@Builder` para construcción flexible
- Incluir `@PrePersist` y `@PreUpdate` para timestamps automáticos

---

### 3.2 Repository (common/repository/)

```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>, JpaSpecificationExecutor<Entity> {
    
    // Query methods
    Optional<Entity> findByField(String field);
    boolean existsByField(String field);
}
```

**Consideraciones:**
- **SIEMPRE** extender `JpaRepository<Entity, Long>` y `JpaSpecificationExecutor<Entity>`
- Usar nombres de método de Spring Data para queries simples
- Para queries complejas, usar `@Query`

---

### 3.3 DTOs (common/dto/{modulo}/)

**Request DTO:**
```java
public record EntityRequest(
    String field,
    Long relationId
) {}
// Nota: NO se usan anotaciones @NotNull, @Size, etc. en los DTOs
// Las validaciones se realizan en las preConditions del UseCase
```

**Response DTO:**
```java
@Builder
public record EntityResponse(
    Long id,
    String field,
    LocalDateTime createdAt
) {}
```

**Consideraciones:**
- Usar **records** para DTOs inmutables
- Nombrar como `{Entidad}Request` y `{Entidad}Response`
- Incluir todos los campos relevantes
- Para responses complejos, crear DTO específico
- **SIEMPRE** usar `@Builder` de Lombok en records
- Uso del builder: `EntityResponse.builder().id(...).field(...).build()`
- **NO usar anotaciones de validación** (@NotNull, @Size, @Email, etc.) en los DTOs Request
- Las validaciones obligatorias y longitudes se hacen en las `preConditions` del UseCase

---

### 3.4 Service (common/service/)

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityService extends BaseCrudService<Entity, EntityRequest, EntityResponse, Long> {

    // Obligatorio: retornar el repository
    @Override
    protected EntityRepository getRepository() {
        return entityRepository;
    }

    // Mapper: Request → Entity
    @Override
    protected Entity toEntity(EntityRequest request) {
        return Entity.builder()
                .field(request.field())
                .build();
    }

    // Mapper: Entity → Response
    @Override
    protected EntityResponse toResponse(Entity entity) {
        return new EntityResponse(
                entity.getId(),
                entity.getField()
        );
    }

    // Update: Request → Entity existente
    @Override
    protected void updateEntity(EntityRequest request, Entity entity) {
        if (request.field() != null) {
            entity.setField(request.field());
        }
    }
}
```

**Consideraciones:**
- Extender `BaseCrudService<Entity, REQ, RES, Long>`
- Usar `@RequiredArgsConstructor` de Lombok
- Implementar los 4 métodos abstractos obligatorios
- Agregar métodos custom específicos del dominio
- No exponer entities directamente en responses

---

### 3.5 UseCase (usecase/{modulo}/)

```java
@Component
@Slf4j
public class AccionUseCase extends UseCaseAdvance<Request, Response> {

    private final EntityService entityService;

    // Validaciones previas
    @Override
    protected void preConditions(Request request) {
        if (request.field() == null) {
            throw new IllegalArgumentException("Field is required");
        }
    }

    // Lógica de negocio principal
    @Override
    protected Response core(Request request) {
        // Orchestración de servicios, transacciones, etc.
        return new Response(...);
    }

    // Side effects post-éxito
    @Override
    protected void postConditions(Response response) {
        // Notificaciones, logs, eventos
    }
}
```

**Consideraciones:**
- Usar `UseCaseAdvance<Request, Response>` como base
- `preConditions`: Validaciones, cargar contexto
- `core`: Lógica principal (OBLIGATORIO)
- `postConditions`: Efectos secundarios (opcional)
- Manejo de excepciones ya incluido en el template
- Para lógica simple, usar Service directamente

---

### 3.6 Adapter (adapter/{modulo}/)

**Interfaz:**
```java
public interface EntityAdapter {
    Response getById(Long id);
    Response create(Request request);
    PageResponse<EntityResponse> search(PageRequest pageRequest);
}
```

**Implementación:**
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityAdapterImpl implements EntityAdapter {

    private final EntityService entityService;
    private final AccionUseCase accionUseCase;

    @Override
    public Response getById(Long id) {
        return entityService.findById(id);
    }

    @Override
    public Response create(Request request) {
        // Decide: usar Service o UseCase
        if (esOperacionSimple(request)) {
            return entityService.create(request);
        } else {
            return accionUseCase.execute(request);
        }
    }
}
```

**Consideraciones:**
- Definir interfaz + implementación
- Traduce DTOs entre capas
- Decide cuándo usar Service vs UseCase
- Centraliza transformación de datos

---

### 3.7 Controller (controller/{modulo}/)

```java
@RestController
@RequestMapping("/api/v1/{recurso}")
@RequiredArgsConstructor
@Slf4j
public class EntityController {

    private final EntityAdapter entityAdapter;

    @PostMapping
    @Operation(summary = "Crear recurso", description = "Crea un nuevo recurso")
    @ApiResponse(responseCode = "201", description = "Recurso creado")
    @ApiResponse(responseCode = "400", description = "Request inválido")
    public ResponseEntity<Response> create(@Valid @RequestBody Request request) {
        Response response = entityAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID")
    public ResponseEntity<Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entityAdapter.getById(id));
    }

    @GetMapping
    @Operation(summary = "Buscar con paginación")
    public ResponseEntity<PageResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(entityAdapter.search(...));
    }
}
```

**Consideraciones:**
- Usar `@Operation` y `@ApiResponse` para documentación OpenAPI
- Delegar TODO al Adapter
- No tener lógica de negocio
- Usar `ResponseEntity` para respuestas HTTP
- Validar con `@Valid` en request body

---

## 4. Cuándo Usar Service vs UseCase

| Escenario | Usar |
|-----------|------|
| CRUD básico (create, update, delete, findById) | **Service** |
| Consulta simple (findAll, existsById) | **Service** |
| Búsqueda con filtros/paginación | **Service** |
| Lógica de negocio compleja | **UseCase** |
| Múltiples operaciones transaccionales | **UseCase** |
| Orchestración de múltiples servicios | **UseCase** |
| Validaciones de negocio complejas | **UseCase** |
| Integración con sistemas externos | **UseCase** |

---

## 5. Integración con Sistemas Externos

### 5.1 Estructura (external/)

```
external/
├── persona/                    ← Sistema externo de personas
│   ├── PersonaClient.java      ← Cliente HTTP (RestTemplate/WebClient)
│   ├── PersonaResponse.java    ← DTO del response externo
│   └── PersonaMapper.java      ← Traduce response externo → DTO interno
│
└── vehiculo/                   ← Sistema externo de vehículos
    ├── VehiculoClient.java
    ├── VehiculoResponse.java
    └── VehiculoMapper.java
```

### 5.2 Ejemplo de Cliente

```java
@Service
@RequiredArgsConstructor
public class PersonaClient {

    private final RestTemplate restTemplate;
    private final ExternalApiProperties properties;

    public Optional<PersonaResponse> buscarPorDocumento(String tipoDoc, String numDoc) {
        try {
            String url = properties.getPersonaUrl() + "/buscar";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + properties.getApiKey());
            
            PersonaRequest request = new PersonaRequest(tipoDoc, numDoc);
            ResponseEntity<PersonaResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                PersonaResponse.class
            );
            
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.warn("Error consultando sistema externo de personas: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
```

**Consideraciones:**
- Usar `Optional` para manejar cuando el sistema externo no retorna datos
- Registrar warnings (no errores) cuando el sistema externo falla
- Fallback: permitir entrada manual
- Timeout en las llamadas HTTP (máx 5 segundos)
- No exponer datos sensibles del sistema externo directamente

---

## 6. Convenciones de Código

### 6.1 Nombrado

| Elemento | Convención | Ejemplo |
|----------|------------|---------|
| Paquetes | lowercase | `com.parkcontrol.common.entity` |
| Clases | PascalCase | `IngresoService` |
| Métodos | camelCase | `findByPlaca()` |
| Variables | camelCase | `ingresoRepository` |
| Constantes | UPPER_SNAKE | `MAX_RETRY` |
| DTOs Records | PascalCase | `IngresoRequest` |
| Enums | PascalCase | `TipoAcceso` |

### 6.2 Anotaciones Obligatorias

```java
// Entity
@Entity @Table @Getter @Setter @Builder

// Service
@Service @Slf4j @RequiredArgsConstructor

// Controller
@RestController @RequestMapping @RequiredArgsConstructor

// Repository
@Repository

// UseCase
@Component @Slf4j

// DTO Records (Response)
@Builder
public record EntityResponse(...) {}
```

### 6.3 Inyección de Dependencias

```java
// ✅ CORRECTO: Constructor con final fields
@Service
@RequiredArgsConstructor
public class IngresoService {
    private final IngresoRepository ingresoRepository;
}

// ❌ INCORRECTO: @Autowired en campo
@Service
public class IngresoService {
    @Autowired
    private IngresoRepository ingresoRepository;
}
```

---

## 7. Consideraciones Importantes

### 7.1 Excepciones
- Usar `IllegalArgumentException` para errores de validación
- `BaseCrudService.EntityNotFoundException` para no encontrado
- No exponer stack traces en producción
- UseCase tiene manejo centralizado de excepciones

### 7.2 Validaciones
- **NO usar** anotaciones de validación en DTOs (`@NotNull`, `@Size`, @Email, etc.)
- Las validaciones obligatorias (campos requeridos) y longitudes se realizan en las `preConditions` del UseCase
- Usar `@Valid` en Controller solo para validar formato del JSON (sintaxis)
- Validaciones de negocio en `preConditions` del UseCase

### 7.3 Transacciones
- `@Transactional` en Service o UseCase
- Por defecto, Propagation.REQUIRED
- No hacer commits manuales

### 7.4 Logs
- Usar SLF4J con `@Slf4j`
- Logs significativos (no "entró", "salió")
- Incluir contexto en errores

### 7.5 Evidencias (Archivos)
- Almacenar en sistema de archivos local o cloud storage
- Guardar solo la ruta/path en la BD
- Nombrar archivos: `{ingreso_id}_{tipo}_{timestamp}.{ext}`
- Validar tamaño máximo (ej: 10MB)

---

## 8. Resumen del Flujo Completo

```
1. Controller recibe HTTP Request
       ↓
2. Valida DTO con @Valid
       ↓
3. Llama al Adapter
       ↓
4. Adapter traduce y decide: Service o UseCase
       ↓
5. Service → Repository → Entity → DB
   UseCase → Orchestación → Services → Repos
   External → Cliente HTTP → Sistema Externo
       ↓
6. Respuesta fluye de vuelta: Entity → Service → Adapter → Controller
       ↓
7. Controller devuelve HTTP Response
```

---

## 9. Estructura de Módulos Propuesta

```
com.parkcontrol/
├── common/
│   ├── entity/
│   │   ├── Persona.java
│   │   ├── Vehiculo.java
│   │   ├── Ingreso.java
│   │   ├── Salida.java
│   │   ├── InventarioExterior.java
│   │   ├── InventarioInterior.java
│   │   ├── InventarioSeguridad.java
│   │   ├── Evidencia.java
│   │   └── TipoEvidencia.java
│   └── dto/
│       ├── persona/
│       ├── vehiculo/
│       ├── ingreso/
│       └── evidencia/
│
├── controller/
│   ├── ingreso/
│   ├── salida/
│   ├── persona/
│   ├── vehiculo/
│   └── evidencia/
│
├── usecase/
│   ├── ingreso/
│   │   ├── RegistrarIngresoUseCase.java
│   │   └── BuscarIngresoUseCase.java
│   └── salida/
│       └── RegistrarSalidaUseCase.java
│
├── adapter/
│   ├── ingreso/
│   └── salida/
│
└── external/
    ├── persona/
    └── vehiculo/
```

---

## ⚠️ IMPORTANTE: Colección de Postman

Todos los endpoints deben documentarse en la colección de Postman:

**Ubicación:** `docs/collections/Postman.json`

**Regla:** Cada vez que se agregue un nuevo endpoint, debe agregarse a esta colección.

**Estructura del archivo:**
```json
{
  "info": { "name": "ParkControl API", "description": "..." },
  "variable": [ { "key": "baseUrl", "value": "http://localhost:8080/api/v1" } ],
  "item": [ /* endpoints aquí */ ]
}
```

Esta colección sirve como:
- Documentación viva de la API
- Pruebas de integración
- Referencia rápida para el equipo

---

## 10. Configuración por Ambiente

### 10.1 Estructura de application.yml

El proyecto usa un único archivo `application.yml` con configuración por perfil:

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

**Perfiles disponibles:**
- `dev` → Desarrollo (H2, logs detallados)
- `prod` → Producción (PostgreSQL, logs reducidos)

### 10.2 Configuración por Perfil

| Configuración | dev | prod |
|---------------|-----|------|
| Base de datos | H2 en memoria | PostgreSQL (variables env) |
| JPA ddl-auto | update | validate |
| show-sql | true | false |
| Level logs | DEBUG | WARN |
| Puerto | 8080 | SERVER_PORT env |
| Console H2 | enabled | disabled |

### 10.3 Variables de Entorno (prod)

```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://host:5432/db
DB_USER=usuario
DB_PASSWORD=password
SERVER_PORT=8080
EXTERNO_PERSONA_URL=http://api.external/personas
EXTERNO_VEHICULO_URL=http://api.external/vehiculos
UPLOAD_PATH=/var/parkcontrol/uploads
```

### 10.4 Banner de Inicio

Al iniciar la aplicación se muestra un banner ASCII con:
- Nombre del proyecto (grande)
- Versión
- Perfil activo (DEV / PROD)
- Advertencia visual según el ambiente

### 10.5 Logs

**Desarrollo:**
- Nivel DEBUG detallado
- SQL formateado con parámetros
- Trace de bindings de Hibernate

**Producción:**
- Nivel WARN/INFO
- Solo errores y advertencias en consola
- Logs a archivo (logs/parkcontrol.log)
- Rotación: 10MB máx, 30 días

---

*Documento basado en arquitectura genérica Spring Boot*
*Adaptado para ParkControl*

---

*Documento generado: 2026-04-17*
*Versión: 1.0.0*