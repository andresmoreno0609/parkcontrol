# Negocio — Parqueadero

## Contexto General

Parqueadero es un sistema de gestión para parquesaderos en Colombia que permite el registro digital de ingresos de vehículos, control de inventarios de estado (exterior, interior, seguridad) y asignación de cupos. El sistema reemplaza los formatos físicos en papel por un flujo digital que garantiza trazabilidad, reduce errores y facilita la consulta de información.

El modelo actual se basa en un formulario de ingresos que incluye datos del vehículo, del conductor/propietario, y un checklist de inventario dividido en tres categorías: exterior, interior y seguridad. Ambos (conductor y operador) firman como responsables del estado registrado.

**Integración externa**: El sistema se alimenta de una aplicación externa vía HTTP que proporciona datos de la persona (conductor/propietario). Si la persona existe en el sistema externo, se auto-completa la información; si no existe, el operario debe digitar los datos manualmente.

---

## Objetivo

Proveer un sistema digital que permita:

1. **Registrar ingresos de vehículos** con validación de placa única por fecha
2. **Gestionar inventarios de estado** mediante checklists estructurados (exterior, interior, seguridad)
3. **Asignar cupos** según tipo de acceso (hora, diario, mensual, convenio)
4. **Mantener trazabilidad** mediante registro de fecha/hora y responsables (conductor y operario)
5. **Integrarse con sistemas externos** para obtener datos de personas
6. **Controlar la salida** con verificación de estado vs. ingreso

---

## Actores

| Actor | Descripción |
|-------|-------------|
| **Administrador** | Gestiona configuración del parqueadero, tarifas, usuarios del sistema, reportes. Tiene acceso total. |
| **Operario** | Empleado del parqueadero que registra el ingreso, ejecuta el checklist de inventario, asigna el cupo y registra la salida. Acceso limitado a operaciones del día. |
| **Conductor/Propietario** | Cliente que entrega el vehículo, proporciona datos personales (via sistema externo o manual) y firma como responsable del estado |
| **Sistema Externo** | Aplicación externa que proporciona datos de personas y vehículos via HTTP |

---

## Usuarios del Sistema

El sistema tiene su propia tabla de usuarios para autenticación y control de acceso.

### Roles

| Rol | Descripción | Permisos |
|-----|-------------|-----------|
| **ADMIN** | Administrador del sistema | Gestionar usuarios, configuraciones, tarifas, reportes, todas las operaciones |
| **OPERARIO** | Operario del parqueadero | Registrar ingresos, salidas,查看 reportes básicos |

### Datos del Usuario

| Campo | Descripción |
|-------|-------------|
| Nombre de usuario | Identificador único para login |
| Contraseña | Hash de la contraseña |
| Rol | ADMIN u OPERARIO |
| Persona asociada | FK a persona (datos del empleado) |
| Estado | ACTIVO o INACTIVO |
| Último login | Fecha del último acceso |

---

## Datos de la Persona (Conductor/Propietario)

El sistema obtiene los datos de la persona de dos fuentes:

### Fuente primaria: Sistema externo (HTTP)
Cuando el sistema externo retorna datos, se obtiene:
- Nombres completos
- Tipo de documento (Cédula, Cédula Extranjería, etc.)
- Número de documento
- Correo electrónico
- Teléfono
- Sexo
- Fecha de nacimiento (opcional)

### Fuente secundaria: Entrada manual
Si la persona NO existe en el sistema externo, el operario debe digitar:
- Nombres completos
- Tipo de documento
- Número de documento
- Correo electrónico
- Teléfono
- Sexo
- Fecha de nacimiento (opcional)

> **Nota**: NO se almacenan fotos ni datos biométricos.

---

## Datos del Vehículo

El sistema obtiene los datos del vehículo de dos fuentes:

### Fuente primaria: Sistema externo (HTTP)
Cuando el sistema externo retorna datos, se obtiene:
- Placa (identificador)
- Marca (ej: BMW, Toyota, Ford)
- Modelo (año, ej: 2021, 2023)
- Referencia (línea o versión, ej: Serie 3, Corolla)

### Fuente secundaria: Entrada manual
Si el vehículo NO existe en el sistema externo, el operario debe digitar:
- Placa
- Marca
- Modelo
- Referencia
- Color (opcional)
- Tipo (Automóvil, Camioneta, Moto, Camión, Otro)
- Servicio (Particular, Público, Oficial)

