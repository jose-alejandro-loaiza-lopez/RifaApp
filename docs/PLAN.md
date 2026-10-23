# RifaApp — Plan de trabajo v1

> App Android nativa (Kotlin + Jetpack Compose) para gestionar rifas **100% local y offline**.
> Sin backend, sin cuentas, sin pagos en línea.

---

## 1. Alcance de la v1

**Dentro:**
- Rifa con rango de números configurable (`00`–`99` por defecto).
- Estado individual por número: `DISPONIBLE`, `RESERVADO`, `ABONADO`, `PAGADO`.
- Datos del comprador por número: nombre + celular con indicativo.
- Registro de pagos parciales; el estado se **deriva automáticamente** del dinero pagado.
- Liberar un número cuando el cliente ya no quiere participar.
- Configuración de la rifa editable en cualquier momento: qué se rifa, fecha del sorteo, precio de la boleta, rango de números.
- Envío de mensaje por WhatsApp mediante deep link local (`wa.me`).
- Resumen en vivo: disponibles / reservados / abonados / pagados y total recaudado.

**Fuera de v1 (anotado para después):**
- Backend / sincronización multi-dispositivo.
- Pasarela de pago.
- Impresión de boletas y exportación a PDF.
- Sorteo automático / generador de ganador.
- Notificaciones programadas.
- Múltiples usuarios o roles.

---

## 2. Modelo de dominio

### Entidades

| Entidad | Campos principales |
|---|---|
| `Rifa` | `id`, `titulo` (qué se rifa), `descripcion`, `fechaSorteo`, `precioBoleta` (centavos), `numeroInicio`, `numeroFin`, `cantidadDigitos`, `creadaEn`, `actualizadaEn` |
| `Numero` | `rifaId`, `valor` (Int), `estado` |
| `Boleta` | `id`, `rifaId`, `numero`, `cliente`, `estado`, `montoPagado`, `creadaEn`, `actualizadaEn`, `notas` |
| `Cliente` | `nombre`, `telefonoE164` (ej. `+573001234567`) |
| `Pago` | `id`, `boletaId`, `monto` (centavos), `fecha`, `nota` |
| `MovimientoEstado` | `id`, `boletaId`, `estadoAnterior`, `estadoNuevo`, `monto`, `timestamp`, `motivo` |

> **Separación clave:** `Numero` es la casilla del tablero (siempre existe).
> `Boleta` es la asignación de esa casilla a una persona.
> `Pago` es cada abono individual: **la fuente de verdad del dinero**.
> `montoPagado` se calcula como la suma de sus pagos.

### Estados (4)

```kotlin
enum class EstadoNumero { DISPONIBLE, RESERVADO, ABONADO, PAGADO }
```

| Estado | Significado |
|---|---|
| `DISPONIBLE` | No hay boleta. Se puede vender. |
| `RESERVADO` | Hay boleta con datos del cliente, pero **aún no pagó nada**. |
| `ABONADO` | Pagó **parte** del precio (0 < pagado < precio). Queda saldo. |
| `PAGADO` | Pagó el **total** (pagado ≥ precio). |

### El estado NO se guarda a mano: se deriva

```kotlin
fun estadoDe(montoPagado: Long, precioBoleta: Long): EstadoNumero = when {
    montoPagado <= 0L            -> RESERVADO
    montoPagado <  precioBoleta  -> ABONADO
    else                         -> PAGADO
}
```

Esto evita inconsistencias: no existe forma de tener `PAGADO` con saldo pendiente.
Vive en `core:domain` como función pura y con tests de borde (pago exacto, pago de más, precio 0).

### Transiciones

| Acción | Desde | Resultado |
|---|---|---|
| `reservar(nombre, telefono)` | DISPONIBLE | RESERVADO |
| `registrarPago(monto)` | RESERVADO / ABONADO | se recalcula: ABONADO o PAGADO |
| `liberarNumero(motivo)` | cualquiera | DISPONIBLE (la boleta queda en historial) |
| `editarPrecioBoleta(nuevoPrecio)` | — | **se recalculan todos los estados** de la rifa |

