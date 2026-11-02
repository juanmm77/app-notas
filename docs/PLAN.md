# Plan de implementación propuesto y aprobado

Fecha: 2026-11-02.
El usuario aprobó este plan y posteriormente autorizó el primer bloque local: preparación, interfaz, temáticas, notas de texto, persistencia y APK de prueba. La configuración de Google y las demás integraciones quedan para bloques posteriores.

Requisitos: `REQUIREMENTS.md`. Diseño técnico y riesgos: `ARCHITECTURE.md`.

## Etapas

### 1. Diseño y preparación

Objetivo: concretar las cuatro pantallas, el diseño visual y una configuración técnica compatible.

Dependencias: autorización de implementación para instalar herramientas o modificar servicios; resolución de los pendientes que condicionan esta etapa.

Trabajo previsto:
- Revisar herramientas disponibles y verificar versiones estables compatibles con Android 11 en adelante.
- Seleccionar tipografía y comprobar licencia, contraste y adaptación del texto.
- Concretar las reglas funcionales pendientes sin cambiar decisiones ya aprobadas.
- Preparar la configuración de Google Drive y autorización para uso personal.
- Recomendar uso de Git y ramas por funcionalidad; ejecutarlo solo con autorización que lo cubra.

Criterio de cierre: diseño revisado, dependencias justificadas, configuración técnica documentada y pendientes necesarios resueltos. Las credenciales no se incluirán en documentación ni repositorio.

### 2. Temáticas y notas

Objetivo: completar el núcleo local y la navegación tipo conversación.

Dependencias: etapa 1; reglas de validación, eliminación de temáticas y orden definidas.

Criterio de cierre: crear, editar, buscar, mover y borrar con confirmación; conservar datos tras reinicio; poder consultar y gestionar sin conexión. Pruebas de lógica y persistencia, más comprobaciones de interfaz Android con datos ficticios.

### 3. Adjuntos y compartir

Objetivo: incorporar imágenes y documentos y los flujos entre aplicaciones.

Dependencias: etapa 2; tipos, límites y cantidad de adjuntos definidos.

Criterio de cierre: guardar una copia local utilizable sin conexión, abrir documentos externamente y compartir en ambos sentidos. Cancelaciones, archivos inaccesibles y falta de espacio no dejan contenido incompleto. No modificar ni borrar los archivos originales de otras apps.

### 4. Acceso y privacidad básica

Objetivo: proteger las entradas a la app con huella o credencial del dispositivo.

Dependencias: navegación y recepción de contenido disponibles; política de sesión definida.

Criterio de cierre: autenticación aplicada a todas las entradas; cancelación y fallo bloquean el acceso; permisos acotados y ausencia de contenido personal o secretos en registros. Verificar también el retorno desde un lector externo de documentos.

### 5. Respaldo y restauración

Objetivo: recuperar la colección completa desde la última copia verificada de Drive.

Dependencias: datos y adjuntos estables, configuración Google operativa, formato de respaldo y reglas de reintento definidos.

Criterio de cierre: respaldo periódico con ambas conexiones; última fecha de éxito y errores visibles; copia anterior conservada durante fallos; recuperación de textos, temáticas y archivos. Restauración confirmada y validada antes de reemplazar datos. Probar interrupciones y revocación de permisos.

### 6. Pruebas y APK

Objetivo: entregar un instalador firmado y comprobar instalación y actualización.

Dependencias: etapas anteriores completadas; custodia de firma y matriz de dispositivos definidas.

Criterio de cierre: pruebas relevantes aprobadas en Android 11, versiones posteriores seleccionadas y Redmi Note 12S; APK firmado instalable; actualización conservando datos. Documentar resultados, limitaciones y pasos comprobados de instalación y recuperación.

## Registro inicial de pruebas

Este es el registro de casos del plan completo. Sus resultados efectivos se describen en el registro del bloque local al final; los casos de funciones futuras siguen propuestos y no ejecutados.

