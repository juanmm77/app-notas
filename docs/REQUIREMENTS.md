# Requisitos y decisiones

Fecha de consolidación: 2026-11-02.
Estado: planificación aprobada; primer bloque local implementado y APK de prueba generado. Ver resultados y límites de comprobación en `PLAN.md`.

## Objetivo

Crear una aplicación personal de notas para un único usuario en su celular. Las notas se organizan por temáticas que se abren como conversaciones de WhatsApp: cada entrada es un mensaje propio dentro de una secuencia cronológica.

La imagen aportada por el usuario sirve como referencia para la lista de conversaciones. No implica aprobar automáticamente todas las funciones visibles en WhatsApp, como filtros de no leídos o elementos fijados.

## Registro de decisiones aprobadas

| ID | Decisión | Confirmación |
| --- | --- | --- |
| D01 | Uso personal en Android; temáticas con notas como mensajes. | Pedido inicial y respuestas sobre Android y la conversación. |
| D02 | Compartir contenido hacia la app y desde ella hacia otras apps. | El usuario eligió ambas direcciones. |
| D03 | Admitir texto, enlaces, imágenes y documentos. | Confirmación explícita; audios excluidos por ahora. |
| D04 | Crear, renombrar y eliminar temáticas; editar texto, eliminar y mover notas; buscar por texto y nombre. | «lo dejaría asi». |
| D05 | Consultar y gestionar información sin internet, incluidos archivos guardados. | «si confirmo». |
| D06 | Acceso con huella y alternativa del PIN de bloqueo del celular. | «que pida huella» y «si, confirmo». |
| D07 | Respaldo automático diario en Google Drive con Wi-Fi o datos móviles. | Elecciones explícitas de Drive, frecuencia diaria y ambas conexiones. |
| D08 | Conservar solo la última copia completa verificada. La anterior se retira después de verificar la nueva. | «aprobado». |
| D09 | Borrado definitivo con confirmación, sin papelera. Lo borrado sale del respaldo al completarse la siguiente actualización. | Elección de borrado definitivo y aprobación de la política de copias. |
| D10 | Kotlin, Compose, Room y WorkManager; sin servidor propio. | «apruebo» a la arquitectura. |
| D11 | Android 11 como versión mínima, con compatibilidad prevista y pruebas para versiones posteriores. | «aprobado». |
| D12 | Pantallas de acceso, temáticas, conversación y ajustes/respaldo. | Aprobaciones de la conversación y del resto de la interfaz. |
| D13 | Identidad original violeta y ámbar, modos claro y oscuro siguiendo el sistema, tipografía de formas suaves e íconos por temática. | «lo veo bien» a la propuesta visual. |
| D14 | Guardar, compartir y abrir documentos con aplicaciones compatibles externas. | «apruebo». |
| D15 | Protecciones normales de Android y Drive; sin cifrado adicional con clave de recuperación. | El usuario rechazó la propuesta de seguridad adicional. |
| D16 | Instalación mediante APK y actualizaciones manuales. | «APK» y aprobación posterior del plan. |
| D17 | Restaurar reemplaza el contenido local, con confirmación y validación previa del respaldo. No combinar colecciones. | Aprobación explícita del plan que incluía esta regla. |
| D18 | Plan de seis etapas y documentación en cuatro archivos. | «apruebo» al plan y «autorizo» a crear los archivos. |
| D19 | Implementar el primer bloque local: proyecto Android, interfaz, crear/renombrar temáticas, escribir/editar/eliminar texto, persistencia y APK de prueba. | «autorizo» después de recomendar ese bloque. |

## Funciones y criterios de aceptación

### F01. Acceso

- Solicitar huella al acceder, con alternativa de la credencial de bloqueo configurada en Android; en el dispositivo del usuario, el PIN.
- Cancelar o fallar la autenticación no permite consultar notas ni seleccionar una temática al recibir contenido compartido.
- La app no almacena huellas ni administra un PIN propio.
- La duración de una sesión y el comportamiento al volver del segundo plano siguen pendientes de definición.

### F02. Temáticas

- Crear, renombrar y eliminar temáticas.
- Mostrar nombre, vista previa de la última nota y fecha; ordenar por actividad reciente.
- Incluir buscador y acción para crear una temática.
- La validación de nombres y la definición de actividad reciente del bloque local se concretan en «Decisiones de implementación» al final de este documento.
- Debe acordarse el tratamiento de las notas al eliminar una temática con contenido; no asumir un borrado en cascada sin explicitarlo.

### F03. Conversación y gestión de notas

- Presentar textos, enlaces, imágenes y documentos en orden cronológico como mensajes propios.
- Mostrar campo para escribir, acción para adjuntar y acción de envío. Al enviar, la nota aparece al final.
- Permitir desplazarse hacia notas anteriores y abrir las acciones mediante pulsación prolongada.
- Editar el texto, mover una nota a otra temática y eliminar con confirmación.
- Cancelar un borrado conserva la nota; confirmarlo la retira de la app sin papelera.
- Mover una nota conserva el contenido y los adjuntos, sin duplicarla.
- Los datos persisten después de cerrar y abrir la app.

### F04. Búsqueda

- Buscar por texto de notas, nombres de temáticas y nombres de archivos.
- Acceder desde un resultado al contenido correspondiente.
- Mostrar un estado claro si no hay resultados.
- No se aprobó buscar dentro del contenido de documentos ni reconocer texto en imágenes.