> Ojo con el último caso: si el precio sube, un número `PAGADO` puede volver a `ABONADO`.
> Por eso el estado es derivado y no un dato editable.

### Invariantes
- Un número no puede tener dos boletas activas a la vez.
- `precioBoleta >= 0`; un pago individual es `> 0`.
- Teléfono normalizado a E.164 antes de guardar.
- `numeroInicio < numeroFin`; el rango debe contener los números existentes.
- Dinero siempre en **centavos** (`Long`), nunca `Double`.

### Fechas y dinero
- `minSdk 24` → `core library desugaring` activado para usar `java.time`. ✅
- Persistencia de dinero: `Long` en centavos. Formato con `NumberFormat` de la locale.

---

## 3. Arquitectura

Clean Architecture por capas + feature modules:

```
┌─────────────────────────────────────────────┐
│ :app            navegación, DI, MainActivity│
├─────────────────────────────────────────────┤
│ :feature:rifa   tablero y venta (Compose)   │
│ :feature:config ajustes de la rifa           │
├─────────────────────────────────────────────┤
│ :core:designsystem  tema y componentes       │
│ :core:ui            utilidades Compose       │
├─────────────────────────────────────────────┤
│ :core:domain        modelos + casos de uso   │  ← Kotlin puro, sin Android
│ :core:data          repos impl, mappers      │
│ :core:database      Room: entities, DAOs     │
│ :core:common        Result, Dispatchers      │
└─────────────────────────────────────────────┘
```

**Regla de dependencia:** `feature → core:domain` (nunca a `core:data`).
`:app` es el único que conoce las implementaciones y las inyecta.

### Capas por feature

```
feature/rifa
├── presentation/
│   ├── tablero/   TableroViewModel, TableroUiState, TableroScreen
│   └── venta/     VentaViewModel, VentaUiState, VentaScreen
├── navigation/    rutas y NavHost del feature
└── di/            wiring del feature
```

`core/domain`:
```
model/        Rifa, Numero, Boleta, Cliente, Pago, EstadoNumero
repository/   RifaRepository, BoletaRepository, PagoRepository (interfaces)
usecase/      ObservarTablero, ReservarNumero, RegistrarPago,
              LiberarNumero, GuardarConfiguracionRifa, RecalcularEstados
validation/   ValidadorTelefono, ValidadorRango, ValidadorPago
state/        EstadoNumero.desde(pagado, precio)
```

`core/data`:
```
repository/   RifaRepositoryImpl, BoletaRepositoryImpl, PagoRepositoryImpl
mapper/       RifaEntity ↔ Rifa, BoletaEntity ↔ Boleta, PagoEntity ↔ Pago
source/       RifaLocalDataSource, BoletaLocalDataSource, PagoLocalDataSource
```

---

## 4. Patrones de diseño

| Patrón | Dónde | Para qué |
|---|---|---|
| **MVVM + UDF** | cada feature | `UiState` inmutable + `StateFlow`, eventos one-shot por `Channel` |
| **Repository** | `core:domain` (interfaz) / `core:data` (impl) | desacoplar UI de Room |
| **Use Case** | `core:domain` | una intención de negocio por clase, testeable |
| **Mapper** | `core:data` | aislar el modelo de persistencia del de dominio |
| **Derived State** | `core:domain` | el estado se calcula del dinero, no se almacena |
| **Strategy** | `core:notifications` | plantilla de mensaje WhatsApp intercambiable |
| **Result (sealed)** | `core:common` | errores de negocio sin excepciones |
| **Manual DI** (constructor injection + `AppContainer`) | `:app` | sin plugins frágiles; migrable a Hilt si crece |
| **Version catalog** | `gradle/libs.versions.toml` | una sola fuente de versiones |

---

## 5. Pantallas

1. **Tablero de números** (principal)
   - Grilla 10×10 con `00`–`99`, color por estado, leyenda y contadores.
   - Tap en número → detalle/venta. Tap largo → liberar.
2. **Detalle / venta de número**
   - Estado actual, nombre, celular con indicativo, cuánto pagó, saldo.
   - Registrar pago, liberar, enviar WhatsApp.
