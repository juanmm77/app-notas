# Notas · primer bloque local

Aplicación Android personal de notas por temática, con conversaciones de texto e identidad violeta y ámbar. Nombre provisional: **Notas**.

## Instalador listo

[Descargar la versión de prueba en GitHub](https://github.com/juanmm77/app-notas/releases/tag/v0.1.0).

El repositorio es público. El APK se distribuye como archivo adjunto de la versión; no se guarda en el historial del código.

Archivo: `dist/Notas-0.1.0-prueba.apk` (62.709.775 bytes, aproximadamente 60 MiB). Firma de depuración verificada y mínimo Android 11 comprobado en el manifiesto. SHA-256 disponible en `dist/SHA256SUMS.txt`.

Validación final: 17 pruebas locales aprobadas; Android Lint sin errores, con 11 advertencias no bloqueantes. La instalación y el uso en el Redmi físico todavía no se comprobaron. Capturas del renderizado simulado en `dist/vistas/`.

## Alcance de esta entrega

- Crear y renombrar temáticas.
- Escribir, editar y eliminar notas de texto con confirmación.
- Conservar las notas localmente mediante Room y utilizarlas sin internet.
- Modos claro y oscuro según el teléfono, tipografía Nunito incorporada e ícono propio.
- Android 11 o posterior; APK de prueba.

Adjuntos, compartir entre aplicaciones, búsqueda, movimiento de notas, eliminación de temáticas, huella/PIN y respaldo en Drive pertenecen a etapas posteriores. Esta entrega todavía no cuenta con respaldo: desinstalarla o borrar sus datos elimina las notas locales.

## Uso

1. Crear una temática desde la pantalla principal.
2. Escribir una nota en el campo inferior y tocar el botón de envío.
3. Mantener presionado un mensaje para editarlo o eliminarlo.
4. Tocar el lápiz del encabezado para renombrar la temática.
5. Volver para ver la lista ordenada por actividad reciente.

No se cargan notas de ejemplo en la aplicación. Los datos de las capturas y las pruebas son ficticios y están aislados.

## Preparación y compilación en Windows

Se utiliza PowerShell 7 (`pwsh`). Las herramientas y cachés se conservan dentro de `.tools`; no se requiere instalar Android Studio para compilar por consola.

```powershell
pwsh -NoProfile -File scripts/bootstrap.ps1
pwsh -NoProfile -File scripts/build.ps1
```

La preparación requiere conexión a los proveedores oficiales y aceptación de las licencias del SDK. Descarga JDK 17, Gradle 8.13 y herramientas Android; valida los archivos de herramientas mediante SHA-256. La compilación resuelve las dependencias desde Google Maven, Maven Central y Gradle Plugin Portal.

La ejecución predeterminada genera el wrapper de Gradle, ejecuta pruebas locales, analiza Android Lint y compila `assembleDebug`. Para una tarea concreta:

```powershell
pwsh -NoProfile -File scripts/build.ps1 -Tasks assembleDebug
```

Salidas generadas y comprobadas:

- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Pruebas: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Análisis: `app/build/reports/lint-results-debug.html`.
- Capturas de las pruebas Compose: `app/build/screenshots/`.

Consultar `docs/PLAN.md` para los resultados efectivos y las limitaciones de la verificación.

La firma y el manifiesto se pueden volver a inspeccionar con `pwsh -NoProfile -File scripts/verify-apk.ps1`.

## Firma e instalación

El APK de esta etapa usa una firma de depuración guardada en `.tools/signing/debug.keystore`, excluida mediante `.gitignore`. Conservarla para actualizar esta misma instalación de prueba. La firma final de distribución todavía está pendiente; cambiar de firma puede requerir desinstalar la versión anterior.

Para probarlo, copiar el APK al celular y abrirlo con el instalador de Android. Si el sistema lo solicita, permitir la instalación desde esa aplicación de archivos. No se instaló automáticamente en el teléfono del usuario.

## Diseño y dependencias

Kotlin, Jetpack Compose, Room y componentes oficiales de Android. Nunito se distribuye bajo SIL OFL 1.1; su licencia acompaña al APK en `app/src/main/assets/licenses/Nunito-OFL.txt`.

Las pruebas usan JUnit y Robolectric, con SQLite y renderizado Android simulado. No reemplazan las comprobaciones en el Redmi Note 12S.

## Documentación

- `AGENTS.md`: instrucciones y alcance autorizado.
- `docs/REQUIREMENTS.md`: funciones completas aprobadas y decisiones del bloque.
- `docs/ARCHITECTURE.md`: diseño, versiones e integraciones futuras.
- `docs/PLAN.md`: etapas, pruebas y estado real.