| ID | Requisitos | Caso y resultado esperado |
| --- | --- | --- |
| T01 | F01, F06 | Abrir normalmente o mediante contenido compartido y cancelar huella/PIN: no acceder a notas ni al selector de temáticas. |
| T02 | F02, F03 | Crear temáticas y notas, cerrar y volver a abrir sin internet: contenido íntegro y navegación disponible. |
| T03 | F03 | Editar y mover una nota con adjunto: contenido conservado una sola vez en el destino. |
| T04 | F03 | Cancelar borrado conserva la nota; confirmar la retira sin papelera. |
| T05 | F04 | Buscar texto o nombre conocido y una cadena inexistente: resultados pertinentes o estado vacío. |
| T06 | F05 | Guardar una imagen y documento; retirar el acceso al origen y consultar sin conexión: copias internas disponibles. |
| T07 | F05 | Archivo inaccesible, espacio insuficiente o ausencia de lector: error claro, sin notas parciales ni cierre inesperado. |
| T08 | F06 | Compartir texto, enlace, imagen y documento hacia la app, elegir temática y comprobar destino; compartir hacia una app de prueba y comprobar contenido. |
| T09 | F07 | Completar respaldo mediante Wi-Fi y mediante datos móviles: copia válida y fecha de éxito actualizada. |
| T10 | F07 | Interrumpir red, revocar autorización o simular cuota agotada: conservar copia anterior y mostrar fallo; no registrar falso éxito. |
| T11 | F07 | Borrar una nota y completar siguiente respaldo: restauración de esa copia no recupera la nota borrada. |
| T12 | F08 | Restaurar copia válida en una instalación de prueba con datos: confirmar reemplazo y recuperar exactamente la colección respaldada. |
| T13 | F08 | Cancelar restauración o usar paquete corrupto/incompatible: conservar la colección local. |
| T14 | F08 | Interrumpir el reemplazo en un punto controlado: recuperar un estado coherente, sin pérdida parcial silenciosa. |
| T15 | F09 | Instalar y actualizar APK con misma identidad y firma: mantener notas, archivos y configuración pertinente. |
| T16 | Interfaz | Revisar modos claro/oscuro, texto ampliado, contraste, etiquetas accesibles y navegación: contenido legible y acciones utilizables. No equivale a auditoría completa de accesibilidad. |
| T17 | Seguridad | Revisar permisos, entradas de archivos y diagnósticos: acceso limitado, rechazo de rutas inválidas y ausencia de secretos o contenido personal en registros. |
| T18 | F07 | Evaluar comportamiento periódico en dispositivo real con restricciones de batería: registrar retrasos y estado visible; no exigir ejecución a hora exacta. |

Las cargas máximas de archivos, volumen de notas, latencias aceptables y tiempos de prueba deben acordarse antes de declarar verificados los límites de rendimiento.

## Herramientas, entorno y evidencia

- Pruebas unitarias para reglas; integración para Room, archivos y formato de respaldo; pruebas instrumentadas de Android para interfaz y acceso al sistema.
- Emuladores para versiones seleccionadas y dispositivo físico para huella, compartir, apertura externa y trabajo en segundo plano.
- Playwright MCP no es la herramienta principal de esta app nativa; no se utilizó ni se comprobó su conexión. Revaluar solo si se incorpora un componente web autorizado.
- Usar datos ficticios y una copia de prueba identificable. Una ejecución en Drive escribe datos externos y necesita autorización que la cubra; la aprobación de este documento no autoriza esas operaciones.
- Registrar para cada ejecución: fecha, versión de la app, dispositivo/Android, entorno, datos usados, resultado y evidencia sin información sensible.
- Una prueba intentada pero interrumpida se registra como ejecutada con resultado inconcluso. Aprobada significa resultado verificado, no permiso del usuario para ejecutarla.

## Pendientes antes de las etapas correspondientes

1. Nombre definitivo de la app y detalles visuales de pantallas futuras. El bloque local usa nombre provisional Notas y tipografía Nunito con licencia incorporada.
2. Confirmar la versión Android exacta del Redmi Note 12S. El usuario informó «15ap3a.2309»; no constituye una verificación inequívoca. No cambiar por ello el mínimo aprobado de Android 11.
3. Política de sesión al volver del segundo plano y comportamiento al regresar de una app externa.
4. Borrado de una temática con notas. Nombres, notas vacías y actividad reciente ya se resolvieron para el bloque local en `REQUIREMENTS.md`.
5. Tipos y tamaños de documentos, cantidad de adjuntos por nota/operación y objetivos medibles de rendimiento.
6. Versiones compatibles de herramientas y dependencias, formato de respaldo, verificación, recuperación ante interrupciones y parámetros de reintento.
7. Cuenta y configuración de Google, elección final del espacio de Drive y permisos; capacidad disponible y condiciones de acceso persistente.
8. Matriz exacta de versiones de Android y acceso al dispositivo para pruebas.
9. Custodia de la clave de firma y procedimiento de entrega/actualización del APK.

Estos pendientes no son requisitos aprobados por omisión. Resolver los que cambien la experiencia con el usuario, y las decisiones técnicas rutinarias dentro del alcance que se autorice, antes de ejecutar las etapas dependientes.

## Riesgos y límites conocidos

- La programación diaria puede retrasarse por conectividad y restricciones de Android. Mostrar el último éxito permite evaluar la antigüedad real de la copia.
- Adjuntos grandes o numerosos consumen espacio local, cuota de Drive y datos móviles, cuyo uso fue aprobado. Faltan límites concretos.
- Un respaldo único no conserva un historial de versiones y puede reflejar borrados en la siguiente actualización; esta política fue aprobada.
- El bloqueo biométrico no equivale al cifrado adicional rechazado por el usuario.
- La pérdida de la clave de firma puede impedir continuar las actualizaciones con la misma identidad; definir su custodia antes de distribuir.
- La compatibilidad futura requiere pruebas y mantenimiento; no se garantiza por fijar una versión mínima.

## Siguiente autorización