### F05. Adjuntos y funcionamiento sin conexión

- Guardar imágenes y documentos dentro del almacenamiento de la app para consultarlos sin depender del archivo de origen.
- Mostrar imágenes en la conversación y documentos mediante su nombre y una representación de adjunto.
- Abrir documentos con una aplicación compatible del celular; informar si no hay una disponible.
- Manejar archivos inaccesibles y falta de espacio sin dejar notas incompletas.
- Consultar y gestionar notas sin internet. Abrir el destino de un enlace externo puede requerir conexión; no se aprobó descargar páginas para uso sin conexión.
- Tipos exactos de documentos, límites de tamaño, cantidad de adjuntos y cargas de rendimiento quedan pendientes.

### F06. Compartir

- Desde otra app: recibir contenido compatible, autenticar, elegir temática y guardar.
- Desde una nota: abrir el mecanismo de compartir de Android y entregar el contenido seleccionado a la app elegida.
- Cancelar el flujo no debe crear una nota incompleta.
- Verificar interoperabilidad con otras aplicaciones usando datos ficticios. La recepción de múltiples archivos en una operación requiere concretar su alcance.

### F07. Respaldo

- Incluir temáticas, textos, enlaces, imágenes, documentos y relaciones necesarias para restaurarlos.
- Programar un respaldo diario permitiendo Wi-Fi y datos móviles.
- Mostrar cuenta de Drive, estado, última copia exitosa y errores pendientes.
- Conservar el respaldo anterior si la transferencia o verificación falla.
- Después de verificar la nueva copia, conservar solo esta como respaldo vigente. Puede existir una copia temporal durante el reemplazo.
- Las notas borradas pueden permanecer en la copia anterior hasta completar la actualización.
- La programación diaria no garantiza una hora exacta ni un intervalo máximo de pérdida: Android y la conectividad pueden retrasar el trabajo.

### F08. Restauración

- Permitir recuperar la última copia desde la cuenta de Google correspondiente.
- Explicar y confirmar que la operación reemplazará los datos locales.
- Validar integridad, formato y compatibilidad antes de modificar los datos actuales.
- Recuperar las temáticas, notas y adjuntos sin pérdida de relaciones.
- Una copia inválida o una restauración cancelada conserva el contenido local existente.
- No fusionar automáticamente la colección local con la respaldada.

### F09. Instalación y actualización

- Entregar un APK firmado instalable en dispositivos compatibles desde Android 11.
- Actualizar mediante APK conservando notas, archivos y configuración pertinente.
- Conservar la identidad de la aplicación y la clave de firma para las actualizaciones.
- Probar en el Redmi Note 12S y en versiones seleccionadas mediante emuladores u otros dispositivos. No prometer compatibilidad verificada con todas las versiones futuras.

## Interfaz y accesibilidad

- Principal: violeta iris `#7563D9`.
- Acento: ámbar suave `#E9B86E`.
- Modo claro: fondo marfil y mensajes lavanda suave.
- Modo oscuro: fondo azul tinta y mensajes violeta profundo.
- Tipografía de formas suaves y redondeadas, con títulos más marcados. El bloque local utiliza Nunito bajo SIL OFL 1.1; los colores concretos están en `ui/Theme.kt`.
- Íconos sencillos por temática. No se aprobó un editor de íconos ni fotos personalizadas.
- Seguir el modo del sistema, respetar el tamaño de texto y comprobar contraste de las combinaciones finales. La paleta aprobada no constituye una verificación de contraste.

## Exclusiones y antecedentes

- Audios: pospuestos por el usuario.
- Papelera: descartada a favor del borrado definitivo.
- Edición interna de documentos: fuera de la primera versión.
- Cifrado adicional local y de respaldo con clave de recuperación: propuesta rechazada; se mantienen las protecciones normales de la plataforma.
- Google Play: no elegido; distribución por APK.
- Restauración mediante combinación de colecciones: fuera de alcance.
- iPhone, colaboración entre usuarios, sincronización entre varios dispositivos, filtros de no leídos y elementos fijados no forman parte del alcance aprobado.

## Cuestiones pendientes

### Decisiones de implementación del bloque local

- Nombre provisional: Notas. Tipografía: Nunito, incluida en el APK con licencia SIL OFL 1.1.
- Temáticas: nombres de una línea, recortados en los extremos, de 1 a 80 caracteres. Se permiten nombres repetidos; cada temática tiene su identidad propia.
- Notas: texto no vacío, hasta 20.000 caracteres, conservando saltos de línea interiores. Se recortan espacios exteriores.
- Actividad reciente: creación o renombrado de temática y guardado, edición o borrado de una nota. La posición cronológica de una nota no cambia al editarla.
- La entrega inicial no implementa eliminar temáticas ni mover notas ni búsqueda; esas funciones siguen en el plan completo y no eran parte del primer bloque autorizado.
- El violeta iris aprobado se conserva en la identidad del ícono; para botones y texto en modo claro se usa el tono más oscuro `#6754C0` para mejorar contraste. El modo oscuro usa violeta claro para acentos.

Estas son decisiones operativas del bloque autorizado, no nuevas funciones. La eliminación de temáticas con contenido sigue pendiente.

Los pendientes de reglas funcionales están identificados en cada función. Los detalles técnicos, de configuración y de validación se registran en `ARCHITECTURE.md` y `PLAN.md`. No hay propuestas funcionales adicionales aprobadas implícitamente por este documento.
