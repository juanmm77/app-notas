# Instrucciones del proyecto

## Estado y alcance autorizado

- Proyecto: aplicación personal de notas para Android, todavía sin nombre definitivo.
- Al 2026-11-02 se aprobaron la planificación y la creación de este archivo y de `docs/REQUIREMENTS.md`, `docs/ARCHITECTURE.md` y `docs/PLAN.md`.
- Posteriormente el usuario autorizó el primer bloque de implementación: preparar el proyecto Android, interfaz, crear/renombrar temáticas, escribir/editar/eliminar notas de texto, persistencia local, comprobaciones y un APK de prueba. Esta autorización reemplaza la restricción inicial de solo planificación para ese bloque.
- No ampliar esta entrega a adjuntos, compartir, huella ni Drive. Posteriormente el usuario autorizó subir este bloque a su GitHub y eligió crear el repositorio privado `app-notas`; esa instrucción autoriza el commit inicial y la subida correspondiente.
- El 2026-11-02 el usuario cambió esa decisión y solicitó que `app-notas` sea público. La visibilidad pública reemplaza la elección privada inicial y autoriza actualizar las referencias documentales correspondientes.
- Conversar en español y mantener explicaciones claras. Consultar una decisión importante por vez y aprovechar las respuestas ya registradas.

## Fuente de verdad

- `docs/REQUIREMENTS.md`: alcance, decisiones del usuario y criterios de aceptación.
- `docs/ARCHITECTURE.md`: diseño técnico, datos, acceso y respaldo.
- `docs/PLAN.md`: etapas, dependencias, pruebas y cuestiones pendientes.
- Distinguir siempre lo aprobado, las propuestas pendientes y lo implementado. Consultar el registro de ejecución en `docs/PLAN.md` para el estado actual.
- Si cambia una decisión, conservar su antecedente y actualizar las referencias afectadas. No ampliar el alcance por costumbre.

## Restricciones aprobadas

- Android nativo: Kotlin, Jetpack Compose, Room y WorkManager. Android 11 como mínimo; distribución mediante APK firmado y actualizaciones manuales.
- Uso personal, sin servidor propio. Google Drive es el destino del respaldo.
- Organización por temáticas con notas presentadas como mensajes de WhatsApp e identidad visual propia.
- Texto, enlaces, imágenes y documentos; funcionamiento sin conexión y compartir en ambos sentidos.
- Huella con alternativa de credencial de bloqueo del dispositivo. No crear un PIN separado para la app ni una clave de recuperación.
- No agregar audios, papelera, edición interna de documentos ni cifrado adicional con clave de recuperación.
- No reutilizar como hechos las versiones de dependencias de ejemplos de documentación: verificar estabilidad y compatibilidad al preparar la implementación.

## Criterios para trabajo futuro

- Revisar el alcance autorizado antes de escribir código o cambiar configuraciones.
- Conservar datos ante errores de importación, respaldo, restauración y actualización.
- No retirar el respaldo anterior antes de verificar el nuevo. No reemplazar datos locales antes de confirmar y validar una restauración.
- No incluir datos personales, tokens ni claves de firma en código, repositorio o registros de diagnóstico.
- Mantener los permisos de archivos y Drive limitados a lo necesario. Tratar archivos recibidos y respaldos como entradas no confiables.
- Probar lógica y persistencia con pruebas adecuadas, interfaz mediante pruebas Android y flujos del sistema en dispositivo. Playwright no es la herramienta principal para esta app nativa.
- Registrar pruebas propuestas, ejecutadas y resultados; no afirmar que algo funciona sin evidencia.
- Git: la subida inicial a `app-notas` está autorizada. Para cambios posteriores, respetar el alcance de la instrucción vigente.
