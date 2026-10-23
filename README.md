# RifaApp

Aplicación Android 100% local/offline para gestionar rifas: reserva y venta de números, abonos, pagos, liberación y resumen en vivo. Sin backend, sin cuentas y sin pagos en línea.

## Características

- Rifa configurable: premio, descripción, fecha de sorteo, precio por número, rango y columnas de la grilla.
- Campo opcional de lotería con la que se juega.
- Estados por número: `DISPONIBLE`, `RESERVADO`, `ABONADO`, `PAGADO`, calculados automáticamente según el monto pagado.
- Datos del comprador por número: nombre y celular (E.164).
- Registro de abonos parciales y liberación de números.
- Resumen en vivo: disponibles, reservados, abonados, pagados y total recaudado.
- Detección de clientes con varios números comprados y saldo total por cliente.
- Mensaje por WhatsApp mediante deep link `wa.me`, con descripción, lotería, fecha o “jugará cuando se vendan todas las boletas”.
- Vista para captura de pantalla: grilla centrada con cuadrados, disponibles en verde y ocupados tachados con X roja; botón para compartir la imagen.
- Tema claro/oscuro/automático con Material You (color dinámico).
- Exportar/importar respaldo completo en JSON.
- Almacenamiento local con Room; persistencia offline.

## Arquitectura

Estructura modular inspirada en Clean Architecture:

- `app`: punto de entrada, navegación principal, DI manual (`AppContainer`) y migraciones Room.
- `core/domain`: modelo de dominio, casos de uso, validaciones y contratos de repositorio (Kotlin puro).
- `core/data`: implementaciones en memoria de repositorios.
- `core/database`: persistencia Room, entidades, DAOs y mappers.
- `core/designsystem`: tema, formatos, componentes y mensajes de error.
- `feature/rifa`: tablero, detalle, reserva, filtros, edición y envío por WhatsApp.
- `feature/config`: configuración de la rifa, respaldo y vista para captura.

Regla de dependencia: `feature → core:domain`. `:app` es el único que conoce las implementaciones y las inyecta.

## Modelo de dominio

Entidades principales:

- `Rifa`: id, titulo, descripcion, fechaSorteo, precioBoleta (centavos), numeroInicio, numeroFin, cantidadDigitos, columnas, loteria (opcional).
- `Numero`: valor, estado.
- `Boleta`: id, rifaId, numero, cliente, pagos, notas.
- `Cliente`: nombre, telefonoE164.
- `Pago`: id, boletaId, monto (centavos), fecha, nota.

Estados:

- `DISPONIBLE`: sin boleta.
- `RESERVADO`: boleta con cliente, sin pagos.
- `ABONADO`: pago parcial.
- `PAGADO`: pago total o superior.

El estado se deriva automáticamente del dinero pagado (`EstadoNumero.desde`), por lo que editar el monto recalcula el estado.

## Pantallas

1. **Tablero de números** (principal): grilla, leyenda, contadores, filtros por estado/número/cliente.
2. **Detalle / venta de número**: datos del cliente, precio, pagado, saldo, números comprados por el cliente, saldo total, registrar abono, editar, liberar, WhatsApp.
3. **Configuración de la rifa**: título, descripción, lotería, fecha, precio, rango, columnas, tema, respaldo y restaurar por defecto.
4. **Vista para captura**: grilla centrada con fondo blanco para captura de pantalla y botón para compartir la imagen.

## Requisitos

- Android Studio Ladybug o superior.
- JDK 17+.
- SDK Android mínimo: 21 (revisar `minSdk` en Gradle).
- Gradle Wrapper incluido.

## Compilar y ejecutar

```bash
# Compilar módulos principales
./gradlew :app:assembleDebug

# Instalar en un dispositivo/emulador conectado
./gradlew :app:installDebug
```

En Windows usa `gradlew.bat`.

## Pruebas

```bash
./gradlew :core:domain:test
```

## Migraciones de base de datos

- Versión 1→2: agrega columna `columnas`.
- Versión 2→3: agrega columna `loteria` (nullable).

Se usa `fallbackToDestructiveMigration()` como respaldo.

## Respaldo JSON

- Exportar: genera un JSON con la rifa y todas las boletas/pagos.
- Importar: valida rifa, rango, teléfonos y montos antes de aplicar los cambios.

## Notas

- La base de datos local usa Room.
- Los estados de los números se derivan del dinero pagado.
- Los cambios en configuración se aplican automáticamente y no requieren botón de guardado.
- El tema se persiste en SharedPreferences (`rifa_prefs`).