---

## Evidencias (Fotos)

El sistema permite capturar y almacenar evidencias fotográficas del vehículo tanto al ingreso como a la salida.

### Tipos de Evidencia
Los tipos de evidencia se configuran en la base de datos. Ejemplos típicos:
- Foto frontal
- Foto lateral izquierda
- Foto lateral derecha
- Foto trasera
- Foto interior
- Foto daños
- Foto documentos
- etc.

> El administrador puede configurar los tipos de evidencia desde la aplicación.

### Flujo de Evidencias
1. El operario selecciona el tipo de evidencia del selector (desde BD)
2. Captura o sube la foto
3. Se associa al ingreso (entrada) o a la salida

### Reglas
- Las evidencias son opcionales pero recomendadas
- Cada evidencia tiene: tipo, imagen (blob/path), fecha/hora, ingreso_id o salida_id
- Las evidencias de entrada se asocian al ingreso
- Las evidencias de salida se asocian a la salida

---

## Reglas de Negocio

| # | Regla |
|---|-------|
| RN-01 | Todo ingreso genera un registro único con placa, fecha y hora de ingreso |
| RN-02 | Un vehículo puede tener múltiples ingresos en el mismo día si sale y vuelve a entrar |
| RN-03 | El inventario se divide en tres categorías: Exterior, Interior, Seguridad |
| RN-04 | Cada categoría de inventario tiene un conjunto fijo de checkitems |
| RN-05 | El checkitem indica si hay novedad (True = tiene novedad, False = OK) |
| RN-06 | El conductor/propietario es responsable del estado del vehículo registrado |
| RN-07 | El operario firma como testigo del inventario realizado |
| RN-08 | El tipo de acceso define la tarifa: Por Hora, Diario, Mensual, Convenio |
| RN-09 | Cada ingreso debe tener un cupo asignado (identificador de espacio físico) |
| RN-10 | El sistema debe registrar la fecha/hora exacta de cada operación |
| RN-11 | La búsqueda de persona se realiza via HTTP al sistema externo antes de permitir entrada manual |
| RN-12 | Si el sistema externo retorna datos, se usan para completar el registro de persona |
| RN-13 | Si el sistema externo NO retorna datos, el operario debe digitar los datos manualmente |
| RN-14 | La salida del vehículo requiere inspección de estado y comparación con el ingreso |
| RN-15 | El tiempo se cobra completo según el tipo de acceso contratado, sin importar la hora de salida |
| RN-16 | Las tarifas son configuradas por el administrador por tipo de vehículo y modelo |
| RN-17 | La búsqueda de vehículo se realiza via HTTP al sistema externo antes de permitir entrada manual |
| RN-18 | Si el sistema externo retorna datos del vehículo, se usan para completar el registro |
| RN-19 | Si el sistema externo NO retorna datos del vehículo, el operario debe digitar los datos manualmente |
| RN-20 | El sistema permiteadjuntar evidencias (fotos) por tipo en ingresos y salidas |
| RN-21 | Los tipos de evidencia se configuran en la base de datos (selector) |
| RN-22 | Cada evidencia pertenece a un tipo específico y a un ingreso o salida |
| RN-23 | El sistema tiene usuarios propios para autenticación y control de acceso |
| RN-24 | Los usuarios tienen roles: ADMIN (acceso total) y OPERARIO (operaciones día) |
| RN-25 | El usuario administrador puede crear, editar y desactivar usuarios |
| RN-26 | Cada operación (ingreso/salida) queda registrada con el usuario que la realizó |

---

## Procesos

### P-01: Ingreso de Vehículo