El primer bloque local está autorizado. Se requiere una instrucción posterior para avanzar con adjuntos, compartir, autenticación, Drive u otras funciones fuera de ese bloque.

## Ejecución del primer bloque local

- Autorización: «autorizo» a la propuesta de primer APK local.
- Implementación: proyecto Android, Room, lista de temáticas, conversación de texto, creación/renombrado, edición y borrado de notas con confirmación; modos claro/oscuro y Nunito.
- Verificación final: compilación y pruebas completadas; resultados detallados a continuación.
- Sin cambios en cuentas de Google, sin instalación en el teléfono y sin operaciones de Git.

### Resultados ejecutados el 2026-11-02

| Comprobación | Entorno y resultado |
| --- | --- |
| Reglas de texto y nombres | JUnit: 5 pruebas aprobadas, incluidos Unicode, saltos de línea y límites. |
| Persistencia Room | Robolectric Android 11/API 30 y Android 15/API 35: 10 pruebas aprobadas. Reapertura de base en disco, orden, edición, renombrado, aislamiento, borrado y rechazo de datos inválidos. |
| Recorrido de interfaz | Robolectric API 35 y Compose: 2 pruebas aprobadas. Crear/renombrar temática, enviar/editar notas, cancelar/confirmar borrado y navegación. |
| Apariencias | Capturas de inicio vacío, conversación clara, lista y conversación oscuras revisadas visualmente. Entorno simulado, no capturas del Redmi. |
| Contraste calculado | Combinaciones revisadas: botón claro 5,82:1, mensaje claro 12,40:1, mensaje oscuro 10,38:1, texto secundario claro 5,62:1. Comprobación parcial, no auditoría de accesibilidad. |
| Android Lint | 0 errores y 11 advertencias: 10 avisos de versiones más recientes y 1 carpeta vacía con calificador de versión redundante. Se conserva el conjunto estable ya comprobado; no se ocultaron advertencias. |
| APK | `assembleDebug` completado. 62.709.775 bytes; paquete `com.notas.personal`, versión 0.1.0, mínimo API 30, objetivo API 36. |
| Firma y permisos | `apksigner verify`: firma v2 válida, certificado de depuración. `aapt2 dump badging`: paquete y actividad de inicio válidos; sin permiso INTERNET. |

Total final: **17 pruebas, 0 fallidas y 0 errores**. Evidencia reproducible en `app/build/reports/tests/testDebugUnitTest/`, `app/build/test-results/testDebugUnitTest/` y `app/build/reports/lint-results-debug.html`.

Los primeros intentos de pruebas visuales fallaron por captura de ventana y por una limitación de Robolectric con dimensiones personalizadas en diálogos con texto. Se corrigió la captura usando el dibujo nativo de la vista y se separó la configuración de tamaño de las pruebas de interacción. Referencia del problema del entorno: [Robolectric #8460](https://github.com/robolectric/robolectric/issues/8460). El recorrido funcional completo pasó después de esas correcciones.

Durante el empaquetado se corrigieron la generación de la clave de prueba y el escape de la ruta Windows del SDK. Una referencia de recurso quedó en caché al mover el ícono; la reconstrucción encontró un archivo temporal bloqueado por Gradle. Se detuvo el daemon del proyecto y la reconstrucción final completó todas las tareas correctamente. Estos fallos intermedios no se contabilizan como comprobaciones aprobadas.

### Entrega y límites

- Instalador: `dist/Notas-0.1.0-prueba.apk`.
- SHA-256: `70b99c6b26522ea7b901d9ddd3f5b0ae9cd70bf922c6272a8635fd6577c7ee9d`.
- Capturas: `dist/vistas/`. Datos ficticios de las pruebas, no incluidos como notas iniciales en el APK.
- Firma de depuración local en `.tools/signing/debug.keystore`; conservarla para actualizar esta misma prueba. Firma final pendiente.
- No ejecutadas: instalación, actualización y cierre/reapertura del proceso en el Redmi físico; teclado real del usuario, bloqueo biométrico, adjuntos, compartir y Drive. La prueba de reapertura de base verifica persistencia, pero no equivale por sí sola a una prueba completa de reinicio del teléfono.
- La app inicial todavía no tiene copia de seguridad ni autenticación. Desinstalarla o borrar datos elimina las notas locales.
- El usuario puede ahora probar el APK y revisar la experiencia antes de autorizar el siguiente bloque.

### Publicación del primer bloque en GitHub

El usuario autorizó subir el proyecto y eligió un repositorio nuevo privado llamado `app-notas`. Se verificó la sesión de `juanmm77` y la disponibilidad del nombre. La preparación incluye código, documentación, pruebas y capturas; el APK se distribuye en la versión preliminar `v0.1.0`.

Se excluyen herramientas descargadas, cachés, rutas locales y claves de firma. Esta autorización cubre inicializar Git, crear el commit inicial, el repositorio privado y subir los archivos de esta entrega; no amplía el alcance funcional de la app.
