# Arquitectura prevista

Fecha: 2026-11-02. Estado: núcleo local implementado, comprobado con pruebas locales y empaquetado en APK de depuración. Las secciones de Drive, adjuntos y acceso describen trabajo futuro.
Alcance y decisiones: ver `REQUIREMENTS.md`.

## Componentes aprobados

### Configuración del primer bloque

- JDK 17 (Temurin), Gradle 8.13, Android Gradle Plugin 8.13.2.
- Kotlin y compilador Compose 2.2.21, KSP 2.2.21-2.0.4.
- Compose BOM 2025.12.00, Activity 1.12.2, Lifecycle 2.10.0, Room 2.8.4.
- SDK de compilación y objetivo 36; mínimo 30 (Android 11).
- JUnit 4.13.2 y Robolectric 4.16 para pruebas locales de reglas, Room e interfaz Compose. No equivalen a ejecución en un celular físico.
- Versiones estables fijadas para comprobar el conjunto; no se afirma que sean las últimas disponibles.
- Herramientas descargadas y verificadas por SHA-256 en `.tools`, sin modificar Java ni el SDK global del equipo. Cachés, rutas locales y firma de prueba excluidos del repositorio mediante `.gitignore`.
- APK inicial de depuración firmado con clave de prueba local; no es todavía una entrega final con firma de distribución.
- Nunito se distribuye como fuente local con su licencia en `app/src/main/assets/licenses/Nunito-OFL.txt`. Fuente: [Google Fonts](https://github.com/google/fonts/tree/main/ofl/nunito).
- Referencias: [AGP 8.13](https://developer.android.com/build/releases/agp-8-13-0-release-notes), [Compose diciembre 2025](https://android-developers.googleblog.com/2025/12/whats-new-in-jetpack-compose-december.html), [Room](https://developer.android.com/jetpack/androidx/releases/room), [Robolectric 4.16](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.16).

La base implementada contiene `topics` y `notes`, claves estables, índice por temática/fecha y transacciones para mantener contenido y actividad coherentes. No hay migraciones destructivas automáticas. Se exporta el esquema Room. Adjuntos, cuenta y respaldo aún no tienen tablas implementadas.

El APK inicial no solicita permisos de red y excluye la copia automática del sistema para no activar un respaldo alternativo al planificado. Huella/PIN y Drive se incorporarán en bloques posteriores.

| Componente | Responsabilidad |
| --- | --- |
| Kotlin y Jetpack Compose | Aplicación Android nativa e interfaz de acceso, temáticas, conversaciones y ajustes. |
| Room | Persistencia estructurada local de temáticas, notas y referencias a adjuntos. |
| Almacenamiento privado de la app | Copias locales de imágenes y documentos. |
| Integración de Android | Autenticación del dispositivo, selección de archivos, recepción y envío de contenido y apertura externa. |
| WorkManager | Trabajo periódico de respaldo y tratamiento de reintentos. |
| Google Drive | Destino de la última copia completa y fuente para restaurar. |

Android 11 es la versión mínima. No hay servidor propio. El teléfono mantiene los datos de trabajo; Drive conserva un respaldo, no una colección para sincronización continua.

Las versiones del bloque local están fijadas arriba y se compilaron juntas. WorkManager y las bibliotecas de Google todavía no se incorporaron; verificar sus versiones y compatibilidad cuando se autorice ese bloque.

## Organización interna propuesta

Separar interfaz, operaciones sobre notas y acceso a almacenamiento/integraciones. Esta separación permite probar las reglas sin depender de pantallas y sustituir servicios por dobles de prueba. No implica agregar frameworks ni dependencias nuevas automáticamente.

Flujo principal: interfaz → operación de la aplicación → Room y archivos locales.
Flujo de respaldo: instantánea consistente local → paquete temporal → Drive → verificación → retiro del respaldo anterior.
Flujo de restauración: Drive → descarga temporal → validación → confirmación aplicable → reemplazo coherente local.

## Modelo de datos propuesto, pendiente de concretar

| Entidad | Campos conceptuales |
| --- | --- |
| Temática | Identificador estable, nombre, fechas de creación y modificación; información necesaria para orden e ícono. |
| Nota | Identificador estable, temática, texto opcional, fechas de creación y edición. |
| Adjunto | Identificador estable, nota, nombre original, tipo de contenido, tamaño y referencia interna al archivo. |
| Estado de respaldo | Cuenta vinculada, identificador de copia vigente, fecha de éxito y estado o error de la operación. |
| Manifiesto de respaldo | Versión del formato, fecha, entidades incluidas e inventario de archivos con datos de integridad. |

- Cada nota pertenece a una temática existente.
- Los adjuntos deben estar asociados a notas existentes y a archivos válidos.
- Un movimiento cambia la asociación de temática, no la identidad de la nota.
- Debe definirse si una nota admite varios adjuntos y cómo combinar texto y adjuntos en una recepción.
- Las referencias internas deben poder reconstruirse al restaurar en otro dispositivo; no depender de rutas absolutas del teléfono anterior.
- Las migraciones futuras de datos deben conservar el contenido al actualizar el APK.

El esquema local de temáticas y notas se implementó y está exportado en `app/schemas/com.notas.personal.data.NotesDatabase/1.json`. Las entidades restantes siguen como propuesta para los bloques posteriores.

## Respaldo y restauración

### Integración con Drive

- Configurar un proyecto de Google, habilitar la API y registrar la identidad Android y su firma según el flujo de autorización seleccionado.
- Obtener autorización del usuario para el espacio de respaldo con el menor alcance de permisos posible.
- Se propone evaluar `appDataFolder`, el espacio de datos específico de la aplicación. No aparece como una carpeta ordinaria navegable en la interfaz de Drive; confirmar esta elección técnica antes de implementarla.
- Verificar el modo de publicación y consentimiento de OAuth adecuado para uso personal, la persistencia del acceso y el comportamiento al revocarlo. La configuración todavía no existe ni fue comprobada.
- No se verificaron cuotas, espacio disponible ni costos de la cuenta del usuario; no prometer almacenamiento ilimitado o costos nulos.

### Consistencia y fallos

- Crear un respaldo que corresponda a un estado coherente de la base y los adjuntos, incluso si hay ediciones concurrentes.
- Verificar inventario e integridad del paquete antes de declarar éxito y retirar la copia anterior.
- Evitar trabajos duplicados o simultáneos que retiren una copia vigente por error.
- Planificar reintentos limitados y espaciados para errores transitorios; concretar cantidad y plazos antes de implementar.
- Una autorización revocada o falta de espacio debe producir un estado accionable en ajustes, sin modificar la fecha del último respaldo exitoso.
- Durante una restauración, preparar y validar el contenido por separado; definir un mecanismo de reemplazo con recuperación ante interrupciones.
- Una copia con formato desconocido, rutas inválidas, archivos faltantes o contenido corrupto debe rechazarse preservando los datos locales.
- No confundir verificación de integridad con cifrado o garantía de autenticidad frente a un atacante.

El mecanismo exacto para empaquetar, verificar y reemplazar datos sigue pendiente. También debe definirse si la configuración visual forma parte de la copia; el contenido y las relaciones sí están aprobados.

## Acceso y privacidad proporcional

El usuario indicó que almacenará todo tipo de información y rechazó una capa adicional de cifrado con clave de recuperación. Se respeta esa decisión: huella o credencial del dispositivo al entrar, protecciones normales de Android y autorización de Drive. No se promete cifrado adicional administrado por la aplicación ni certificación de seguridad.

| Riesgo relevante | Control previsto | Comprobación propuesta |
| --- | --- | --- |
| Acceso por una entrada alternativa | Aplicar el bloqueo también al flujo de compartir recibido. | Cancelar o fallar autenticación en cada entrada y comprobar que no exponga notas. |
| Exposición innecesaria de archivos | Almacenamiento privado y permisos temporales al compartir o abrir. | Comprobar acceso del destinatario y alcance de permisos. |
| Exposición de cuenta o contenido en diagnósticos | No registrar textos personales, archivos, tokens o secretos; usar mecanismos oficiales de autorización. | Revisar registros y configuración con datos ficticios. |
| Entradas de archivos o copias inválidas | Validar nombres, rutas, tamaños, formato y coherencia; definir límites antes de implementar. | Probar archivos inaccesibles y paquetes manipulados, sin pérdida de datos. |
| Pérdida por interrupción o actualización | Conservar estado anterior hasta validar y aplicar cambios de forma recuperable. | Interrumpir operaciones en entorno de prueba y comprobar recuperación. |
| Dependencias o configuraciones inseguras | Verificar versiones soportadas, permisos y configuración de publicación. | Revisión de dependencias y manifiesto antes de entregar el APK. |

Referencias de planificación: OWASP Top 10:2025 y OWASP ASVS 5.0.0, limitados a riesgos aplicables de acceso, configuración, dependencias, integridad y errores. Son referencias orientadas a aplicaciones y servicios web; para particularidades móviles se consultó también OWASP MASVS, sin fijar una versión ni citar identificadores de controles. No se adopta automáticamente un estándar completo ni se afirma cumplimiento.

Room no agrega por sí solo el cifrado adicional que se descartó. El bloqueo de acceso y el cifrado son controles distintos. La política de sesión al volver a la app y la coordinación del bloqueo con trabajos de respaldo deben concretarse sin impedir la programación acordada.

## Documentos y compartir

Copiar al almacenamiento de la app los archivos que el usuario decida guardar, usando los permisos de lectura concedidos por Android. Para abrir o compartir, exponer solo el contenido seleccionado mediante mecanismos del sistema y permisos acotados. Manejar la ausencia de una app compatible.

Seleccionar formatos admitidos y límites tras revisar las necesidades y las bibliotecas disponibles. No incorporar lectores, editores, OCR ni descarga automática del contenido de enlaces sin ampliar expresamente el alcance.

## Distribución

Entregar un APK firmado para instalación y actualización manual. Conservar de forma segura la clave de firma y la identidad de la app, fuera del repositorio. Definir la custodia y recuperación de esa clave antes de generar la primera entrega. La firma debe corresponder a la configuración de autorización de Google utilizada.

## Fuentes oficiales consultadas

Consulta realizada durante la planificación del 2026-11-02; revisar nuevamente cuando se fijen versiones.

- [Jetpack Compose](https://developer.android.com/compose): interfaz nativa.
- [Room](https://developer.android.com/training/data-storage/room): persistencia local.
- [Autenticación biométrica](https://developer.android.com/identity/sign-in/biometric-auth): combinación de biometría y credencial del dispositivo, y limitaciones anteriores a Android 11.
- [Compatibilidad Android](https://developer.android.com/guide/topics/manifest/uses-sdk-element): versión mínima y objetivo.
- [Trabajo persistente](https://developer.android.com/develop/background-work/background-tasks/persistent): WorkManager.
- [Solicitudes de trabajo](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work): periodicidad, restricciones y reintentos.
- [Datos de aplicación en Drive](https://developers.google.com/workspace/drive/api/guides/appdata): espacio de respaldo propuesto.
- [OWASP Top 10:2025](https://top10.owasp.org/2025/).
- [OWASP ASVS 5.0.0](https://asvs.owasp.org/).
- [OWASP MASVS](https://mas.owasp.org/MASVS/): referencia móvil complementaria.
