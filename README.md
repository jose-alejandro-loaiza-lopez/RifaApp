# RifaApp

Aplicación Android 100% local/offline para gestionar rifas: reserva y venta de números, abonos, pagos, liberación y resumen en vivo. Sin backend, sin cuentas y sin pagos en línea.

## Características

- Rifa configurable: premio, descripción, fecha de sorteo, precio por número y rango.
- Estados por número: `DISPONIBLE`, `RESERVADO`, `ABONADO`, `PAGADO`, calculados automáticamente según el monto pagado.
- Datos del comprador por número: nombre y celular (E.164).
- Registro de abonos parciales y liberación de números.
- Resumen en vivo: disponibles, reservados, abonados, pagados y total recaudado.
- Mensaje por WhatsApp mediante deep link `wa.me`.
- Interfaz adaptativa en Jetpack Compose con tema claro/oscuro.
- Almacenamiento local con Room; persistencia offline.

## Arquitectura

Estructura modular inspirada en Clean Architecture:

- `app`: punto de entrada, navegación principal y wiring del contenedor.
- `core/domain`: modelo de dominio, casos de uso y contratos de repositorio.
- `core/data`: implementaciones en memoria de repositorios.
- `core/database`: persistencia Room y mapeo de entidades.
- `core/designsystem`: componentes, tema y formato.
- `feature/rifa`: tablero, detalle, reserva, filtros y edición.
- `feature/config`: configuración de la rifa.

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
./gradlew :feature:rifa:connectedDebugAndroidTest
```

## Notas

- La base de datos local usa `fallbackToDestructiveMigration`; los cambios de esquema recrean la BD.
- Los estados de los números se derivan del dinero pagado, por lo que editar el monto actualiza automáticamente el estado.
- Los cambios en configuración se aplican automáticamente y no requieren botón de guardado.