```
1. BÚSQUEDA DE VEHÍCULO (Sistema Externo)
   ├── Consultar HTTP con número de placa
   ├── Si existe → auto-completar datos (marca, modelo, referencia)
   └── Si NO existe → solicitar entrada manual de datos del vehículo

2. BÚSQUEDA DE PERSONA (Sistema Externo)
   ├── Consultar HTTP con número de documento
   ├── Si existe → auto-completar datos
   └── Si NO existe → solicitar entrada manual de datos

2. DATOS DEL VEHÍCULO
   ├── Placa (identificador único, desde sistema externo o manual)
   ├── Tipo (Automóvil/Camioneta/Moto/Camión/Otro)
   ├── Marca (desde sistema externo o manual)
   ├── Línea / Modelo (año, desde sistema externo o manual)
   ├── Referencia (desde sistema externo o manual)
   ├── Color (opcional)
   └── Servicio (Particular/Público/Oficial)

3. DATOS DEL CONDUCTOR/PROPIETARIO
   ├── Nombre completo (desde sistema externo o manual)
   ├── Tipo documento (desde sistema externo o manual)
   ├── Número documento (desde sistema externo o manual)
   ├── Correo electrónico (desde sistema externo o manual)
   ├── Teléfono (desde sistema externo o manual)
   ├── Sexo (desde sistema externo o manual)
   └── Fecha nacimiento (desde sistema externo o manual, opcional)

4. INVENTARIO EXTERIOR (checklist)
   ├── Parachoques delantero [ ]
   ├── Parachoques trasero [ ]
   ├── Puertas [ ]
   ├── Espejos [ ]
   ├── Vidrios [ ]
   ├── Luces [ ]
   ├── Llantas [ ]
   ├── Rayones visibles [ ]
   ├── Golpes visibles [ ]
   └── Observaciones: _______________

5. INVENTARIO INTERIOR (checklist)
   ├── Tapicería [ ]
   ├── Tablero [ ]
   ├── Radio/Pantalla [ ]
   ├── Alfombras [ ]
   ├── Cinturones [ ]
   ├── Elementos personales [ ]
   └── Observaciones: _______________

6. ELEMENTOS DE SEGURIDAD
   ├── Llanta repuesto [ ]
   ├── Gato [ ]
   ├── Cruceta [ ]
   ├── Extintor [ ]
   ├── Botiquín [ ]
   ├── Triángulos [ ]
   ├── Herramientas [ ]
   └── Otros: _______________

7. INFORMACIÓN PARQUEADERO
   ├── Cupo asignado
   └── Tipo Acceso: Hora | Diario | Mensual | Convenio

8. EVIDENCIAS (fotos opcionales)
   ├── Selector de tipos de evidencia (desde BD)
   ├── Captura/subida de fotos por tipo
   └── Se asocia al ingreso

9. RESPONSABLES (firmas digitales)
   ├── Firma conductor → responsable del estado
   └── Firma operario → testigo del inventario

9. REGISTRO
   └── Fecha/Hora exacta de ingreso
```

---

### P-02: Salida de Vehículo

```
1. IDENTIFICACIÓN
   ├── Buscar vehículo por Placa
   └── Seleccionar ingreso activo (el más reciente sin salida)

2. VERIFICACIÓN DE AUTORIZACIÓN
   ├── Solicitar identificación de quien retira (nombre, documento)
   ├── Comparar con datos del conductor registrado
   └── Confirmar autorización

3. INSPECCIÓN DE SALIDA
   ├── Realizar checklist de estado actual del vehículo
   ├── Comparar con inventario del ingreso
   ├── Registrar novedades (si las hay)
   ├── Captura de evidencias (fotos opcionales)
   ├── Selector de tipos de evidencia (desde BD)
   └── Generar Acta de Salida (contraste ingreso vs. salida)

4. CÁLCULO DE ESTADÍA
   ├── Fecha/Hora ingreso → Fecha/Hora salida
   └── Duración total (horas/días)

5. CÁLCULO DE TARIFA
   ├── Según tipo acceso (Hora/Diario/Mensual/Convenio)
   ├── Según tarifa configurada por tipo de vehículo
   └── Total a pagar (se cobra tiempo completo, sin importar salida temprana)

6. REGISTRO DE SALIDA
   ├── Fecha/Hora exacta de salida
   ├── Usuario que registra la salida
   ├── Estado de entrega (entregado OK)
   └── Novedades registradas (si aplica)

7. ENTREGA DEL VEHÍCULO
   └── Confirmar que el vehículo sale
```

---

## Pendientes por Definir

- [ ] Formato exacto del consumo HTTP (endpoint, autenticación, response)
- [ ] Detalle de tarifas (valor por hora/día/mes por tipo de vehículo)
- [ ] Tipos de evidencia por defecto a cargar en la BD
- [ ] Reportes y consultas
- [ ] Roles y autenticación (login de operario/administrador)

---

*Documento generado a partir de: Inventario.txt y Formato Ingreso.txt*
*Actualizado con definiciones del proceso de salida e integración externa*