3. **Configuración de la rifa**
   - Título/premio, descripción, fecha del sorteo, precio, rango y dígitos.
   - Al cambiar el precio se recalculan los estados.
4. **Resumen**
   - Contadores por estado y total recaudado vs. potencial.

---

## 6. Tareas (checklist)

### Fase 0 — Base del proyecto
- [x] Gradle 9.8.2 + AGP 9.4.1 + Kotlin 2.4.20 + Compose BOM 2026.09.00
- [x] Módulo `:app` compilando e instalado en el Xiaomi
- [x] Repo git conectado a `github.com/jose-alejandro-loaiza-lopez/RifaApp`
- [x] Core library desugaring (`java.time` en API 24+)
- [x] Módulo `:core:domain` (Kotlin puro, sin Android)
- [ ] Módulos `:core:data`, `:core:database` y `:feature:*`
- [ ] `build-logic` (convention plugins) para no repetir configuración

### Fase 1 — Dominio
- [x] Modelos: `Rifa`, `Numero`, `Boleta`, `Cliente`, `Pago`, `EstadoNumero`
- [x] `EstadoNumero.desde()` + tests de borde
- [x] Reglas de transición y validaciones
- [x] Interfaces de repositorio
- [x] Casos de uso + tests con repos fake
- [x] Validador de teléfono E.164 y de montos

### Fase 2 — Persistencia
- [ ] Room: `RifaEntity`, `NumeroEntity`, `BoletaEntity`, `PagoEntity`, `MovimientoEntity`
- [ ] DAOs con `Flow`
- [ ] Migraciones desde versión 1
- [ ] Repositorios implementados + mappers
- [ ] Seed inicial: rifa de ejemplo con 100 números en `DISPONIBLE`

### Fase 3 — UI
- [ ] `designsystem`: tema, tokens, `NumeroChip`, `EstadoBadge`, `ContadorResumen`
- [ ] `feature:rifa` tablero con grilla y filtros
- [ ] Detalle/venta con formulario validado
- [ ] `feature:config` ajustes de la rifa
- [ ] Navegación (type-safe routes)
- [ ] Preview de cada pantalla

### Fase 4 — Integraciones locales
- [ ] Deep link WhatsApp: `https://wa.me/{telefono}?text={mensaje}` vía `Intent.ACTION_VIEW`
- [ ] Plantillas de mensaje (reserva, recordatorio de saldo, confirmación de pago)
- [ ] Copiar mensaje al portapapeles como fallback

### Fase 5 — Calidad
- [ ] Tests de UI con Compose test
- [ ] Accesibilidad: content descriptions, tamaños táctiles ≥ 48dp
- [ ] Modo oscuro y color dinámico verificados
- [ ] Exportar/importar respaldo (JSON)

---

## 7. Decisiones cerradas

1. ✅ `ABONADO` = **pago parcial**, queda saldo. El estado se deriva del monto pagado.
2. ✅ `PAGADO` reemplaza a "cancelado": el cliente completó el total.
3. ✅ **Una rifa activa** a la vez.
4. ✅ Cancelar la participación = **liberar el número** → vuelve a `DISPONIBLE` (con historial).
5. ✅ Rango de números **configurable**, `00`–`99` por defecto.

---

## 8. Entorno (ya configurado en esta máquina)

| Componente | Valor |
|---|---|
| JDK | Temurin 21.0.12.1 (`JAVA_HOME` configurado) |
| Android SDK | `C:\Android\Sdk` — platform 36 y 37.0, build-tools 36.0.0 |
| Gradle | wrapper 9.8.2 (AGP 9.4 exige ≥ 9.6) |
| Dispositivo | Xiaomi 2412DPC0AG, Android 16 (API 36), arm64, depuración inalámbrica |

```powershell
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.rifaapp/.MainActivity
```

> En IntelliJ: `Gradle JVM` debe ser **21**, no el 25.
> En HyperOS: activar **Opciones de desarrollador → Instalar por USB**, y aceptar el
> diálogo de instalación en el teléfono (si no, `installDebug` falla con `INSTALL_FAILED_USER_RESTRICTED`).
